package alex.UTFProject.util;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class MessageUtil {

    //注册账号
    public static String signUp(String email,String code){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return "华实创赛平台：您好！"+ email + "您于 " + getNowTime() + "使用邮箱验证注册，验证码：" + code + "。";
    }


    //找回密码
    public static String findPassword(String email,String code){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return "华实创赛平台：您好！" + email + "您于 " + getNowTime() + "使用邮箱验证找回密码，验证码：" + code + "。";
    }

    //获取现在的时间
    private static String getNowTime(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String timeStr=timestamp
                .toString()
                .substring(0, timestamp.toString().indexOf("."));
        return timeStr;
    }

    public void sendMail(String sender, String email, String verificationCode, JavaMailSender jms,int flag)throws Exception{

        //建立邮件消息
        SimpleMailMessage mainMessage = new SimpleMailMessage();

        //发送者
        mainMessage.setFrom(sender);

        //接收者
        mainMessage.setTo(email);

        //发送的标题
        mainMessage.setSubject("华实创赛平台");
        String msg = "";

        if(flag==0){
            msg = signUp(email,verificationCode);
        }else if(flag==1){
            msg = findPassword(email,verificationCode);
        }
        mainMessage.setText(msg);

        //发送邮件
        jms.send(mainMessage);

    }
}
