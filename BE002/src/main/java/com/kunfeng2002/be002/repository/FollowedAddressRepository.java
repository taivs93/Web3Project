package com.kunfeng2002.be002.repository;

import com.kunfeng2002.be002.entity.FollowedAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowedAddressRepository extends JpaRepository<FollowedAddress, Long> {
    boolean existsByAddressAndNetwork(String address, String network);
    Optional<FollowedAddress> findByAddressAndNetwork(String address, String network);
}
