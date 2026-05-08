package com.bs.odontograma.shared.security;

import com.bs.odontograma.auth.entity.User;
import com.bs.odontograma.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email
                ));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User is disabled: " + email);
        }

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        log.debug("User loaded successfully: {} with role: {}", email, user.getRole());

        return new UserPrincipal(
                user.getId(),
                user.getTenantId(),
                user.getEmail(),
                user.getPasswordHash(),
                authorities,
                user.isActive()
        );
    }
}
