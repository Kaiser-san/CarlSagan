package com.ldjuric.saga.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public String getUser(String username) {
        UserEntity user = this.userRepository.findByUsername(username);
        return user != null ? user.getName() : "";
    }

    public boolean validateUser(String username, String password) {
        UserEntity user = this.userRepository.findByUsername(username);
        return user != null ? user.getPassword() == password : false;
    }
}
