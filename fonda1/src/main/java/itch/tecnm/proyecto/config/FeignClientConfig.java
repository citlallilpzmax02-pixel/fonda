package itch.tecnm.proyecto.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (RequestTemplate template) -> {
            try {
                ServletRequestAttributes attrs =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attrs != null) {
                    String token = attrs.getRequest().getHeader("Authorization");

                    if (token != null && !token.isEmpty()) {
                        template.header("Authorization", token);
                        System.out.println("🔐 TOKEN reenviado a microservicio -> " + token);
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠ No se pudo reenviar el token a Feign: " + e.getMessage());
            }
        };
    }
}
