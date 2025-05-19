//package danskebank.mini_bank_system.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/actuator/**").permitAll()
//                        .anyRequest().authenticated())
//                .build();
//    }
//}
