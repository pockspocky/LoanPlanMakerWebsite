package com.example.hello.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import java.lang.management.ManagementFactory;
import java.time.Duration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
public class ScheduledRestartConfig {

    @Autowired
    private ConfigurableApplicationContext context;

    private final long startTime = System.currentTimeMillis();

    // Restart every 5 days, with initial delay of 5 days
    @Scheduled(fixedRate = 5 * 24 * 60 * 60 * 1000, initialDelay = 5 * 24 * 60 * 60 * 1000)
    public void scheduleRestart() {
        long uptime = System.currentTimeMillis() - startTime;
        Duration duration = Duration.ofMillis(uptime);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        
        log.info("Restarting the application...");
        log.info("Uptime: {} Days {} Hours {} Minutes {} Seconds", days, hours, minutes, seconds);
        
        try {
            // Set exit code to 0 for normal exit
            System.exit(SpringApplication.exit(context, (ExitCodeGenerator) () -> 0));
        } catch (Exception e) {
            log.error("Error occurred during restart", e);
        }
    }
} 