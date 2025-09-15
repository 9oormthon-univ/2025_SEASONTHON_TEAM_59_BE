package com.leafup.leafupbackend.member.domain.repository;

import com.leafup.leafupbackend.member.domain.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
}
