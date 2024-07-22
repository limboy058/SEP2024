package alex.UTFProject.util;

import java.util.regex.Pattern;

public class RegexUtil {

    public final static String PHONE_NUMBER = "^[1][3,4,5,7,8][0-9]{9}$";

    public static void main(String[] args) {
        String content="15800715975";
        System.out.println(Pattern.matches(PHONE_NUMBER, content));
    }

}
