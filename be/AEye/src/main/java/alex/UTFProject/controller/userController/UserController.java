package alex.UTFProject.controller.userController;

import alex.UTFProject.common.CommonConstants;
import alex.UTFProject.common.CommonErrorCode;
import alex.UTFProject.mapper.UserMapper;
import alex.UTFProject.util.*;
import com.google.gson.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import java.io.File;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Autowired
    private RedisTemplate<String, Object> redisTemplate0;//用于校验登录，logincode-openId

    @Autowired
    private RedisTemplate<String, Object> redisTemplate1;//用于存取旧对话信息，openId-json

    @Autowired
    private RedisTemplate<String, Object> redisTemplate2;//用于存放发送的消息队列，openId-json

    @Autowired
    private RedisTemplate<String, String> redisTemplate3;//用于存放获取的消息队列，openId-json

    AtomicInteger txn_id=new AtomicInteger(0);


    @PostMapping("/AITalk")
    @ApiOperation("进行对话")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginCode",value = "登录码",required = true),
            @ApiImplicitParam(name = "newTalk",value = "是否为新对话（bool）",required = true),
            @ApiImplicitParam(name = "kind",value="调用AI服务类型，1为实景识别，2为文字识别,其余为自定义prompt"),
            @ApiImplicitParam(name = "question", value = "用户的提问",required = false),
            @ApiImplicitParam(name = "photo",value = "照片",required = false,dataTypeClass = MultipartFile.class),
    })
    String AITalk(@RequestPart(value = "newTalk",required = true) String newTalk,
                  @RequestPart(value = "loginCode",required = true) String loginCode,
                  @RequestPart(value="kind",required = true) String kind,
                  @RequestPart(value="question",required = false) String question,
                  @RequestPart(value="photo",required = false) MultipartFile photo
    ) throws Exception {
        String openId=get_openId(loginCode);
        if(Objects.equals(openId, "-1")){
            return "请求失败，登录码不合法";
        }
        if(Objects.equals(openId, "-2")){
            return "请求次数过多，请稍后再试";
        }
        if(!Objects.equals(newTalk, "1") && !Objects.equals(newTalk, "0")){
            return "newTalk只接受0和1";
        }
        if(newTalk.equals("0")&&redisTemplate1.opsForValue().get(openId)==null){
            return "不存在旧对话";
        }
//        if(photo==null&& Objects.equals(newTalk, "1")){
//            return "新对话必须包含图片";
//        }
        JsonArray jsonArray;


        if(newTalk.equals("0")){
            String previous_content="";
            previous_content=(String)redisTemplate1.opsForValue().get(openId);
            assert previous_content != null;
            jsonArray = JsonParser.parseString(previous_content).getAsJsonArray();
        }
        else{
            jsonArray=new JsonArray();
        }

        if(photo!=null){
            String path;
            path=uploadImg(photo);
            JsonObject picJson = new JsonObject();
            picJson.addProperty("url",path);
            jsonArray.add(picJson);
        }

        if(question!=null){
            JsonObject questionJson = new JsonObject();
            questionJson.addProperty("user",question);
            jsonArray.add(questionJson);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject totJson=new JsonObject();
        totJson.addProperty("f",kind);
        totJson.addProperty("chat",gson.toJson(jsonArray));
        totJson.addProperty("type","talk");
        String requestBody =gson.toJson(totJson);


        redisTemplate2.opsForValue().set(openId,requestBody);

        int wait_cnt=30*10;
        while(wait_cnt>0&& Boolean.FALSE.equals(redisTemplate3.opsForValue().getOperations().hasKey(openId))){
            wait_cnt--;
            Thread.sleep(100);
        }
        if(wait_cnt==0){
            return "请求超时，计算服务器未响应";
        }
        String response=(String)redisTemplate3.opsForValue().get(openId);
        redisTemplate3.delete(openId);

        JsonObject answerJson=new JsonObject();
        answerJson.addProperty("assistant",response);
        jsonArray.add(answerJson);

        redisTemplate1.opsForValue().set(openId, gson.toJson(jsonArray));

        return response;
    }

    @PostMapping("/addPersonImg")
    @ApiOperation("添加人物")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginCode",value = "登录码",required = true),
            @ApiImplicitParam(name = "name",value = "人物名称",required = true),
            @ApiImplicitParam(name = "photo",value = "照片",required = false,dataTypeClass = MultipartFile.class),
    })
    String addPersonImg(@RequestPart(value = "name",required = true) String name,
                        @RequestPart(value = "loginCode",required = true) String loginCode,
                        @RequestPart(value="photo",required = true) MultipartFile photo
    ) throws Exception {
        String openId=get_openId(loginCode);
        if(Objects.equals(openId, "-1")){
            return "请求失败，登录码不合法";
        }
        if(Objects.equals(openId, "-2")){
            return "请求次数过多，请稍后再试";
        }
        String path=uploadImg(photo);
        int len=userMapper.getUserImg(openId,name);
        String new_name=name+"_"+String.valueOf(len);


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject totJson=new JsonObject();
        totJson.addProperty("type","add");
        totJson.addProperty("url",path);
        totJson.addProperty("name",new_name);
        String requestBody =gson.toJson(totJson);
        redisTemplate2.opsForValue().set(openId,requestBody);
        int wait_cnt=30*10;
        while(wait_cnt>0&& Boolean.FALSE.equals(redisTemplate3.opsForValue().getOperations().hasKey(openId))){
            wait_cnt--;
            Thread.sleep(100);
        }
        if(wait_cnt==0){
            return "请求超时，计算服务器未响应";
        }
        String response=redisTemplate3.opsForValue().get(openId);
        redisTemplate3.delete(openId);
        if(!Objects.equals(response, "0")){
            return response+" 上传失败";
        }

        userMapper.addUserImg(openId,new_name,path);
        return "添加成功";
    }


    @PostMapping("/faceDetect")
    @ApiOperation("人脸识别")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginCode",value = "登录码",required = true),
            @ApiImplicitParam(name = "photo",value = "照片",required = true,dataTypeClass = MultipartFile.class),
    })
    String faceDetect(@RequestPart(value = "loginCode",required = true) String loginCode,
                      @RequestPart(value="photo",required = true) MultipartFile photo
    ) throws Exception {
        String openId=get_openId(loginCode);
        if(Objects.equals(openId, "-1")){
            return "请求失败，登录码不合法";
        }
        if(Objects.equals(openId, "-2")){
            return "请求次数过多，请稍后再试";
        }
        String path=uploadImg(photo);


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject totJson=new JsonObject();
        totJson.addProperty("type","faceDetect");
        totJson.addProperty("url",path);
        String requestBody =gson.toJson(totJson);
        redisTemplate2.opsForValue().set(openId,requestBody);
        int wait_cnt=30*10;
        while(wait_cnt>0&& Boolean.FALSE.equals(redisTemplate3.opsForValue().getOperations().hasKey(openId))){
            wait_cnt--;
            Thread.sleep(100);
        }
        if(wait_cnt==0){
            return "请求超时，计算服务器未响应";
        }
        String response=redisTemplate3.opsForValue().get(openId);
        redisTemplate3.delete(openId);
        return response;
    }



    @PostMapping("/deletePersonImg")
    @ApiOperation("添加人物")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginCode",value = "登录码",required = true),
            @ApiImplicitParam(name = "name",value = "人物名称",required = true),
    })
    String deletePersonImg(@RequestPart(value = "name",required = true) String name,
                           @RequestPart(value = "loginCode",required = true) String loginCode
    ) throws Exception {
        String openId=get_openId(loginCode);
        if(Objects.equals(openId, "-1")){
            return "请求失败，登录码不合法";
        }
        if(Objects.equals(openId, "-2")){
            return "请求次数过多，请稍后再试";
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject totJson=new JsonObject();
        totJson.addProperty("type","delete");
        totJson.addProperty("name",name);
        String requestBody =gson.toJson(totJson);
        redisTemplate2.opsForValue().set(openId,requestBody);
        int wait_cnt=30*10;
        while(wait_cnt>0&& Boolean.FALSE.equals(redisTemplate3.opsForValue().getOperations().hasKey(openId))){
            wait_cnt--;
            Thread.sleep(100);
        }
        if(wait_cnt==0){
            return "请求超时，计算服务器未响应";
        }
        String response=redisTemplate3.opsForValue().get(openId);
        redisTemplate3.delete(openId);
        for(int idx=0;idx<response.length();idx++){
            if(response.charAt(idx)>'9'||response.charAt(idx)<'0'){
                return response;
            }
        }

        userMapper.deleteUserImg(openId,name);
        return "删除了"+name+"的"+response+"张相关图片";
    }

    @PostMapping("/getPersonImg")
    @ApiOperation("获取人物列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginCode",value = "登录码",required = true),
    })
    String getPersonImg(
            @RequestPart(value = "loginCode",required = true) String loginCode
    ) throws Exception {
        String openId=get_openId(loginCode);
        if(Objects.equals(openId, "-1")){
            return "请求失败，登录码不合法";
        }
        if(Objects.equals(openId, "-2")){
            return "请求次数过多，请稍后再试";
        }
        List<String> names=userMapper.getUserAllImg(openId);

        Set<String> uniqueNames = new HashSet<>();

        for (String name_ : names) {
            // 去掉_后面的部分
            String processedName = name_.split("_")[0];
            uniqueNames.add(processedName);
        }

        // 将Set转换为List
        List<String> uniqueNamesList = new ArrayList<>(uniqueNames);

        // 使用Gson将List转换为JSON字符串
        Gson gson = new Gson();
        return gson.toJson(uniqueNamesList);
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

//    @GetMapping("/addPrompt/{loginCode}/{promptName}/{promptContent}")
//    @ApiOperation("添加自定义prompt")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "loginCode",value = "登录码",required = true),
//    })
//    String addPrompt(
//            @RequestPart(value = "loginCode",required = true) String loginCode,
//            @RequestPart(value = "promptName")String promptName,
//            @RequestPart(value = "promptContent")String promptContent
//    ) throws IOException {
//        String openId=get_openId(loginCode);
//        if(Objects.equals(openId, "-1")){
//            return "请求失败，登录码不合法";
//        }
//        userMapper.addPrompt(openId,promptName,promptContent);
//        return "添加成功";
//    }


    @PostMapping("/VisionTalk")
    @ApiOperation("hololens进行对话")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "kind",value="调用AI服务类型，1为实景识别，2为文字识别"),
            @ApiImplicitParam(name = "photo",value = "照片",required = false,dataTypeClass = MultipartFile.class),
            @ApiImplicitParam(name = "question",value="问题"),
    })
    String VisionTalk(@RequestPart(value="kind",required = true) String kind,
                      @RequestPart(value="photo",required = false) MultipartFile photo,
                      @RequestPart(value="question",required = false)String question
    ) throws Exception {
        JsonArray jsonArray=new JsonArray();

        if(photo!=null){
            String path;
            path=uploadImg(photo);
            JsonObject picJson = new JsonObject();
            picJson.addProperty("url",path);
            jsonArray.add(picJson);
        }
        else return "未收到图片";
        if(question!=null){
            JsonObject questionJson = new JsonObject();
            questionJson.addProperty("user",question);
            jsonArray.add(questionJson);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject totJson=new JsonObject();
        totJson.addProperty("f",kind);
        totJson.addProperty("chat",gson.toJson(jsonArray));
        totJson.addProperty("type","talk");
        String requestBody =gson.toJson(totJson);
        String openId="114514";

        redisTemplate2.opsForValue().set(openId,requestBody);

        int wait_cnt=30*10;
        while(wait_cnt>0&& Boolean.FALSE.equals(redisTemplate3.opsForValue().getOperations().hasKey(openId))){
            wait_cnt--;
            Thread.sleep(100);
        }
        if(wait_cnt==0){
            return "请求超时，计算服务器未响应";
        }
        String response=redisTemplate3.opsForValue().get(openId);
        redisTemplate3.delete(openId);
        return response;
    }

    String get_openId(String loginCode) throws IOException {
        //String openId;
        String openId=userMapper.get_openId(loginCode);
        if(openId==null){
            if(loginCode.equals("114514")) {
                openId = "1919810";
            }
            else openId=wechatLoginUtil.checkOpenId(loginCode);
            if(openId==null){
                return "-1";
            }
            if(userMapper.check_user_ex(openId)==0){
                userMapper.Insert_User(openId,loginCode);
            }
            else userMapper.Update_LoginCode(openId,loginCode);
        }
        int min_cnt= userMapper.get_min_cnt(openId);
        System.out.println(min_cnt);
        if(min_cnt>=10){
            return "-2";
        }
        userMapper.add_cnt(openId);
        return openId;
    }
    @Scheduled(cron = "0 0/1 * * * ?")
    public void checkRedisHealth() {
        System.out.println("MAN!\n");
        userMapper.clear_cnt();
    }

}
