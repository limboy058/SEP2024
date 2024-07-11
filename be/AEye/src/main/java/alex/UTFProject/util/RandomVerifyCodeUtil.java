package alex.UTFProject.util;

import org.springframework.stereotype.Component;

@Component
public class RandomVerifyCodeUtil {
    public static int getRandNum(int min, int max) {
        int randNum = min + (int)(Math.random() * ((max - min) + 1));
        return randNum;
    }

    public static String getRandomVerifyCode(){
        return getRandNum(100000,999999)+"";
    }

}
