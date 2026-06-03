package com.tradelia.Service;

import com.tradelia.Dto.UserProfileDto;
import com.tradelia.Model.User;
import com.tradelia.Repository.UserRepository;
import com.tradelia.exception.ApiException;
import com.tradelia.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String register(String username, String email, String password) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No se pudo completar el registro");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setCreated_at(LocalDateTime.now());
        userRepository.save(user);

        return "usuario creado";
    }

    public UserProfileDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "usuario no encontrado"));
        return UserProfileDto.from(user);
    }

    public String login(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return jwtService.generateToken(userDetails);
        } catch (org.springframework.security.authentication.BadCredentialsException | DisabledException e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "credenciales incorrectas");
        }
    }
}
