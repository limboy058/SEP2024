package alex.UTFProject.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("User 用户")
public class User implements Serializable {

    @Id
    @ApiModelProperty("用户id")
    private Long id;

    @Id
    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("会话id")
    private String sessionId;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("签名")
    private String signature;

    @ApiModelProperty("")
    Integer totalPostDay;

    @ApiModelProperty("")
    Integer continuousPostDay;

    @ApiModelProperty("头像地址")
    String photopath;

    @ApiModelProperty("用户类型 0为普通用户 1为管理员")
    Integer type;

    public User(String name,String password,String sessionId){
        this.name=name;
        this.password=password;
        this.sessionId = sessionId;
    }
}
