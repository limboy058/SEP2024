package alex.UTFProject.dto;

import alex.UTFProject.common.CommonErrorCode;
import alex.UTFProject.entity.User;
import alex.UTFProject.util.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * session缓存实体
 *
 * @author yan on 2020-02-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("SessionData 会话实体")
public class SessionData implements Serializable {

    /**
     * {@link User}
     */
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

    public SessionData(User user) {
        AssertUtil.notNull(user, CommonErrorCode.USER_NOT_EXIST);
        this.id = user.getId();
        this.continuousPostDay = user.getContinuousPostDay();
        this.email = user.getEmail();
        this.name = user.getName();
        this.password = user.getPassword();
        this.photopath = user.getPhotopath();
        this.sessionId = user.getSessionId();
        this.signature = user.getSignature();
        this.totalPostDay = user.getTotalPostDay();
        this.type = user.getType();
    }

}


