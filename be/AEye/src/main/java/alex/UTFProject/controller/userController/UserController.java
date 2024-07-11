package alex.UTFProject.controller.userController;
import alex.UTFProject.common.CommonConstants;
import alex.UTFProject.common.CommonErrorCode;
import alex.UTFProject.mapper.UserMapper;
import alex.UTFProject.util.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static alex.UTFProject.common.CommonConstants.USER_PIC_LOCAL;

@RestController
@Api(tags = "传输用户信息")
@CrossOrigin("*")
@Slf4j
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private wechatLoginUtil wechatLoginUtil;


    @PostMapping("/AITalk")
    @ApiOperation("进行对话")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginCode",value = "登录码",required = true),
            @ApiImplicitParam(name = "newTalk",value = "是否为新对话（bool）",required = true),
            @ApiImplicitParam(name = "kind",value="调用AI服务类型，1为实景识别，2为文字识别，3为人物识别"),
            @ApiImplicitParam(name = "question", value = "用户的提问",required = false),
            @ApiImplicitParam(name = "photo",value = "照片",required = false,dataTypeClass = MultipartFile.class),
    })
    String AITalk(@RequestPart String newTalk,
                       @RequestPart String loginCode,
                       @RequestPart String kind,
                       @RequestPart String question,
                       @ApiParam(value="上传图片", required = false)@RequestPart MultipartFile photo
    ) throws Exception {
        String openId=wechatLoginUtil.checkOpenId(loginCode);
        if(openId==null){
            return "请求失败，登录码不合法";
        }
        String path=uploadImg(photo);
        String requestBody = "{\"image_url\":\""+path+"\",\"content\":\""+question+"\"}";
        return HttpClientSslUtils.doPost("http://49.52.27.63:19999/chat",requestBody,30000);
    }
    public String uploadImg(MultipartFile file) {
        //file是imgFile的别名，只能上传一张图
        String fileName = file.getOriginalFilename();
        // 获取上传文件类型的扩展名,先得到.的位置，再截取从.的下一个位置到文件的最后，最后得到扩展名

        AssertUtil.notNull(fileName, CommonErrorCode.FILENAME_CAN_NOT_BE_NULL);

        String ext = fileName.substring(fileName.lastIndexOf("."));
        // 对扩展名进行小写转换
        ext = ext.toLowerCase();
        // 定义一个数组，用于保存可上传的文件类型
        List<String> fileTypes = new ArrayList<>();
        fileTypes.add(".jpg");
        fileTypes.add(".jpeg");
        fileTypes.add(".bmp");
        fileTypes.add(".gif");
        fileTypes.add(".png");
        if (!fileTypes.contains(ext)) { // 如果扩展名属于允许上传的类型，则创建文件
            return "file type error";
        }
        fileName = new Date().getTime()+".jpg";
        File targetFile = new File( USER_PIC_LOCAL , fileName );
        // 保存
        try {
            //使用此方法保存必须要绝对路径且文件夹必须已存在,否则报错
            file.transferTo(targetFile);
        } catch (Exception e) {
            // e.printStackTrace();
            return "-1";
        }
        return CommonConstants.USER_PIC_ADDRESS + fileName;
    }
}
