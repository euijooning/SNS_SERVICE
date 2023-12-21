package my.sns.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.sns.dto.UserDto;
import my.sns.service.UserService;
import my.sns.util.JwtTokenUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String key;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더 가져오기
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) { //에러인 경우
            log.error("Error occurs while getting header. Header is null or invalid");
            filterChain.doFilter(request, response);
            return;
        }

        // 앞에 Bearer 부분은 빼고 가져오기
        try {
            final String token = header.split(" ")[1].trim();

            // check token is valid
            if (JwtTokenUtils.isExpired(token, key)) {
                log.error("Key is expired");
                filterChain.doFilter(request, response);
                return;
            }

            // get Username from token
            String userName = JwtTokenUtils.getUserName(token, key);
            // check the userName is valid
            UserDto user = userService.loadUserByUserName(userName);

            UsernamePasswordAuthenticationToken authentication
                    = new UsernamePasswordAuthenticationToken(user, null , user.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (RuntimeException e) {
            log.error("Error occurs while validating. {}", e.toString());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
