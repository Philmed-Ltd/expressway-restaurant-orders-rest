package io.security;

import io.httpaccesslog.service.HttpAccessLogService;
import io.lib.service.SystemConfig;
import io.user.entity.User;
import io.user.entity.UserType;
import io.user.service.UserAuthService;
import io.userauthentication.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration  {

    private AuthenticationExceptionHandler authExceptionHandler;
    private Map<UserType, UserAuthService<?, ?>> userServiceMap;
    private JwtService jwtService;
    private HttpAccessLogService httpAccessLogService;

    private final String[] permittedUrls = {
            SystemConfig.INTERNAL_USER_BASE_URL + "/verification-code/request",
            SystemConfig.INTERNAL_USER_BASE_URL + "/verification-code/resend/**",
            SystemConfig.INTERNAL_USER_BASE_URL + "/auth/reset-password/**",
            SystemConfig.INTERNAL_USER_BASE_URL + "/auth/init-login",
            SystemConfig.INTERNAL_USER_BASE_URL + "/auth/complete-login/**",
            SystemConfig.INTERNAL_USER_BASE_URL + "/auth/update-public-key",

            SystemConfig.CUSTOMER_USER_BASE_URL + "/verification-code/request",
            SystemConfig.CUSTOMER_USER_BASE_URL + "/verification-code/resend/**",
            SystemConfig.CUSTOMER_USER_BASE_URL + "/auth/register",
            SystemConfig.CUSTOMER_USER_BASE_URL + "/auth/reset-password/**",
            SystemConfig.CUSTOMER_USER_BASE_URL + "/auth/init-login",
            SystemConfig.CUSTOMER_USER_BASE_URL + "/auth/complete-login/**",

            SystemConfig.CUSTOMER_USER_BASE_URL + "/product-items/list/**",
            SystemConfig.CUSTOMER_USER_BASE_URL + "/product-categories/list/**",
            "/saf/c2b/register-payment",
            "/saf/c2b/transaction-status/result",
            "/saf/c2b/transaction-status/timeout",

            "/saf/stk-callback/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {

        var authFilter= new AuthenticationFilter(
            jwtService,
            userServiceMap,
            httpAccessLogService
        );

        http.cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(matcherReg -> matcherReg.requestMatchers(HttpMethod.OPTIONS).permitAll())
            .authorizeHttpRequests(matcherReg -> matcherReg.requestMatchers(permittedUrls).permitAll())

            .authorizeHttpRequests(matcherReg -> matcherReg.requestMatchers(SystemConfig.INTERNAL_USER_BASE_URL +"/**").hasAuthority(UserType.INTERNAL_USER.toString()))
            .authorizeHttpRequests(matcherReg -> matcherReg.requestMatchers(SystemConfig.CUSTOMER_USER_BASE_URL +"/**").hasAuthority(UserType.CUSTOMER_USER.toString()))

            .authorizeHttpRequests(matcherReg -> matcherReg.anyRequest().authenticated())
            .exceptionHandling(expHandler -> expHandler.authenticationEntryPoint(authExceptionHandler))
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilter(authFilter);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowCredentials(true);
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setMaxAge(3600L);
        var configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        return configSource;
    }

    @Autowired
    public void setAuthExceptionHandler(AuthenticationExceptionHandler authExceptionHandler) {
        this.authExceptionHandler = authExceptionHandler;
    }

    @Autowired
    public  <U extends User> void setUserServices(List<UserAuthService<U, ?>> userServices) {
        if(userServices != null){
            this.userServiceMap = new HashMap<>();
            userServices.forEach(userService -> userServiceMap.put(userService.getUserType(), userService));
        }
    }

    @Autowired
    public void setJwtService(JwtService service) {
        this.jwtService = service;
    }

    @Autowired
    public void setHttpAccessLogService(HttpAccessLogService service) {
        this.httpAccessLogService = service;
    }
}
