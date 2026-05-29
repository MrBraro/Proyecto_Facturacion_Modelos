package com.modelosgr86e1eq6.proyectofacturacion.notifications.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
 
import java.util.concurrent.Executor;
 
/**
 * Habilita el procesamiento asíncrono para el módulo de notificaciones.
 *
 * Al estar anotado con @Async, el NotificationEventListener no bloquea
 * el hilo del módulo publicador (FacturaService, PagoService).
 * Si el envío de email o SMS falla, no afecta la transacción principal.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
 
    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("notification-");
        executor.initialize();
        return executor;
    }
}