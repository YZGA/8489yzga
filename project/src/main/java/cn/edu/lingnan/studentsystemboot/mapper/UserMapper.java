package cn.edu.lingnan.studentsystemboot.mapper;

import cn.edu.lingnan.studentsystemboot.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}