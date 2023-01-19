package com.goal.taxi.front.configuration;

import com.goal.taxi.front.dao.entity.Client;
import com.goal.taxi.front.dao.entity.Role;
import com.goal.taxi.front.dao.entity.Roles;
import com.goal.taxi.front.dao.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientInitializationConfiguration {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.clients.default.id}")
    private String defaultClientId;
    @Value("${app.clients.default.password}")
    private String defaultClientPassword;

    @EventListener
    public Mono<Void> onApplicationEvent(final ContextRefreshedEvent event) {
        final var client = buildClient();

        return clientRepository.findById(client.getId())
                .flatMap(existingClient -> {
                    log.info("Client exists, updating...");
                    existingClient.setPassword(passwordEncoder.encode(defaultClientPassword));
                    return clientRepository.save(existingClient);
                })
                .switchIfEmpty(clientRepository.save(client))
                .then();
    }

    private Client buildClient() {
        return new Client()
                .setId(defaultClientId)
                .setPassword(passwordEncoder.encode(defaultClientPassword))
                .setAuthorities(List.of(new Role(Roles.DEFAULT.name())))
                .setEnabled(true)
                .setAccountExpired(false)
                .setAccountLocked(false)
                .setCredentialsExpired(false);
    }
}
