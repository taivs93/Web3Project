package com.kunfeng2002.be002.service;

import com.kunfeng2002.be002.dto.request.FollowRequest;
import com.kunfeng2002.be002.entity.FollowedAddress;
import com.kunfeng2002.be002.repository.FollowedAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowedAddressRepository repository;

    public void follow(FollowRequest request) {
        if (repository.existsByAddressAndNetwork(request.getAddress().toLowerCase(), request.getNetwork().toUpperCase())) {
            return; 
        }

        FollowedAddress fa = FollowedAddress.builder()
                .address(request.getAddress().toLowerCase())
                .network(request.getNetwork().toUpperCase())
                .build();

        repository.save(fa);
    }
}
