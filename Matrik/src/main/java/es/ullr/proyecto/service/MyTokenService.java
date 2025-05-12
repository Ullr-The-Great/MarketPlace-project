package es.ullr.proyecto.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

@Service
public class MyTokenService {
    
    private final Set<String> blacklistedTokens = new HashSet<>();
    private static final long CLEANUP_INTERVAL = 30 * 60 * 1000; // 30 minutos

    public MyTokenService() {
        // Programar limpieza periódica de tokens expirados
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::cleanupExpiredTokens, 
                                    CLEANUP_INTERVAL, 
                                    CLEANUP_INTERVAL, 
                                    TimeUnit.MILLISECONDS);
    }

    public void invalidateToken(String token) {
        if (token != null) {
            blacklistedTokens.add(token);
        }
    }

    public synchronized boolean isTokenValid(String token) {
        return token != null && !blacklistedTokens.contains(token);
    }

    private synchronized void cleanupExpiredTokens() {
        // Aquí podrías añadir lógica para limpiar tokens muy antiguos
        // si quisieras mantener el Set bajo control a largo plazo
        // Por ahora simplemente limpiamos todos (en producción usarías Redis con TTL)
        blacklistedTokens.clear();
    }
}