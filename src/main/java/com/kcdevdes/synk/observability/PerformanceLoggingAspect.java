package com.kcdevdes.synk.observability;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceLoggingAspect {

    private final long slowThresholdMs;

    public PerformanceLoggingAspect(
            @Value("${app.logging.slow-threshold-ms:500}") long slowThresholdMs
    ) {
        this.slowThresholdMs = slowThresholdMs;
    }

    @Around("execution(* com.kcdevdes.synk.service..*(..)) || execution(* com.kcdevdes.synk.repository..*(..))")
    public Object logDuration(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String className = signature.getDeclaringType().getSimpleName();
            String methodName = signature.getName();

            if (durationMs >= slowThresholdMs) {
                log.warn(
                        "event=slow_operation class={} method={} durationMs={} thresholdMs={}",
                        className, methodName, durationMs, slowThresholdMs
                );
            } else {
                log.debug(
                        "event=operation_timing class={} method={} durationMs={}",
                        className, methodName, durationMs
                );
            }
        }
    }
}
