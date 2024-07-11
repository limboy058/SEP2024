package alex.UTFProject.common;

import alex.UTFProject.common.serializer.EnumSerializer;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回实体
 * @author yan on 2020-02-27
 */
@Data
public class Result implements Serializable {

    private Integer code;
    private String msg;
    private Object data;

    @JSONField(serializeUsing = EnumSerializer.class)
    private CommonErrorCode commonErrorCode;

    public static Result success(Object data) {
        return success("操作成功", data);
    }

    public static Result success(String mess, Object data) {
        Result m = new Result();
        m.setCode(0);
        m.setData(data);
        m.setMsg(mess);
        return m;
    }

    public static Result fail(String mess) {
        return fail(mess, null);
    }

    public static Result fail(String mess, Object data) {
        Result m = new Result();
        m.setCode(-1);
        m.setData(data);
        m.setMsg(mess);

        return m;
    }

    public static Result result(CommonErrorCode commonErrorCode, Object data) {
        Result m = new Result();
        m.setCode(-1);
        m.setData(data);
        m.setCommonErrorCode(commonErrorCode);

        return m;
    }

    public static Result result(CommonErrorCode commonErrorCode) {
        return result(commonErrorCode,null);
    }
}