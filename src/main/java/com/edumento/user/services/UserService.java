package com.edumento.user.services;

import org.springframework.stereotype.Service;
import com.edumento.core.exception.NotFoundException;
import com.edumento.user.domain.User;
import com.edumento.user.repo.UserRepository;
import reactor.core.publisher.Mono;

@Service
public class UserService {

	private final UserRepository userRepo;

	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	// TODO: add Regiester method
	// TODO: add Password Hasher
	// TODO: add method to check userName and Passwrod

	public Mono<User> getUserByUserName(String username) {
		return Mono.create(emitter -> userRepo.findOneByUserNameAndDeletedFalse(username)
				.ifPresentOrElse((user) -> emitter.success(user),
						() -> emitter.error(new NotFoundException())));
	}
}
