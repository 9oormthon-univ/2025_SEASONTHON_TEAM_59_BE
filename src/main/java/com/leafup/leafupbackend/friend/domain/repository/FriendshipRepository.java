package com.leafup.leafupbackend.friend.domain.repository;

import com.leafup.leafupbackend.friend.domain.Friendship;
import com.leafup.leafupbackend.friend.domain.FriendshipStatus;
import com.leafup.leafupbackend.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Optional<Friendship> findByRequesterAndReceiver(Member requester, Member receiver);

    List<Friendship> findByReceiverAndStatus(Member receiver, FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.requester = :member OR f.receiver = :member) AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriends(@Param("member") Member member);

}