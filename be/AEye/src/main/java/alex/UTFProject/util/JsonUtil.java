package alex.UTFProject.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author yannis
 * @version 2020/8/1 13:17
 */
public class JsonUtil {

    /**
     * 将一个对象装换为Json字符串
     */
    public static String toJSONString(Object object) {
        return JSONObject.toJSONString(object);
    }

    /**
     * 将Json字符串转换为Object类型的
     */
    public static Object toObject(String str) {
        return JSON.parse(str);
    }

    /**
     * 将Json字符串转换为实例
     */
    public static <T> T toObject(String str, Class<T> t) {
        return JSON.parseObject(str, t);
    }

    /**
     * 将Json转换为指定类型的List
     */
    public static <T> List<T> toList(String str, Class<T> t) {
        return JSON.parseArray(str, t);
    }

    /**
     * 将Json转换为Map
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> jsonToMap(String str) {
        return (Map<String, T>) JSONObject.parseObject(str);
    }

    /**
     * 将Object转换为Map
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> objectToMap(Object object) {
        return (Map<String, T>) JSONObject.parseObject(JSONObject.toJSONString(object));
    }


}
