package alex.UTFProject.base;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author phoenix
 * @version 2022/1/19 19:21
 */

public interface MyMapper<T> extends Mapper<T>,MySqlMapper<T> {

}
