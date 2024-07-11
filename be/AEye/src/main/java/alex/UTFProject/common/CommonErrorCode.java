package alex.UTFProject.common;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author phoenix
 * @version 2022/1/19 19:21
 */
@Getter
public enum CommonErrorCode {

    //1打头是微信错误，其他是程序错误
    WX_LOGIN_BUSY(1002,"系统繁忙，此时请开发者稍候再试","微信小程序繁忙，请稍候再试"),
    WX_LOGIN_INVALID_CODE(1003,"无效的code","授权失败，请检查微信账号是否正常"),
    WX_LOGIN_FREQUENCY_REFUSED(1004,"请求太频繁，一分钟不能超过100次","请勿多次重复授权"),
    WX_LOGIN_UNKNOWN_ERROR(1005,"微信授权未知错误","微信异常，请稍后再试"),
    WX_APPSECRET_INVALID(1006,"AppSecret 错误或者 AppSecret 不属于这个小程序","系统异常，请稍后再试"),
    WX_GRANTTYPE_MUSTBE_CLIENTCREDENTIAL(1007,"请确保 grant_type 字段值为 client_credential","系统异常，请稍后再试"),
    WX_APPID_INVALID(1008,"不合法的 AppID","系统异常，请稍后再试"),
    WX_CONTENT_ILLEGAL(1009,"内容安全校验不通过","内容含有违法违规内容，请修改"),
    WX_CONTENT_SECURITY_FORMAT_ERROR(1010,"内容安全校验格式不合法","系统异常，请稍后再试"),

    //微信支付回调
    WX_NOTIFY_RESULT_NULL(1011,"回调结果为空","回调结果为空"),
    WX_NOTIFY_RESULT_UNEXPECTED(1011,"回调结果不是success","回调结果不是success"),

    //微信订阅消息
    WX_SUBSCRIBE_SEND_NULL(140000,"订阅消息返回体为空","系统异常，请稍后再试"),
    WX_SUBSCRIBE_SEND_40003(140003,"touser字段openid为空或者不正确","系统异常，请稍后再试"),
    WX_SUBSCRIBE_SEND_40037(140037,"订阅模板id为空或不正确","系统异常，请稍后再试"),
    WX_SUBSCRIBE_SEND_43101(143101,"用户拒绝接受消息，如果用户之前曾经订阅过，则表示用户取消了订阅关系","系统异常，请稍后再试"),
    WX_SUBSCRIBE_SEND_47003(147003,"模板参数不准确，可能为空或者不满足规则，errmsg会提示具体是哪个字段出错","系统异常，请稍后再试"),
    WX_SUBSCRIBE_SEND_41030(141030,"page路径不正确，需要保证在现网版本小程序中存在，与app.json保持一致","系统异常，请稍后再试"),
    //微信退款

    WX_QRCODE_UNAUTHORIZED(1012,"暂无生成权限","系统异常，请稍后再试"),
    WX_QRCODE_TOO_FREQUENT(1013,"调用分钟频率受限(目前5000次/分钟，会调整)，如需大量小程序码，建议预生成","系统繁忙，请稍后重试"),

    USER_NOT_EXIST(2001,"用户不存在","用户不存在"),
    SYSTEM_ERROR(2002,"系统错误","系统错误，请重试"),
    INVALID_SESSION(2006,"会话丢失","登录已失效，请重新登录"),
    SCHOOL_UNAUTHORIZED(2007,"未通过学校认证","尚未进行校园认证，请先认证"),
    INVALID_PICTURE_TYPE(2008,"无效的图片类型（必须是goods或advice）","图片上传出错，请重试"),
    USER_HAS_BEEN_MEMBER(2009,"用户已经是该项目成员","用户已经是该项目成员，不可以重复邀请"),
    UPDATE_FAIL(2010,"更新失败，出现竞态条件","请稍后重试"),
    UPLOAD_FILE_FAIL(2011,"上传文件失败","请检查网络状况后稍后重试"),
    FILENAME_CAN_NOT_BE_NULL(2012,"文件名不能为空","请取一个有后缀的文件名"),
    DOWNLOAD_FILE_FAILED(2013,"下载文件失败","请在浏览器地址栏中输入链接来测试，或者检查网络或系统状况"),
    FILE_NOT_EXIST(2014, "该文件不存在", "请输入有效的文件名"),
    APPLICATION_HAS_PASSED(2015, "申请已经通过", "请勿重复申请"),
    INVITATION_HAS_PASSED(2016, "邀请已经通过", "请勿重复邀请"),
    EXCEED_MAX_NUMBER(2017, "超过文件可上传的最大数量", "请删除后再上传或取消上传"),
    PROGRAM_UNDERWAY(2017, "项目仍在进行中不可申请成为展示项目", "请结束项目后再申请"),
    WRONG_FILE_FORMAT(2017, "文件格式错误", "请上传正确格式的文件"),
    WRONG_FILE_NAME(2017, "文件名重复", "请勿上传重名的文件"),
    PROGRAM_NOT_EXIST(2018,"项目不存在","项目不存在"),
    CAROUSEL_NOT_EXIST(2018,"轮播图","轮播图"),
    USER_NOT_ADMIN(2018,"用户不是管理员","用户不是管理员"),
    PASSWORD_NOT_QUANTIFIED(2019,"密码不合法","密码不符合要求，请重新输入"),

    TEL_USED_ERROR(1007,"手机号已注册","请前往登录"),
    VERIFY_FAILED(1008,"验证失败","请重试"),

    LOGIN_FAILED(1009,"登录失败","用户名或密码错误"),
    PARAMS_INVALID(1010,"存在有误的参数","请重试"),
    UNSIGNED_USER(1011,"未注册用户","请前往注册"),
    INVALID_PHONE(1012,"无效手机号","请输入正确的手机号"),
    VOTES_MAXIMUM_REACHED(1013,"本日投票已达上限","本日投票已达上限，请明日再投票"),
    SEND_EMAIL_FAILED(1014, "邮件发送失败", "邮件发送失败"),
    VERIFICATION_CODE_WRONG(1015, "邮箱验证码错误", "请输入正确的邮箱验证码"),
    VERIFICATION_CODE_HAS_EXPIRED(1016, "验证码已过期", "请重新申请发送验证码"),
    EMAIL_HAS_BEEN_USED(1017, "邮箱已被使用", "邮箱已被使用，建议找回原账号"),
    PASSWORD_NOT_QUALIFIED(1018, "密码格式错误","密码格式错误，请检查"),
    NOT_SCHOOL_EMAIL(1019, "不是学校邮箱", "不是学校邮箱,请重新输入"),
    NOT_TEACHER_EMAIL(1020,"不是教师邮箱","不是教师邮箱，请确认身份"),
    USER_NOT_TEACHER(1021,"用户不是教师","用户不是教师"),
    NEWS_NOT_EXIST(1022,"新闻不存在","新闻不存在"),
    USER_NOT_STUDENT(1023, "用户不是学生", "用户不是学生"),
    CAPTAIN_ALREADY_EXIST(1024, "该项目已存在负责人", "该项目已存在负责人，请取消原负责人身份后重试"),
    USER_NOT_MEMBER(1025, "用户不是项目成员","用户不是项目成员"),
    USER_NOT_CAPTAIN(1026, "用户不是项目负责人", "用户不是项目负责人"),
    NOT_OWN_PROJECT(1027, "不是该教师的项目，无权操作", "不是该教师的项目，无权操作"),
    NOTIFICATION_NOT_EXIST(1028, "通知不存在", "通知不存在"),
    COMPETITION_NOT_EXIST(1029,"学科竞赛不存在","学科竞赛不存在"),
    DISPLAYPROJECT_NOT_EXIST(1030,"展示项目不存在","展示项目不存在")
    ;


    /**
     * 错误码
     */
    private final Integer errorCode;

    /**
     * 错误原因（给开发看的）
     */
    private final String errorReason;

    /**
     * 错误行动指示（给用户看的）
     */
    private final String errorSuggestion;

    CommonErrorCode(Integer errorCode, String errorReason, String errorSuggestion) {
        this.errorCode = errorCode;
        this.errorReason = errorReason;
        this.errorSuggestion = errorSuggestion;
    }

    @Override
    public String toString() {
        return "CommonErrorCode{" +
                "errorCode=" + errorCode +
                ", errorReason='" + errorReason + '\'' +
                ", errorSuggestion='" + errorSuggestion + '\'' +
                '}';
    }

    //use for json serialization
    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("errorCode",errorCode);
        map.put("errorReason",errorReason);
        map.put("errorSuggestion",errorSuggestion);
        return map;
    }


}