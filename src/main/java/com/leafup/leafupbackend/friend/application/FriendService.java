package com.leafup.leafupbackend.friend.application;

import com.leafup.leafupbackend.friend.api.dto.request.FriendReqDto;
import com.leafup.leafupbackend.friend.api.dto.response.FriendResDto;
import com.leafup.leafupbackend.friend.api.dto.response.FriendsResDto;
import com.leafup.leafupbackend.friend.api.dto.response.FriendshipResDto;
import com.leafup.leafupbackend.friend.api.dto.response.FriendshipsResDto;
import com.leafup.leafupbackend.friend.domain.Friendship;
import com.leafup.leafupbackend.friend.domain.FriendshipStatus;
import com.leafup.leafupbackend.friend.domain.repository.FriendshipRepository;
import com.leafup.leafupbackend.friend.exception.FriendNotFoundException;
import com.leafup.leafupbackend.friend.exception.FriendshipAlreadyExistsException;
import com.leafup.leafupbackend.friend.exception.FriendshipNotFoundException;
import com.leafup.leafupbackend.friend.exception.InvalidFriendRequestException;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.MemberAvatarRepository;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;
    private final MemberAvatarRepository memberAvatarRepository;

    public FriendResDto getSearchMember(String nicknameWithCode) {
        String[] parts = nicknameWithCode.split("#");

        if (parts.length != 2) {
            throw new InvalidFriendRequestException("닉네임#코드 형식이 올바르지 않습니다.");
        }

        String nickname = parts[0];
        String code = parts[1];

        Member friend = memberRepository.findByNicknameAndCode(nickname, code).orElseThrow(MemberNotFoundException::new);

        String avatarUrl = memberAvatarRepository.findEquippedByMemberWithAvatar(friend)
                .map(memberAvatar -> memberAvatar.getAvatar().getAvatarUrl())
                .orElse(null);

        return FriendResDto.of(friend, avatarUrl, List.of());
    }

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

    public FriendshipsResDto getFriendRequests(String email) {
        Member receiver = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        List<Friendship> friendships = friendshipRepository.findByReceiverAndStatus(receiver, FriendshipStatus.PENDING);

        List<FriendshipResDto> friendResDtos = friendships.stream()
                .map(friendship -> {
                    Member requester = friendship.getRequester();
                    return FriendshipResDto.builder()
                            .friendshipId(friendship.getId())
                            .nickname(requester.getNickname() + "#" + requester.getCode())
                            .picture(requester.getPicture())
                            .lastAccessedAt(requester.getUpdatedAt()).build();
                })
                .toList();

        return FriendshipsResDto.from(friendResDtos);
    }

    public FriendsResDto getFriends(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        List<Friendship> friendships = friendshipRepository.findAcceptedFriends(member);

        List<FriendResDto> friendResDtos = friendships.stream()
                .map(friendship -> {
                    Member friend =
                            friendship.getRequester().equals(member) ? friendship.getReceiver() : friendship.getRequester();
                    return FriendResDto.builder()
                            .memberId(friend.getId())
                            .nickname(friend.getNickname() + "#" + friend.getCode())
                            .picture(friend.getPicture())
                            .lastAccessedAt(friend.getUpdatedAt())
                            .build();
                })
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

    public FriendResDto getFriendDetails(String email, Long friendId) {
        Member currentUser = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        Member friend = memberRepository.findMemberWithDetailsById(friendId).orElseThrow(MemberNotFoundException::new);

        friendshipRepository.findAcceptedFriendshipBetween(currentUser, friend).orElseThrow(FriendNotFoundException::new);

        String avatarUrl = memberAvatarRepository.findEquippedByMemberWithAvatar(friend)
                .map(memberAvatar -> memberAvatar.getAvatar().getAvatarUrl())
                .orElse(null);

        List<String> achievements = friend.getMemberAchievements().stream()
                .map(memberAchievement -> memberAchievement.getAchievement().getName())
                .collect(Collectors.toList());

        return FriendResDto.of(friend, avatarUrl, achievements);
    }

}
