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

    // 토큰 유효성 검사 메서드 - 만료되었는지를 확인
    public static boolean isExpired(String token, String key) {
        Date expiredDate = extractClaims(token, key).getExpiration();
        return expiredDate.before(new Date()); // 현재 시간으로 생성되기 때문에 만료되었는지 확인이 가능함
    }

    // 유효성 검사를 위한 클래임을 가져오는 메서드
    public static Claims extractClaims(String token, String key) {
        return Jwts.parserBuilder().setSigningKey(getKey(key))
                .build().parseClaimsJws(token).getBody(); // 파싱해서 가지고 오기
    }

    // 유저이름 가져오는 메서드
    public static String getUserName(String token, String key) {
        return extractClaims(token, key).get("userName", String.class); // 스트링 타입인 것까지 표시해줘야 에러 안 남
    }
}
