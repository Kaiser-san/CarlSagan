package com.ldjuric.saga.user;

import com.ldjuric.saga.interfaces.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"user", "all"})
@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;

    public String getUser(String username) {
        UserEntity user = this.userRepository.findByUsername(username);
        return user != null ? user.getName() : "";
    }

    @Override
    public boolean validateUser(String username, String password) {
        UserEntity user = this.userRepository.findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }
}
