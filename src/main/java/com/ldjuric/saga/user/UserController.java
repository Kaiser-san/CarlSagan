package com.ldjuric.saga.user;

import com.ldjuric.saga.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Profile({"user", "all"})
@RestController
@RequestMapping("/user")
@CrossOrigin(origins="*", maxAge=3600)

public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable("username") String username) {
        String result = userService.getUser(username);
        if(!result.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(result);
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid user");
    }

    @GetMapping()
    public ResponseEntity<?> getUsers() {
        String result = userService.getUsers();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/{username}/{password}")
    public ResponseEntity<?> createUser(@PathVariable("username") String username, @PathVariable("password") String password) {
        boolean result = userService.createUser(username, password);
        if(result)
            return ResponseEntity.status(HttpStatus.OK).body("OK");
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("username exists");
    }
}
