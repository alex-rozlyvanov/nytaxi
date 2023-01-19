package com.goal.taxi.front.service;

import com.goal.taxi.front.dao.entity.Client;
import com.goal.taxi.front.dao.repository.ClientRepository;
import com.goal.taxi.front.dto.LoginRequest;
import com.goal.taxi.front.dto.LoginResponse;
import com.goal.taxi.front.exception.ClientNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class LoginService {

    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Mono<LoginResponse> login(final LoginRequest request) throws BadCredentialsException {
        log.info("Login '{}' ", request.getId());
        return authenticate(request)
                .flatMap(this::buildLoginResponse);
    }

    private Mono<Client> authenticate(final LoginRequest request) {
        return clientRepository.findById(request.getId())
                .map(userDetails -> validatePassword(request, userDetails))
                .switchIfEmpty(Mono.error(() -> new ClientNotFoundException("Client not found by id '%s'".formatted(request.getId()))));
    }

    private Client validatePassword(final LoginRequest request, final UserDetails userDetails) {
        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            log.debug("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException("Bad credentials");
        }

        return (Client) userDetails;
    }

    private Mono<LoginResponse> buildLoginResponse(final Client user) {
        final var accessToken = jwtTokenService.generateAccessToken(user);
        final var refreshTokenMono = refreshTokenService.createRefreshToken(user);

        return refreshTokenMono.map(refreshToken -> buildLoginResponse(accessToken, refreshToken));
    }

    private LoginResponse buildLoginResponse(final String accessToken,
                                             final String refreshToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
