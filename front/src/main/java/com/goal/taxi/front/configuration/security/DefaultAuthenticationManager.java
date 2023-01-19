package com.goal.taxi.front.configuration.security;

import com.goal.taxi.front.dao.repository.ClientRepository;
import com.goal.taxi.front.exception.UnauthorizedException;
import com.goal.taxi.front.service.JwtTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class DefaultAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtTokenService jwtTokenService;
    private final ClientRepository clientRepository;

    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) throws AuthenticationException {
        final var authToken = authentication.getCredentials().toString();

        if (!jwtTokenService.validate(authToken)) {
            return Mono.error(() -> new UnauthorizedException("Token not valid"));
        }

        return getAuthenticationToken(authToken);
    }

    private Mono<Authentication> getAuthenticationToken(final String token) {
        return getUserDetails(token)
                .map(this::buildAuthenticationToken);
    }

    private Mono<UserDetails> getUserDetails(final String token) {
        return clientRepository
                .findById(jwtTokenService.getClientId(token))
                .map(r -> r);
    }

    private Authentication buildAuthenticationToken(final UserDetails userDetails) {
        final var authorities = userDetails.getAuthorities().stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .toList();

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authorities
        );
    }
}
