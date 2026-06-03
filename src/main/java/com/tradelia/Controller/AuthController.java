package com.tradelia.Controller;

import com.tradelia.Model.User;
import com.tradelia.Service.DemoModeService;
import com.tradelia.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final DemoModeService demoModeService;

    public AuthController(UserService userService, DemoModeService demoModeService) {
        this.userService = userService;
        this.demoModeService = demoModeService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        demoModeService.ensureWriteAllowed();
        String status = userService.register(user.getUsername(), user.getEmail(), user.getPassword());
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        String token = userService.login(user.getEmail(), user.getPassword());
        return ResponseEntity.ok(Map.of("message", "Login exitoso", "token", token));
    }
}
