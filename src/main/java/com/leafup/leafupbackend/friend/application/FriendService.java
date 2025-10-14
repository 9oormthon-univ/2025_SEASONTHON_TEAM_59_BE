package com.leafup.leafupbackend.friend.application;

import com.leafup.leafupbackend.friend.api.dto.request.FriendReqDto;
import com.leafup.leafupbackend.friend.api.dto.response.FriendResDto;
import com.leafup.leafupbackend.friend.api.dto.response.FriendsResDto;
import com.leafup.leafupbackend.friend.domain.Friendship;
import com.leafup.leafupbackend.friend.domain.FriendshipStatus;
import com.leafup.leafupbackend.friend.domain.repository.FriendshipRepository;
import com.leafup.leafupbackend.friend.exception.FriendshipAlreadyExistsException;
import com.leafup.leafupbackend.friend.exception.FriendshipNotFoundException;
import com.leafup.leafupbackend.friend.exception.InvalidFriendRequestException;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;

    @Transactional
    public void sendFriendRequest(String email, FriendReqDto friendReqDto) {
        Member requester = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        String[] parts = friendReqDto.nicknameWithCode().split("#");
        String nickname = parts[0];
        String code = parts[1];

        Member receiver = memberRepository.findByNicknameAndCode(nickname, code).orElseThrow(MemberNotFoundException::new);

        friendshipRepository.findByRequesterAndReceiver(requester, receiver)
                .ifPresent(f -> {
                    throw new FriendshipAlreadyExistsException();
                });
        friendshipRepository.findByRequesterAndReceiver(receiver, requester)
                .ifPresent(f -> {
                    throw new FriendshipAlreadyExistsException();
                });

        Friendship friendship = Friendship.builder()
                .requester(requester)
                .receiver(receiver)
                .build();

        friendshipRepository.save(friendship);
    }

    public FriendsResDto getFriendRequests(String email) {
        Member receiver = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        List<Friendship> friendships = friendshipRepository.findByReceiverAndStatus(receiver, FriendshipStatus.PENDING);

        List<FriendResDto> friendResDtos = friendships.stream()
                .map(friendship -> FriendResDto.of(friendship.getId(),
                        friendship.getRequester().getNickname(),
                        friendship.getRequester().getCode(),
                        friendship.getRequester().getPicture(),
                        friendship.getRequester().getUpdatedAt())
                )
                .toList();

        return FriendsResDto.from(friendResDtos);
    }

    public FriendsResDto getFriends(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        List<Friendship> friendships = friendshipRepository.findAcceptedFriends(member);

        List<FriendResDto> friendResDtos = friendships.stream()
                .map(friendship -> {
                            Member friend =
                                    friendship.getRequester().equals(member) ? friendship.getReceiver() : friendship.getRequester();
                            return FriendResDto.of(friend.getId(),
                                    friend.getNickname(),
                                    friend.getCode(),
                                    friend.getPicture(),
                                    friend.getUpdatedAt());
                        }
                )
                .toList();

        return FriendsResDto.from(friendResDtos);
    }

    @Transactional
    public void respondToFriendRequest(String email, Long friendshipId, FriendshipStatus status) {
        Member receiver = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Friendship friendship = friendshipRepository.findById(friendshipId).orElseThrow(FriendshipNotFoundException::new);

        if (!friendship.getReceiver().equals(receiver)) {
            throw new InvalidFriendRequestException("해당 친구 요청을 처리할 권한이 없습니다.");
        }

        if (status == FriendshipStatus.ACCEPTED) {
            friendship.accept();
        } else if (status == FriendshipStatus.REJECTED) {
            friendship.reject();
        }
    }

}
