package alex.UTFProject.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageParam {
    @ApiModelProperty("每页显示数量 (不小于0)")
    private Integer pageSize=10;

    @ApiModelProperty("页数 (不小于0)")
    private Integer pageNum=0;

    @ApiModelProperty("格式:字段名 排序规律,例:\"id asc\"")
    private String orderBy="id asc";
}