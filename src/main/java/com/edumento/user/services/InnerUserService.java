package com.edumento.user.services;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.edumento.user.domain.User;
import com.edumento.user.repo.UserRepository;

@Service
public class InnerUserService {

    private final UserRepository userRepository;

    public InnerUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean checkUserIfExistByIfElse(Long id) {
        return userRepository.existsById(id);
    }

    public <T> List<T> findAllById(List<Long> userIdList, Function<User, T> mapper) {
        return StreamSupport.stream(userRepository.findAllById(userIdList).spliterator(), false)
                .map(mapper)
                .collect(Collectors.toList());

    }

}