package alex.UTFProject.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import org.springframework.util.StringUtils;

/**
 * @author yannis
 * @version 2020/12/6 17:12
 */
public class HutoolUtil {

    public static String base64(String content){
        return Base64.encode(content);
    }

    public static String hmacSha1(String content, String key){
        if(StringUtils.isEmpty(key))return null;

        byte[] keyByte = key.getBytes();
        HMac mac = new HMac(HmacAlgorithm.HmacSHA1, keyByte);
        return mac.digestHex(content);
    }

    public static String hmacSha256(String content, String key){
        if(StringUtils.isEmpty(key))return null;

        byte[] keyByte = key.getBytes();
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, keyByte);
        return mac.digestHex(content);
    }

    public static void main(String[] args) {
        String content = base64("{\"expiration\":\"2020-01-01T12:00:00.000Z\",\"conditions\":[[\"content-length-range\",0,1048576000]]}");
        System.out.println(hmacSha1(content, "1AMBRI9OiTqjhoM5sZZZjnCw7z5CBw"));
    }

}
