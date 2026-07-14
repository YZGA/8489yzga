package cn.edu.lingnan.studentsystemboot.config;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 关闭联表SQL自动count优化，解决<if>标签解析报错
        pageInterceptor.setOptimizeJoin(false);
        pageInterceptor.setMaxLimit(-1L);
        interceptor.addInnerInterceptor(pageInterceptor);
        return interceptor;
    }
}