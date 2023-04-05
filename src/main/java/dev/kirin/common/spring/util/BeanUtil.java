package dev.kirin.common.spring.util;

import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class BeanUtil {
    public static <T> T copyIfNotNulls(T source, T target) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null) {
                        ResolvableType sourceResolvableType = ResolvableType.forMethodReturnType(readMethod);
                        ResolvableType targetResolvableType = ResolvableType.forMethodParameter(writeMethod, 0);

                        // Ignore generic types in assignable check if either ResolvableType has unresolvable generics.
                        boolean isAssignable =
                                (sourceResolvableType.hasUnresolvableGenerics() || targetResolvableType.hasUnresolvableGenerics() ?
                                        ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType()) :
                                        targetResolvableType.isAssignableFrom(sourceResolvableType));

                        if (isAssignable) {
                            try {
                                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                    readMethod.setAccessible(true);
                                }
                                Object value = readMethod.invoke(source);
                                if(value == null) {
                                    continue;
                                }
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                writeMethod.invoke(target, value);
                            }
                            catch (Throwable ex) {
                                throw new ObjectUtilException(
                                        "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                            }
                        }
                    }
                }
            }
        }
        return target;
    }

    public static class ObjectUtilException extends RuntimeException {
        public ObjectUtilException() {
        }

        public ObjectUtilException(String message) {
            super(message);
        }

        public ObjectUtilException(String message, Throwable cause) {
            super(message, cause);
        }

        public ObjectUtilException(Throwable cause) {
            super(cause);
        }

        public ObjectUtilException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
