package com.example.task_service.task_service.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomInfoContributor implements InfoContributor {
    private static final int MEGABYTE = 1024 * 1024;

    @Override
    public void contribute(Info.Builder builder) {
        Runtime runtime = Runtime.getRuntime();

        long freeMemory = runtime.freeMemory() / MEGABYTE;
        long totalMemory = runtime.totalMemory() / MEGABYTE;
        long maxMemory = runtime.maxMemory() / MEGABYTE;
        int availableProcessors = runtime.availableProcessors();

        builder.withDetail("system", Map.of(
                "freeMemoryMB", freeMemory,
                "totalMemoryMB", totalMemory,
                "maxMemoryMB", maxMemory,
                "availableProcessors", availableProcessors
        ));
    }
}
