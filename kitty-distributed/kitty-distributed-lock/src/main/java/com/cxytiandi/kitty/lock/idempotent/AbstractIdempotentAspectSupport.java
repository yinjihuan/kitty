package com.cxytiandi.kitty.lock.idempotent;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * 代码使用的是https://github.com/alibaba/Sentinel/tree/master/sentinel-extension/sentinel-annotation-aspectj
 * 中提供的，没必要重复造轮子
 */
@Slf4j
public abstract class AbstractIdempotentAspectSupport {

    protected Object handleIdempotentException(ProceedingJoinPoint pjp, Idempotent idempotent, IdempotentException ex) throws Throwable {
        Method handlerMethod = extractHandlerMethod(pjp, idempotent.idempotentHandler(), idempotent.idempotentHandlerClass());
        if (handlerMethod != null) {
            Object[] originArgs = pjp.getArgs();
            Object[] args = Arrays.copyOf(originArgs, originArgs.length + 1);
            args[args.length - 1] = ex;
            if (isStatic(handlerMethod)) {
                return handlerMethod.invoke(null, args);
            }
            return handlerMethod.invoke(pjp.getTarget(), args);
        }
        throw ex;
    }

    private boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    private Method extractHandlerMethod(ProceedingJoinPoint pjp, String name, Class<?>[] locationClass) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        boolean mustStatic = locationClass != null && locationClass.length >= 1;
        Class<?> clazz;
        if (mustStatic) {
            clazz = locationClass[0];
        } else {
            clazz = pjp.getTarget().getClass();
        }
        MethodWrapper m = ResourceMetadataRegistry.lookupHandler(clazz, name);
        if (m == null) {
            Method method = resolveHandlerInternal(pjp, name, clazz, mustStatic);
            ResourceMetadataRegistry.updateHandlerFor(clazz, name, method);
            return method;
        }
        if (!m.isPresent()) {
            return null;
        }
        return m.getMethod();
    }

    private Method resolveHandlerInternal(ProceedingJoinPoint pjp, /*@NonNull*/ String name, Class<?> clazz,
                                               boolean mustStatic) {
        Method originMethod = resolveMethod(pjp);
        Class<?>[] originList = originMethod.getParameterTypes();
        Class<?>[] parameterTypes = Arrays.copyOf(originList, originList.length + 1);
        parameterTypes[parameterTypes.length - 1] = IdempotentException.class;
        return findMethod(mustStatic, clazz, name, originMethod.getReturnType(), parameterTypes);
    }

    private Method findMethod(boolean mustStatic, Class<?> clazz, String name, Class<?> returnType,
                              Class<?>... parameterTypes) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (name.equals(method.getName()) && checkStatic(mustStatic, method)
                    && returnType.isAssignableFrom(method.getReturnType())
                    && Arrays.equals(parameterTypes, method.getParameterTypes())) {

                log.info("Resolved method [{0}] in class [{1}]", name, clazz.getCanonicalName());
                return method;
            }
        }
        // Current class not found, find in the super classes recursively.
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && !Object.class.equals(superClass)) {
            return findMethod(mustStatic, superClass, name, returnType, parameterTypes);
        } else {
            String methodType = mustStatic ? " static" : "";
            log.warn("Cannot find{0} method [{1}] in class [{2}] with parameters {3}",
                    methodType, name, clazz.getCanonicalName(), Arrays.toString(parameterTypes));
            return null;
        }
    }

    private boolean checkStatic(boolean mustStatic, Method method) {
        return !mustStatic || isStatic(method);
    }


    protected Method resolveMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        Method method = getDeclaredMethodFor(targetClass, signature.getName(),
                signature.getMethod().getParameterTypes());
        if (method == null) {
            throw new IllegalStateException("Cannot resolve target method: " + signature.getMethod().getName());
        }
        return method;
    }

    /**
     * Get declared method with provided name and parameterTypes in given class and its super classes.
     * All parameters should be valid.
     *
     * @param clazz          class where the method is located
     * @param name           method name
     * @param parameterTypes method parameter type list
     * @return resolved method, null if not found
     */
    private Method getDeclaredMethodFor(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethodFor(superClass, name, parameterTypes);
            }
        }
        return null;
    }

    /**
     * Check whether the exception is in provided list of exception classes.
     *
     * @param ex         provided throwable
     * @param exceptions list of exceptions
     * @return true if it is in the list, otherwise false
     */
    protected boolean exceptionBelongsTo(Throwable ex, Class<? extends Throwable>[] exceptions) {
        if (exceptions == null) {
            return false;
        }
        for (Class<? extends Throwable> exceptionClass : exceptions) {
            if (exceptionClass.isAssignableFrom(ex.getClass())) {
                return true;
            }
        }
        return false;
    }

}
