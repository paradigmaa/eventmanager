package school.sorokin.eventmanager.users.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenManager {

    private  final SecretKey key;


    private final long expiration_time;


    public JwtTokenManager(@Value("${jwt.secretKey}") String keyString,
                           @Value("${jwt.lifetime}") long expiration_time) {
        this.key = Keys.hmacShaKeyFor(keyString.getBytes());
        this.expiration_time = expiration_time;
    }


    public String generateToken(String login){
        return Jwts
                .builder()
                .subject(login)
                .signWith(key)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration_time))
                .compact();

    };


    public String getLoginFromToken(String jwt){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseEncryptedClaims(jwt)
                .getPayload()
                .getSubject();
    }
}
