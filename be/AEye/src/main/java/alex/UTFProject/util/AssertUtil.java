package alex.UTFProject.util;

import alex.UTFProject.common.CommonErrorCode;
import alex.UTFProject.common.CommonException;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author phoenix
 * @version 2022/1/19 19:21
 */
public class AssertUtil {

    public AssertUtil() {
    }

    public static void isTrue(boolean expValue, CommonErrorCode resultCode, Object obj) {
        if (!expValue) {
            throw new CommonException(resultCode,obj);
        }
    }

    public static void isTrue(boolean expValue, CommonErrorCode resultCode) {
        if (!expValue) {
            throw new CommonException(resultCode);
        }
    }

    public static void isFalse(boolean expValue, CommonErrorCode resultCode, Object obj) {
        isTrue(!expValue, resultCode, obj);
    }

    public static void isFalse(boolean expValue, CommonErrorCode resultCode) {
        isTrue(!expValue, resultCode);
    }

    public static void equals(Object obj1, Object obj2, CommonErrorCode resultCode, Object obj) {
        isTrue(obj1 == null ? obj2 == null : obj1.equals(obj2), resultCode, obj);
    }

    public static void equals(Object obj1, Object obj2, CommonErrorCode resultCode) {
        isTrue(obj1 == null ? obj2 == null : obj1.equals(obj2), resultCode);
    }

    public static void notEquals(Object obj1, Object obj2, CommonErrorCode resultCode, Object obj) {
        isTrue(obj1 == null ? obj2 != null : !obj1.equals(obj2), resultCode, obj);
    }

    public static void contains(Object base, Collection<?> collection, CommonErrorCode resultCode, Object obj) {
        notEmpty(collection, resultCode, obj);
        isTrue(collection.contains(base), resultCode, obj);
    }

    public static void contains(Object base, Collection<?> collection, CommonErrorCode resultCode) {
        notEmpty(collection, resultCode);
        isTrue(collection.contains(base), resultCode);
    }

    public static void in(Object base, Object[] collection, CommonErrorCode resultCode, Object obj) {
        notNull(collection, resultCode, obj);
        boolean hasEqual = false;
        Object[] var5 = collection;
        int var6 = collection.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Object obj2 = var5[var7];
            if (base == obj2) {
                hasEqual = true;
                break;
            }
        }

        isTrue(hasEqual, resultCode, obj);
    }

    public static void notIn(Object base, Object[] collection, CommonErrorCode resultCode, Object obj) {
        if (null != collection) {
            Object[] var4 = collection;
            int var5 = collection.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Object obj2 = var4[var6];
                isTrue(base != obj2, resultCode, obj);
            }

        }
    }

    public static void blank(String str, CommonErrorCode resultCode, Object obj) {
        isTrue(isBlank(str), resultCode, obj);
    }

    public static void notBlank(String str, CommonErrorCode resultCode, Object obj) {
        isTrue(!isBlank(str), resultCode, obj);
    }

    public static void isNull(Object object, CommonErrorCode resultCode, Object obj) {
        isTrue(object == null, resultCode, obj);
    }

    public static void notNull(Object object, CommonErrorCode resultCode, Object obj) {
        isTrue(object != null, resultCode, obj);
    }

    public static void notNull(Object object, CommonErrorCode resultCode) {
        isTrue(object != null, resultCode, null);
    }

    public static void notEmpty(Collection collection, CommonErrorCode resultCode, Object obj) {
        isTrue(!CollectionUtils.isEmpty(collection), resultCode, obj);
    }

    public static void notEmpty(Collection collection, CommonErrorCode resultCode) {
        isTrue(!CollectionUtils.isEmpty(collection), resultCode);
    }

    public static void empty(Collection collection, CommonErrorCode resultCode, Object obj) {
        isTrue(CollectionUtils.isEmpty(collection), resultCode, obj);
    }

    public static void notEmpty(Map map, CommonErrorCode resultCode, Object obj) {
        isTrue(!CollectionUtils.isEmpty(map), resultCode, obj);
    }

    public static void empty(Map map, CommonErrorCode resultCode, Object obj) {
        isTrue(CollectionUtils.isEmpty(map), resultCode, obj);
    }

    private static boolean isBlank(String str) {
        int strLen;
        if(str != null && (strLen = str.length()) != 0) {
            for(int i=0; i<strLen; ++i) {
                if(!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }
}
