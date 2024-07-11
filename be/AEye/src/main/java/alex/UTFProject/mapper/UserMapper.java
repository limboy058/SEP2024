package alex.UTFProject.mapper;
import alex.UTFProject.entity.User;
import alex.UTFProject.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper extends MyMapper<User> {

    List<User> getAllUser();

    void uploadUserImgByName(@Param("name")String name,@Param("photopath")String photopath);

    User getUserByName(@Param("name")String name);

    void deleteUser(@Param("name")String name, @Param("password")String password);

    void changeUserPassword(@Param("name")String name, @Param("email")String email,@Param("newPassword")String newPassword);

    String getPhotoPathByName(@Param("name")String name);

    void changeUserInformation(@Param("name")String name,
                               @Param("password")String password,
                               @Param("email")String email,
                               @Param("signature")String signature,
                               @Param("oldName")String oldName);

    @Deprecated
    void insertUser(@Param("name")String name, @Param("password")String password, @Param("email") String email);
}
