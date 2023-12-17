package my.sns.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtTokenUtils {

    public static String generateToken(String userName, String key, Long expiredTimeMs) {
        // 어떤 유저인지 확인
        Claims claims = Jwts.claims();
        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발급시간
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs)) // 만료시간
                .signWith(getKey(key), SignatureAlgorithm.HS256) // 암호화 방식
                .compact();
    }

    // 키 만드는 메서드
    private static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
