package alex.UTFProject.util;

/**
 * @author yannis
 * @version 2021/1/18 12:28
 */
public class RandomNoUtil {

    public static String generateOutTradeNo(){
        return  "PAY_" + generateRandomNo();
    }

    public static String generateRandomNo(){
        return "" + System.currentTimeMillis();
    }
}
