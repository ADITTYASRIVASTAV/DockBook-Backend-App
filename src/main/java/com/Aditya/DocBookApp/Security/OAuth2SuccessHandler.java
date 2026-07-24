package com.Aditya.DocBookApp.Security;

import com.Aditya.DocBookApp.Entity.UserEntity;
import com.Aditya.DocBookApp.Enum.Role;
import com.Aditya.DocBookApp.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler
{
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String provider = authToken.getAuthorizedClientRegistrationId(); // google, github, linkedin
        OAuth2User oAuth2User = authToken.getPrincipal();
        String email = extractEmail(oAuth2User, provider);
        String name = extractName(oAuth2User, provider);

        if (email == null) {
            getRedirectStrategy().sendRedirect(request, response,
                    frontendUrl + "/oauth2/callback?error=email_not_found");
            return;
        }

        // Read requested role from cookie
        Role requestedRole = Role.PATIENT;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("oauth2_role".equals(cookie.getName())) {
                    if ("DOCTOR".equalsIgnoreCase(cookie.getValue())) {
                        requestedRole = Role.DOCTOR;
                    }
                    // Clear the cookie
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        
        final Role finalRole = requestedRole;

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setRole(finalRole);
                    newUser.setEmailVerified(true);
                    newUser.setProvider(provider.toUpperCase());
                    return userRepository.save(newUser);
                });

        String accessToken = jwtUtils.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        String targetUrl = frontendUrl + "/oauth2/callback" 
                + "?accessToken=" + accessToken 
                + "&refreshToken=" + refreshToken 
                + "&email=" + user.getEmail() 
                + "&name=" + java.net.URLEncoder.encode(user.getName(), "UTF-8") 
                + "&role=" + user.getRole().name();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String extractEmail(OAuth2User oAuth2User, String provider) {
        return oAuth2User.getAttribute("email");
    }

    private String extractName(OAuth2User oAuth2User, String provider) {
        if ("github".equals(provider)) {
            String name = oAuth2User.getAttribute("name");
            return (name != null) ? name : oAuth2User.getAttribute("login");
        }
        String name = oAuth2User.getAttribute("name");
        return (name != null) ? name : "User";
    }
}
