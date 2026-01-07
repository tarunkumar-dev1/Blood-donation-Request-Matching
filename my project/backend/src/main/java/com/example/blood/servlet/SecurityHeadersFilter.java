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
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Max-Age", "3600");
        
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
