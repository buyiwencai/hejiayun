package com.hejiayun.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hejiayun.model.common.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // GET请求放行
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // POST/PUT/DELETE写操作，校验角色
        String role = request.getHeader("X-User-Role");
        if ("admin".equals(role)) {
            return true;
        }

        // 非admin，返回403
        response.setStatus(403);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(CommonResult.error(403, "无权限操作")));
        return false;
    }
}
