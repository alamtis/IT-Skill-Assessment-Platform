package com.skillassessment.platform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A custom Spring Security filter that executes once per request.
 * This filter is responsible for authenticating users by validating the JWT
 * present in the request's Authorization header.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    /**
     * The main filtering logic. This method is called by the servlet container for each request.
     *
     * @param request     The incoming HTTP request.
     * @param response    The outgoing HTTP response.
     * @param filterChain The chain of filters to pass the request along to.
     * @throws ServletException If an error occurs during servlet processing.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Extract the JWT token from the HTTP request
            String token = getTokenFromRequest(request);

            // 2. Validate the token
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // 3. Get username from the token
                String username = jwtTokenProvider.getUsername(token);

                // 4. Load the user associated with the token
                // It's important to load from the database to ensure the user still exists, is not disabled, etc.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Create an authentication object
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credentials are not needed as the user is already authenticated by the token
                        userDetails.getAuthorities()
                );

                // 6. Set additional details for the authentication object
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. Set the authentication object in Spring Security's context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception ex) {
            // In case of any error, we log it and let the request continue.
            // If authentication is not set, access will be denied later by Spring Security.
            log.error("Could not set user authentication in security context", ex);
        }

        // 8. Continue the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * A helper method to extract the JWT from the "Authorization" header.
     *
     * @param request The HTTP request.
     * @return The JWT string if present and correctly formatted, otherwise null.
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // The header should look like: "Authorization: Bearer <token>"
        String bearerToken = request.getHeader("Authorization");

        // Check if the header exists and starts with "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Return the actual token part (substring after "Bearer ")
            return bearerToken.substring(7);
        }

        return null;
    }
}