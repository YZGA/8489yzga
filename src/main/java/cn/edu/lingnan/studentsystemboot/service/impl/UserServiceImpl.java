package cn.edu.lingnan.studentsystemboot.service.impl;

import cn.edu.lingnan.studentsystemboot.mapper.UserMapper;
import cn.edu.lingnan.studentsystemboot.pojo.User;
import cn.edu.lingnan.studentsystemboot.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getUsername, username)
                .eq(User::getPassword, password);
        return userMapper.selectOne(wrapper);
    }
}