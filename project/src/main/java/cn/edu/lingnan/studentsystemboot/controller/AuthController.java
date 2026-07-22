package cn.edu.lingnan.studentsystemboot.controller;

import cn.edu.lingnan.studentsystemboot.common.Result;
import cn.edu.lingnan.studentsystemboot.pojo.User;
import cn.edu.lingnan.studentsystemboot.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody User loginUser, HttpSession session) {
        // 接收前端传的username、password
        User login = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (login == null) {
            // 对应原：账号或密码错误
            return Result.fail("账号或密码错误");
        }
        // 和原有逻辑一致：存入Session
        session.setAttribute("user", login);

        Map<String,Object> data = new HashMap<>();
        data.put("msg", "登录成功");
        data.put("user", login);
        return Result.success(data);
    }
}