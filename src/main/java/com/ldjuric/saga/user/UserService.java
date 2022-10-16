package com.ldjuric.saga.user;

import com.ldjuric.saga.interfaces.UserServiceInterface;
import com.ldjuric.saga.order.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Profile({"user", "all"})
@Service
public class UserService implements UserServiceInterface {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMQSender sender;

    public String getUser(String username) {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        return user.isPresent() ? user.get().toString() : "";
    }

    public String getUsers() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterable<UserEntity> users = userRepository.findAll();
        for (UserEntity user : users) {
            stringBuilder.append(user.toString());
            stringBuilder.append(System.getProperty("line.separator"));
        }
        return stringBuilder.toString();
    }

    public boolean createUser(String username, String password) {
        Optional<UserEntity> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return false;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(password);
        userRepository.save(userEntity);
        return true;
    }

    @Override
    public void validateUserOrchestration(Integer orderID, String username, String password) {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        boolean valid = user.isPresent() && user.get().getPassword().equals(password);
        sender.sendOrchestration(valid, orderID, username);
    }

    public void validateUserChoreography(Integer orderID, String username, String password) {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        boolean valid = user.isPresent() && user.get().getPassword().equals(password);
        sender.sendChoreography(valid, orderID, username);
    }
}
