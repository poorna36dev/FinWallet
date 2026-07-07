package com.poorna.fintech.Aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.LinkedHashMap;
import java.lang.reflect.Field;

@Slf4j
@Aspect
@Component
public class LoggingAspect {


    @Around("execution(* com.poorna.fintech.controller..*(..)) ")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logging(joinPoint);
    }
    @Around("execution(* com.poorna.fintech.service..*(..)) ")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logging(joinPoint);
    }
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().toShortString();

        
        log.info("Entering {} with arguments {}",
        methodName,
        maskArguments(joinPoint.getArgs()));

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            log.info("Exiting {} with result {} ({} ms)",
                    methodName,
                    result,
                    System.currentTimeMillis() - start);

            return result;

        } catch (Throwable ex) {

            log.error("Exception in {}: {}",
                    methodName,
                    ex.getMessage(),
                    ex);

            throw ex;
        }
    }
    private Object[] maskArguments(Object[] args) {

        Object[] masked = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            masked[i] = maskObject(args[i]);
        }

        return masked;
    }
    private Object maskObject(Object object) throws SecurityException{

        if (object == null) {
            return null;
        }

        Class<?> clazz = object.getClass();

        // Primitive wrappers & String
        if (clazz.isPrimitive()
                || clazz.equals(String.class)
                || Number.class.isAssignableFrom(clazz)
                || clazz.equals(Boolean.class)
                || clazz.isEnum()) {
            return object;
        }

        Map<String, Object> values = new LinkedHashMap<>();

        for (Field field : clazz.getDeclaredFields()) {

            field.setAccessible(true);

            try {

                if (field.isAnnotationPresent(SensitiveLog.class)) {

                    values.put(field.getName(), "******");

                } else {

                    values.put(field.getName(), field.get(object));

                }

            } catch (IllegalAccessException ignored) {
            }
        }

        return values;
    }
}