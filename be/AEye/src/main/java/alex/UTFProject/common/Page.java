package alex.UTFProject.common;


import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel
public class Page<T> {
    // 当前页
    private Integer pageNum = 0;
    // 每页显示的总条数
    private Integer pageSize = 10;
    @ApiModelProperty("总条数")
    private Long total;
    @ApiModelProperty("总页数")
    private int pages;
    @ApiModelProperty("分页结果")
    private List<T> list;

    public Page(PageInfo<T> pageInfo) {
        this.pageNum = pageInfo.getPageNum();
        this.pageSize = pageInfo.getPageSize();
        this.total = pageInfo.getTotal();
        this.pages = pageInfo.getPages();
        this.list = pageInfo.getList();
    }

    public Page(PageParam pageParam, Long total, int pages, List<T> list){
        this.pageNum = pageParam.getPageNum();
        this.pageSize = pageParam.getPageSize();
        this.total = total;
        this.pages = pages;
        this.list = list;
    }

}

