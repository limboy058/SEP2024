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

}
