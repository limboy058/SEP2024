package alex.UTFProject.util;

import com.aliyuncs.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;

@Slf4j
public class NewJsonUtil {

    /**
     * 定义映射对象
     */
    public static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 日期格式化
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static {
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //取消默认转换timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
        //忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    /**
     * string转JsonNode
     *
     * @param jsonString
     * @return com.fasterxml.jackson.databind.JsonNode
     */
    public static JsonNode stringToJsonNode(String jsonString) throws JsonProcessingException {

        return objectMapper.readTree(jsonString);

    }

    /**
     * 对象转json字符串
     *
     * @param obj
     * @param <T>
     */
    public static <T> String objToString(T obj) {

        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Parse Object to String error : {}", e.getMessage());
            return null;
        }
    }

    /**
     * 对象转格式化的字符串字符串
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String objToPrettyString(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Parse Object to String error : {}", e.getMessage());
            return null;
        }
    }

    /**
     * json字符串转对象
     *
     * @param jsonString
     * @param cls
     * @param <T>
     */
    public static <T> T stringToObj(String jsonString, Class<T> cls) {
        if (StringUtils.isEmpty(jsonString) || cls == null) {
            return null;
        }
        try {
            return cls.equals(String.class) ? (T) jsonString : objectMapper.readValue(jsonString, cls);
        } catch (JsonProcessingException e) {
            log.warn("Parse String to Object error : {}", e.getMessage());
            return null;
        }
    }

//    /**
//     * json字符串转对象(复杂泛型类型)
//     *
//     * @param jsonString
//     * @param typeReference
//     * @param <T>
//     * @return
//     */
//    public static <T> T stringToObj(String jsonString, TypeReference<T> typeReference) {
//        if (StringUtils.isEmpty(jsonString) || typeReference == null) {
//            return null;
//        }
//        try {
//            return typeReference.getType().equals(String.class) ? (T) jsonString : objectMapper.readValue(jsonString, typeReference);
//        } catch (JsonProcessingException e) {
//            log.warn("Parse String to Object error : {}", e.getMessage());
//            return null;
//        }
//    }
}
