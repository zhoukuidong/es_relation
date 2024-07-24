package com.zkd.demo.basic.util;

import com.zkd.demo.basic.core.exceptions.CommonException;
import com.zkd.demo.basic.core.exceptions.IErrorCode;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

public class Assert {

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    public static void isTrue(boolean expression, IErrorCode errorCode) {
        if (!expression) {
            throw new CommonException(errorCode);
        }
    }

    public static void isNotTrue(boolean expression, String message) {
        isTrue(!expression, message);
    }

    public static void isNotTrue(boolean expression) {
        isTrue(!expression);
    }

    public static void isNotTrue(boolean expression, IErrorCode errorCode) {
        isTrue(!expression, errorCode);
    }

    public static void isNull(Object object, String message) {
        if (null != object) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNull(Object object) {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    public static void isNull(Object object, IErrorCode errorCode) {
        if (null != object) {
            throw new CommonException(errorCode);
        }
    }

    public static void notNull(Object object, String message) {
        if (null == object) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    public static void notNull(Object object, IErrorCode message) {
        if (null == object) {
            throw new CommonException(message);
        }
    }

    public static void notBlank(String object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    public static void notBlank(String object, IErrorCode message) {
        if (!StringUtils.hasLength(object)) {
            throw new CommonException(message);
        }
    }

    public static void isBlank(String object) {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    public static void isBlank(String object, IErrorCode message) {
        if (!StringUtils.hasLength(object)) {
            throw new CommonException(message);
        }
    }

    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Object[] array) {
        notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
    }

    public static void notEmpty(Object[] array, IErrorCode errorCode) {
        if (array == null || array.length == 0) {
            throw new CommonException(errorCode);
        }
    }

    public static void notEmpty(Collection collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Collection collection) {
        notEmpty(collection, "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }

    public static void notEmpty(Collection collection, IErrorCode errorCode) {
        if (collection == null || collection.isEmpty()) {
            throw new CommonException(errorCode);
        }
    }

    public static void empty(Collection collection, String message) {
        if (collection != null && !collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void empty(Collection collection) {
        empty(collection, "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }

    public static void empty(Collection collection, IErrorCode errorCode) {
        if (collection != null && !collection.isEmpty()) {
            throw new CommonException(errorCode);
        }
    }

    public static void notEmpty(Map map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Map map, IErrorCode errorCode) {
        if (map == null || map.isEmpty()) {
            throw new CommonException(errorCode);
        }
    }

    public static void notEmpty(Map map) {
        notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
    }

    public static void noNullElements(Object[] array, String message) {
        if (array != null) {
            for (Object o : array) {
                if (o == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }

    public static void noNullElements(Object[] array) {
        noNullElements(array, "[Assertion failed] - this array must not contain any null elements");
    }

    public static void noNullElements(Object[] array, IErrorCode errorCode) {
        if (array != null) {
            for (Object o : array) {
                if (o == null) {
                    throw new CommonException(errorCode);
                }
            }
        }
    }

    /**
     * obj 是否能强转为 type 类型
     *
     * @param type
     * @param obj
     * @param message
     */
    public static void isInstanceOf(Class type, Object obj, String message) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(message + "Object of class ["
                    + (obj != null ? obj.getClass().getName() : "null")
                    + "] must be an instance of " + type);
        }
    }

    public static void isInstanceOf(Class type, Object obj, IErrorCode errorCode) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new CommonException(errorCode);
        }
    }

    /**
     * superType.isAssignableFrom(subType) 判定此 Class
     * 对象所表示的类或接口与指定的 Class 参数所表示的类或接口是否相同，或是否是其超类或超接口。
     * 如果是则返回 true；否则返回 false。如果该 Class 表示一个基本类型，且指定的 Class 参数正是该 Class 对象，
     * 则该方法返回 true；否则返回 false。
     * 1.class2是不是class1的子类或者子接口
     * 2.Object是所有类的父类
     *
     * @param superType
     * @param subType
     */
    public static void isAssignable(Class superType, Class subType, String message) {
        notNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(message + subType + " is not assignable to "
                    + superType);
        }
    }

    public static void isAssignable(Class superType, Class subType, IErrorCode errorCode) {
        notNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new CommonException(errorCode);
        }
    }

    public static void hasText(String text, String message) {
        if (!hasText0(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void hasText(String text, IErrorCode errorCode) {
        if (!hasText0(text)) {
            throw new CommonException(errorCode);
        }
    }

    public static void hasText(String text) {
        hasText(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    private static boolean hasText0(CharSequence str) {
        if (str != null && str.length() > 0) {
            int strLen = str.length();
            for (int i = 0; i < strLen; i++) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void eq(String o, String p, String message) {
        if (null == o && null == p) {
            return;
        }
        if (null == o) {
            throw new IllegalArgumentException(message);
        }
        if (!o.equals(p)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void eq(String o, String p, IErrorCode errorCode) {
        if (null == o && null == p) {
            return;
        }
        if (null == o) {
            throw new CommonException(errorCode);
        }
        if (!o.equals(p)) {
            throw new CommonException(errorCode);
        }
    }
}