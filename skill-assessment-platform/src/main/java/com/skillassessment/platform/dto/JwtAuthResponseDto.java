package com.skillassessment.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for the JWT authentication response.
 * This object is returned to the client upon successful login.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponseDto {

    /**
     * The JSON Web Token (JWT) that the client must include in the
     * Authorization header for subsequent protected API requests.
     */
    private String accessToken;

    /**
     * The type of the token. By default, and as a standard, this is "Bearer".
     * The client should prepend this value followed by a space to the
     * access token in the Authorization header.
     * Example: Authorization: Bearer <accessToken>
     */
    private String tokenType = "Bearer";

}