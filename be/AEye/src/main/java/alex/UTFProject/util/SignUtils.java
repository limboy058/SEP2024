package alex.UTFProject.util;

import alex.UTFProject.common.CommonConstants;
import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import tk.mybatis.mapper.util.StringUtil;

import java.security.*;
import java.util.*;

public class SignUtils {

  /**
   * 签名的时候不携带的参数
   */
  private static final List<String> NO_SIGN_PARAMS = Lists.newArrayList("sign", "key", "xmlString", "xmlDoc", "couponList");


  public static String sign(String string, PrivateKey privateKey) {
    try {
      Signature sign = Signature.getInstance("SHA256withRSA");
      sign.initSign(privateKey);
      sign.update(string.getBytes());

      return Base64.getEncoder().encodeToString(sign.sign());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("当前Java环境不支持SHA256withRSA", e);
    } catch (SignatureException e) {
      throw new RuntimeException("签名计算失败", e);
    } catch (InvalidKeyException e) {
      throw new RuntimeException("无效的私钥", e);
    }
  }

  /**
   * 微信支付签名算法(详见:https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=4_3).
   *
   * @param params        参数信息
   * @param signType      签名类型，如果为空，则默认为MD5
   * @param signKey       签名Key
   * @param ignoredParams 签名时需要忽略的特殊参数
   * @return 签名字符串 string
   */
  public static String createSign(Map<String, Object> params, String signType, String signKey, String[] ignoredParams) {
    StringBuilder toSign = new StringBuilder();
    for (String key : new TreeMap<>(params).keySet()) {
      String value = params.get(key) + "";
      boolean shouldSign = false;
      if (StringUtil.isNotEmpty(value) && !ArrayUtils.contains(ignoredParams, key)
              && !NO_SIGN_PARAMS.contains(key)) {
        shouldSign = true;
      }

      if (shouldSign) {
        toSign.append(key).append("=").append(value).append("&");
      }
    }

    toSign.append("key=").append(signKey);
    if (CommonConstants.SIGN_TYPE_HMAC_SHA256.equals(signType)) {
      return HutoolUtil.hmacSha256(toSign.toString(), signKey);
    } else {
      return DigestUtils.md5Hex(toSign.toString()).toUpperCase();
    }
  }

  /**
   * 随机生成32位字符串.
   */
  public static String genRandomStr() {
    return genRandomStr(32);
  }

  /**
   * 生成随机字符串
   *
   * @param length 字符串长度
   * @return
   */
  public static String genRandomStr(int length) {
    String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    Random random = new Random();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(base.length());
      sb.append(base.charAt(number));
    }
    return sb.toString();
  }
}
