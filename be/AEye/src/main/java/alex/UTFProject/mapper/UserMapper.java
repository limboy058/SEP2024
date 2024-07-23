package alex.UTFProject.mapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("insert into utf.user_img values(#{openId},#{img_name},#{path});")
    void addUserImg(@Param("openId")String openId,@Param("img_name")String img_name,@Param("path")String path);
    @Delete("delete from utf.user_img where openId=#{openId} and img_name like concat(#{img_name},'_%');")
    void deleteUserImg(@Param("openId")String openId,@Param("img_name")String img_name);
    @Select("SELECT count(*) FROM utf.user_img where openId=#{openId} and img_name like concat(#{img_name},'_%');")
    Integer getUserImg(@Param("openId")String openId,@Param("img_name")String img_name);
    @Select("SELECT img_name FROM utf.user_img where openId=#{openId};")
    List<String> getUserAllImg(@Param("openId")String openId);

//    @Insert("insert into utf.user_prompt values(#{openId},#{prompt_name},#{prompt_content});")
//    void addPrompt(@Param("openId")String openId,@Param("prompt_name")String prompt_name,@Param("prompt_content")String prompt_content);
//
//    @Delete("insert into utf.user_prompt where openId=#{openId} and prompt_name=#{prompt_name};")
//    void delPrompt(@Param("openId")String openId,@Param("prompt_name")String prompt_name);
//
//    @Select("select prompt_name from utf.user_prompt where openId=#{openId}")
//    List<String> getPromptLst(@Param("openId") String openId);

    @Insert("insert into user values (#{openId},#{loginCode},0)")
    void Insert_User(@Param("openId")String openId,@Param("loginCode")String loginCode);

    @Update("update user set loginCode=#{loginCode} where openId=#{openId}")
    void Update_LoginCode(@Param("openId")String openId,@Param("loginCode")String loginCode);

    @Update("update user set min_cnt=min_cnt+1 where openId=#{openId}")
    void add_cnt(@Param("openId")String openId);

    @Update("update user set min_cnt=0;")
    void clear_cnt();

    @Select("select openId from user where loginCode=#{loginCode}")
    String get_openId(@Param("loginCode")String loginCode);

    @Select("select count(*) from user where openId=#{openId}")
    int check_user_ex(@Param("openId")String openId);

    @Select("select min_cnt from user where openId=#{openId}")
    int get_min_cnt(@Param("openId")String openId);

}
