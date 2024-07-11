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
    private String  id;

    @Id
    @ApiModelProperty("用户openId")
    private String openId;

}
