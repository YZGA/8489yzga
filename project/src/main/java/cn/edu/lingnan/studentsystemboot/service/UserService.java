package cn.edu.lingnan.studentsystemboot.service;

import cn.edu.lingnan.studentsystemboot.pojo.User;

public interface UserService {
    /**
     * 登录校验
     * @param username 用户名
     * @param password 密码
     * @return 查到用户返回对象，查不到返回null
     */
    User login(String username, String password);
}