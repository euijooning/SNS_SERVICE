package my.sns.config;

import my.sns.common.ResultResponse;
import my.sns.exception.CustomErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(CustomErrorCode.INVALID_TOKEN.getStatus().value()); // Int이므로 value값까지 추출
        response.getWriter().write(ResultResponse.error(CustomErrorCode.INVALID_TOKEN.name()).toStream());
    }
}
