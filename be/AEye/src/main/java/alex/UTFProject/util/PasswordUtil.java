package alex.UTFProject.util;

import cn.hutool.crypto.SecureUtil;
import org.springframework.stereotype.Component;


@Component
public class PasswordUtil {
    /**
     * 键盘横向方向规则
     */
    public static String[] KEYBOARD_HORIZONTAL_ARR = {
            "01234567890",
            "qwertyuiop",
            "asdfghjkl",
            "zxcvbnm",
    };
    /**
     * 键盘斜线方向规则
     */
    public static String[] KEYBOARD_SLOPE_ARR = {
            "1qaz",
            "2wsx",
            "3edc",
            "4rfv",
            "5tgb",
            "6yhn",
            "7ujm",
            "8ik,",
            "9ol.",
            "0p;/",
            "=[;.",
            "-pl,",
            "0okm",
            "9ijn",
            "8uhb",
            "7ygv",
            "6tfc",
            "5rdx",
            "4esz"
    };


    public String convert(String origin){
        return SecureUtil
                .md5(origin)
                .substring(3,13);
    }


    public static void main(String[] args) {
        String password = "123456";
        PasswordUtil passwordUtil = new PasswordUtil();
        System.out.println(SecureUtil.md5(password));
        System.out.println(passwordUtil.convert(password));
    }

    /**
     * @brief   检测密码中字符长度
     * @param[in] password            密码字符串
     * @return  符合长度要求 返回true
     */
    public static boolean checkPasswordLength(String password) {

        return password.length() >= 8 && password.length() <= 20;
    }

    /**
     * @brief   检测密码中是否包含数字
     * @param[in] password            密码字符串
     * @return  包含数字 返回true
     */
    public static boolean checkContainDigit(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;
        int num_count = 0;

        for (char pass : chPass) {
            if (Character.isDigit(pass)) {
                num_count++;
            }
        }

        if (num_count >= 1){
            flag = true;
        }
        return flag;
    }

    /**
     * @brief   检测密码中是否包含字母（不区分大小写）
     * @param[in] password            密码字符串
     * @return  包含字母 返回true
     */
    public static boolean checkContainCase(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;
        int char_count = 0;

        for (char pass : chPass) {
            if (Character.isLetter(pass)) {
                char_count++;
            }
        }

        if (char_count >= 1) {
            flag = true;
        }
        return flag;
    }

    /**
     * @brief   检测密码中是否包含小写字母
     * @param[in] password            密码字符串
     * @return  包含小写字母 返回true
     */
    public static boolean checkContainLowerCase(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;
        int char_count = 0;

        for (char pass : chPass) {
            if (Character.isLowerCase(pass)) {
                char_count++;
            }
        }

        if (char_count >= 1) {
            flag = true;
        }
        return flag;
    }

    /**
     * @brief   检测密码中是否包含大写字母
     * @param[in] password            密码字符串
     * @return  包含大写字母 返回true
     */
    public static boolean checkContainUpperCase(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;
        int char_count = 0;

        for (char pass : chPass) {
            if (Character.isUpperCase(pass)) {
                char_count++;
            }
        }

        if (char_count >= 1) {
            flag = true;
        }
        return flag;
    }

    /**
     * @brief   检测密码中是否包含特殊符号
     * @param[in] password            密码字符串
     * @return  包含特殊符号 返回true
     */
    public static boolean checkContainSpecialChar(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;
        int special_count = 0;

        for (char pass : chPass) {
            if ("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".indexOf(pass) != -1) {
                special_count++;
            }
        }

        if (special_count >= 1){
            flag = true;
        }
        return flag;
    }

    /**
     * @brief   键盘规则匹配器 横向连续检测
     * @param[in] password            密码字符串
     * @return  含有横向连续字符串 返回true
     */
    public static boolean checkLateralKeyboardSite(String password) {
        String t_password = new String(password);
        //将所有输入字符转为小写
        t_password = t_password.toLowerCase();
        int n = t_password.length();
        boolean flag = false;
        int arrLen = KEYBOARD_HORIZONTAL_ARR.length;
        int limit_num = 3 ;

        for(int i=0; i+limit_num<=n; i++) {
            String str = t_password.substring(i, i+limit_num);
            String distinguishStr = password.substring(i, i+limit_num);

            for(int j=0; j<arrLen; j++) {
                String PwdSecurityConfigStr = KEYBOARD_HORIZONTAL_ARR[j];
                String revOrderStr = new StringBuffer(KEYBOARD_HORIZONTAL_ARR[j]).reverse().toString();

                //检测包含字母(区分大小写)
                //考虑 大写键盘匹配的情况
                String UpperStr = KEYBOARD_HORIZONTAL_ARR[j].toUpperCase();
                if((PwdSecurityConfigStr.contains(distinguishStr)) || (UpperStr.contains(distinguishStr))) {
                    flag = true;
                    return flag;
                }
                //考虑逆序输入情况下 连续输入
                String revUpperStr = new StringBuffer(UpperStr).reverse().toString();
                if((revOrderStr.contains(distinguishStr)) || (revUpperStr.contains(distinguishStr))) {
                    flag = true;
                    return flag;
                }
            }
        }
        return flag;
    }

    /**
     * @brief   键盘规则匹配器 斜向规则检测
     * @param[in] password            密码字符串
     * @return  含有斜向连续字符串 返回true
     */
    public static boolean checkKeyboardSlantSite(String password) {
        String t_password = new String(password);
        t_password = t_password.toLowerCase();
        int n = t_password.length();
        boolean flag = false;
        int arrLen = KEYBOARD_SLOPE_ARR.length;
        int limit_num = 3;

        for(int i=0; i+limit_num<=n; i++) {
            String str = t_password.substring(i, i+limit_num);
            String distinguishStr = password.substring(i, i+limit_num);
            for (String PwdSecurityConfigStr : KEYBOARD_SLOPE_ARR) {
                String revOrderStr = new StringBuffer(PwdSecurityConfigStr).reverse().toString();
                //检测包含字母(区分大小写)
                //考虑 大写键盘匹配的情况
                String UpperStr = PwdSecurityConfigStr.toUpperCase();
                if ((PwdSecurityConfigStr.contains(distinguishStr)) || (UpperStr.contains(distinguishStr))) {
                    flag = true;
                    return flag;
                }
                //考虑逆序输入情况下 连续输入
                String revUpperStr = new StringBuffer(UpperStr).reverse().toString();
                if ((revOrderStr.contains(distinguishStr)) || (revUpperStr.contains(distinguishStr))) {
                    flag = true;
                    return flag;
                }
            }
        }
        return flag;
    }

    /**
     * @brief   评估a-z,z-a这样的连续字符
     * @param[in] password            密码字符串
     * @return  含有a-z,z-a连续字符串 返回true
     */
    public static boolean checkSequentialChars(String password) {
        String t_password = new String(password);
        boolean flag = false;
        int limit_num = 3;
        int normal_count = 0;
        int reversed_count = 0;

        //检测包含字母(区分大小写)
        t_password = t_password.toLowerCase();

        int n = t_password.length();
        char[] pwdCharArr = t_password.toCharArray();

        for (int i=0; i+limit_num<=n; i++) {
            normal_count = 0;
            reversed_count = 0;
            for (int j=0; j<limit_num-1; j++) {
                if (pwdCharArr[i+j+1]-pwdCharArr[i+j]==1) {
                    normal_count++;
                    if(normal_count == limit_num -1){
                        return true;
                    }
                }

                if (pwdCharArr[i+j]-pwdCharArr[i+j+1]==1) {
                    reversed_count++;
                    if(reversed_count == limit_num -1){
                        return true;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * @brief   评估aaaa,1111这样的相同连续字符
     * @param[in] password            密码字符串
     * @return  含有aaaa,1111等连续字符串 返回true
     */
    public static boolean checkSequentialSameChars(String password) {
        String t_password = new String(password);
        int n = t_password.length();
        char[] pwdCharArr = t_password.toCharArray();
        boolean flag = false;
        int limit_num = 3;
        int count = 0;
        for (int i=0; i+limit_num<=n; i++) {
            count=0;
            for (int j=0; j<limit_num-1; j++) {
                if(pwdCharArr[i+j] == pwdCharArr[i+j+1]) {
                    count++;
                    if (count == limit_num -1){
                        return true;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * @brief   评估密码中包含的字符类型是否符合要求
     * @param[in] password            密码字符串
     * @return  符合要求 返回true
     */
    public boolean EvalPWD(String password) {
        if (password == null || "".equals(password)) {
            return false;
        }
        boolean flag = false;

        flag = checkPasswordLength(password);
        if (!flag)  return false;
        int i = 0;
        flag = checkContainDigit(password);
        if (flag) i++;

        flag = checkContainLowerCase(password);
        if (flag)  i++;

        //检测包含大写字母
        flag = checkContainUpperCase(password);
        if (flag) i++;

        flag = checkContainSpecialChar(password);
        if (flag) i++;
        return i >= 3;

    }

}
