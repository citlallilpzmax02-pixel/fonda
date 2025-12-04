package itch.tecnm.proyecto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import itch.tecnm.proyecto.security.JwtAuthenticationFilter;
import itch.tecnm.proyecto.security.JwtUtil;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	//MS FONDA1
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil);

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
            		
                // ⭐ PRODUCTO — público para el home (solo GET)
                .requestMatchers(HttpMethod.GET, "/api/producto/**").permitAll()

                // ⭐ Administrador y Supervisor pueden crear/editar/eliminar productos
                .requestMatchers("/api/producto/**")
                    .hasAnyAuthority("administrador", "supervisor")

                // ⭐ TIPO PRODUCTO — supervisor SOLO GET
                .requestMatchers(HttpMethod.GET, "/api/tipoProducto/**")
                    .hasAnyAuthority("administrador", "supervisor")

                // ⭐ TIPO PRODUCTO — solo administrador puede crear/editar/eliminar
                .requestMatchers(HttpMethod.POST, "/api/tipoProducto/**")
                    .hasAuthority("administrador")
                .requestMatchers(HttpMethod.PUT, "/api/tipoProducto/**")
                    .hasAuthority("administrador")
                .requestMatchers(HttpMethod.DELETE, "/api/tipoProducto/**")
                    .hasAuthority("administrador")
                    
                 // NUEVO: PERMISOS PARA VENTAS
                 // Mesero, Cajero y Administrador pueden consultar
                    .requestMatchers(HttpMethod.GET, "/api/venta/**")
                        .hasAnyAuthority("administrador", "cajero", "mesero")

                    // Mesero, Cajero y Administrador pueden actualizar
                    .requestMatchers(HttpMethod.PUT, "/api/venta/**")
                        .hasAnyAuthority("administrador", "cajero", "mesero")

                    // Solo Cajero y Administrador pueden crear
                    .requestMatchers(HttpMethod.POST, "/api/venta/**")
                        .hasAnyAuthority("administrador", "cajero")

                    // Solo Cajero y Administrador pueden borrar si lo usas
                    .requestMatchers(HttpMethod.DELETE, "/api/venta/**")
                        .hasAnyAuthority("administrador", "cajero")

                  // NUEVO: PERMISOS PARA DETALLE VENTA
                  // ⭐ 1️⃣ PERMITIR EL TICKET (antes de validar roles)
                     .requestMatchers(HttpMethod.GET, "/api/detalleventa/ticket/**")
                         .permitAll()

                     // ⭐ 2️⃣ AHORA SÍ, los demás detalleventa
                     .requestMatchers("/api/detalleventa/**")
                         .hasAnyAuthority("administrador", "cajero","mesero")

                // ⭐ Todo lo demás requiere token
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ⭐ CORS para permitir React 3000
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(java.util.List.of("http://localhost:3000"));
        config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("*"));
        config.setExposedHeaders(java.util.List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
