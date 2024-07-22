package alex.UTFProject.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author yannis
 * @version 2021/1/22 22:46
 */
@Component
@Data
public class YmlConfig {

//    @Value("${server.domain}")
//    private String domain;

    @Value("${server.port}")
    private String port;
//
//    @Value("${wxapp.appid}")
//    private String appId;
//
//    @Value("${wxapp.appsecret}")
//    private String appSecret;
//
//    @Value("${mini-app.mch-id}")
//    private String mchId;
//
//    @Value("${mini-app.mch-serial-no}")
//    private String mchSerialNo;
//
//    @Value("${mini-app.mch-private-key}")
//    private String mchPrivateKey;
//
//    @Value("${mini-app.apiV3-key}")
//    private String apiV3Key;

}
