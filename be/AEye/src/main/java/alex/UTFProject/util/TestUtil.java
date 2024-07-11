package alex.UTFProject.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class TestUtil {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String A = "https%3A%2F%2Fhuashi-1305159828.cos.ap-shanghai.myqcloud.com%2Fhs00000131.tzh+PreingWeb%E6%8E%A5%E5%8F%A3%E6%96%87%E6%A1%A31.docx";
        String B = "https://huashi-1305159828.cos.ap-shanghai.myqcloud.com/hs00000131.tzh%20PreingWeb%E6%8E%A5%E5%8F%A3%E6%96%87%E6%A1%A31.docx";
        String C = "https://huashi-1305159828.cos.ap-shanghai.myqcloud.com/hs00000131.%E5%AD%A6%E5%B7%A5%E9%83%A8%E6%88%90%E9%95%BF%E7%94%B5%E5%AD%90%E6%A1%A3%E6%A1%88%E9%9C%80%E6%B1%82%E8%AE%BE%E8%AE%A1%E4%B9%A6v20210304-2.docx";
        System.out.println(URLDecoder.decode(A, "UTF-8"));
        System.out.println(URLDecoder.decode(B, "UTF-8"));
        System.out.println(URLDecoder.decode(C, "UTF-8"));
    }
}
