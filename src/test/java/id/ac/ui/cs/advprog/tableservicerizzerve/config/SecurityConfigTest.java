package id.ac.ui.cs.advprog.tableservicerizzerve.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain filterChain;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    void filterChainBeanExists() {
        assertNotNull(filterChain);
    }

    @Test
    void userDetailsServiceBeanExists() {
        assertNotNull(userDetailsService);
    }

    @Test
    void passwordEncoderBeanExists() {
        assertNotNull(passwordEncoder);
    }

    @Test
    void authenticationManagerBeanExists() {
        assertNotNull(authenticationManager);
    }

    @Test
    void userDetailsServiceReturnsAdminUser() {
        var user = userDetailsService.loadUserByUsername("alice");
        assertNotNull(user);
        assertEquals("alice", user.getUsername());
        assertTrue(user.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}