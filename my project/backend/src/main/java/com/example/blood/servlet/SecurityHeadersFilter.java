package com.example.blood.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Security Headers Filter
 * Adds security headers to all HTTP responses to protect against common vulnerabilities.
 */
public class SecurityHeadersFilter extends HttpFilter {
    
    public SecurityHeadersFilter() {
        // No-argument constructor required by servlet container
    }
    
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        // Add CORS headers for cross-origin requests
        String origin = request.getHeader("Origin");
        if (origin != null && !origin.isBlank()) {
            // Echo requesting origin when credentials are used; wildcard is invalid with credentials
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Vary", "Origin");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        } else {
            // Fallback for same-origin or file:// usage where Origin may be absent
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        // Security headers to prevent common vulnerabilities
        response.addHeader("X-Content-Type-Options", "nosniff");  // Prevent MIME type sniffing
        response.addHeader("X-Frame-Options", "SAMEORIGIN");      // Clickjacking protection
        response.addHeader("X-XSS-Protection", "1; mode=block");  // XSS protection
        response.addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains"); // HSTS
        response.addHeader("Content-Security-Policy", "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'");
        response.addHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Handle OPTIONS requests for CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        chain.doFilter(request, response);
    }
}
