package com.example.auth_project.filter;

import com.example.auth_project.entity.Role;
import com.example.auth_project.entity.User;
import com.example.auth_project.repo.UserRepo;
import com.example.auth_project.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Extract JWT from Authorization header
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7); // Remove "Bearer " prefix
        
        try {
            // 2. Extract user info from token
            String userId = jwtUtil.extractUserId(jwt);
            
            // 3. Check if user is not already authenticated
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // 4. Validate token
                if (jwtUtil.validateTokenByUserId(jwt, userId)) {
                    
                    // 5. Load user from database
                    User user = userRepo.findById(UUID.fromString(userId)).orElse(null);
                    
                    if (user != null) {
                        // 6. Create authentication object
                        UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(
                                user, 
                                null, 
                                getAuthorities(user.getRole())
                            );
                        
                        // 7. Set authentication in security context
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("User {} authenticated via JWT", user.getUsername());
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
        }

        // 8. Continue filter chain
        filterChain.doFilter(request, response);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}