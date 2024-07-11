package alex.UTFProject.util;

import alex.UTFProject.common.CommonErrorCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

/**
 * @author xw
 * @version 2023/2/25
 */
@Component
@Slf4j
public class WechatUtils {

    private static final String appid = "wxb95385a89f7899ac";
    private static final String appSecret = "0e4873840eab9bfca89978cb85311a85";

    public static String getOpenId(String code) throws Exception {
        System.out.println("code" + code);
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        url += "?appid="+appid;//自己的appid
        url += "&secret="+appSecret;//自己的appSecret
        url += "&js_code=" + code;
        url += "&grant_type=authorization_code";
        url += "&connect_redirect=1";
        String res = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);    //GET方式
        CloseableHttpResponse response = null;
        // 配置信息
        RequestConfig requestConfig = RequestConfig.custom()          // 设置连接超时时间(单位毫秒)
                .setConnectTimeout(5000)                    // 设置请求超时时间(单位毫秒)
                .setConnectionRequestTimeout(5000)             // socket读写超时时间(单位毫秒)
                .setSocketTimeout(5000)                    // 设置是否允许重定向(默认为true)
                .setRedirectsEnabled(false).build();           // 将上面的配置信息 运用到这个Get请求里
        httpget.setConfig(requestConfig);                         // 由客户端执行(发送)Get请求
        response = httpClient.execute(httpget);                   // 从响应模型中获取响应实体
        HttpEntity responseEntity = response.getEntity();
        System.out.println("响应状态为:" + response.getStatusLine());
        if (responseEntity != null) {
            res = EntityUtils.toString(responseEntity);
            System.out.println("响应内容长度为:" + responseEntity.getContentLength());
            System.out.println("响应内容为:" + res);
        }
        // 释放资源
        httpClient.close();
        response.close();
        JSONObject jo = JSON.parseObject(res);
        String openid = jo.getString("openid");
        System.out.println("openid" + openid);
        return openid;
    }

    public static String getAccessToken()throws Exception{
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"+
                "&appid="+appid+"&secret="+appSecret;
        String res = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);    //GET方式
        CloseableHttpResponse response = null;
        // 配置信息
        RequestConfig requestConfig = RequestConfig.custom()          // 设置连接超时时间(单位毫秒)
                .setConnectTimeout(5000)                    // 设置请求超时时间(单位毫秒)
                .setConnectionRequestTimeout(5000)             // socket读写超时时间(单位毫秒)
                .setSocketTimeout(5000)                    // 设置是否允许重定向(默认为true)
                .setRedirectsEnabled(false).build();           // 将上面的配置信息 运用到这个Get请求里
        httpget.setConfig(requestConfig);                         // 由客户端执行(发送)Get请求
        response = httpClient.execute(httpget);                   // 从响应模型中获取响应实体
        HttpEntity responseEntity = response.getEntity();
        System.out.println("响应状态为:" + response.getStatusLine());
        if (responseEntity != null) {
            res = EntityUtils.toString(responseEntity);
            System.out.println("响应内容长度为:" + responseEntity.getContentLength());
            System.out.println("响应内容为:" + res);
        }
        // 释放资源
        httpClient.close();
        response.close();
        JSONObject jo = JSON.parseObject(res);
        String accessToken = jo.getString("access_token");
        System.out.println("access_token:" + accessToken);
        return accessToken;
    }

    public static String getPhoneNumber(String code) throws Exception {
        JSONObject phone;
        // 获取token
        String token_url = String.format("https://api.weixin.qq.com/cgi-bin/token?" +
                "grant_type=client_credential&appid=%s&secret=%s", appid, appSecret );

        JSONObject token = JSON.parseObject(HttpClientSslUtils.doGet(token_url));
        if (token == null) {
            log.info("获取token失败");
            return null;
        }
        String accessToken = token.getString("access_token");
        if (StringUtils.isEmpty(accessToken)) {
            log.info("获取token失败");
            return null;
        }
        log.info("token : {}", accessToken);
        //获取phone
        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber"
                + "?access_token=" + accessToken;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        String reqJsonStr = NewJsonUtil.objToString(jsonObject);
        phone = JSON.parseObject(HttpClientSslUtils.doPost(url, reqJsonStr));

        if (phone == null) {
            log.info("获取手机号失败");
            return null;
        }
        JSONObject json = phone.getJSONObject("phone_info");

        AssertUtil.notNull(json, CommonErrorCode.SYSTEM_ERROR);

        return json.getString("phoneNumber");

    }

    public static void main(String[] args) throws Exception {
        System.out.println(getPhoneNumber(""));
    }
}