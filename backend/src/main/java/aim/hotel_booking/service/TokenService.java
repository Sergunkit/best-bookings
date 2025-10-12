package aim.hotel_booking.service;

import aim.hotel_booking.entity.UserEntity;
import aim.hotel_booking.repository.UserRepository;
import aim.hotel_booking.security.JwtService;
import org.openapitools.model.AuthInfo;
import org.openapitools.model.TokenInfo;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TokenService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public TokenService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokenInfo createToken(AuthInfo authInfo) {
        UserEntity user = userRepository.findByEmail(authInfo.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(authInfo.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(token);
        return tokenInfo;
    }

    public boolean isValidToken(String token) {
        try {
            String email = jwtService.extractUsername(token);
            return userRepository.findByEmail(email).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    public UserEntity getUserByToken(String token) {
        String email = jwtService.extractUsername(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));
    }

    public void invalidateToken(String token) {
        // JWT токены не требуют инвалидации, так как они имеют срок действия
    }
}
