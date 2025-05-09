package id.ac.ui.cs.advprog.tableservicerizzerve.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=Zm9vYmFyZm9vYmFyZm9vYmFyZm9vYmFyZm9vYmFyZm9vYmFyZm9vYmY=",
        "jwt.expiration=3600000"
})
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private UserDetails user;

    @BeforeEach
    void setUp() {
        user = User.withUsername("testuser")
                .password("")
                .roles("ADMIN")
                .build();
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extracted = jwtService.extractUsername(token);
        assertEquals("testuser", extracted);
    }

    @Test
    void testIsTokenValid() {
        String token = jwtService.generateToken(user);
        assertTrue(jwtService.isTokenValid(token, user));

        UserDetails other = User.withUsername("other")
                .password("")
                .roles("ADMIN")
                .build();
        assertFalse(jwtService.isTokenValid(token, other));
    }

    @Test
    void testExtractRoleWhenNoRoleClaim() {
        String token = jwtService.generateToken(user);
        assertNull(jwtService.extractRole(token));
    }

}