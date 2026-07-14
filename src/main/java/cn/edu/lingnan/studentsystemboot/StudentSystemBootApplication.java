package cn.edu.lingnan.studentsystemboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.edu.lingnan.studentsystemboot.mapper")
public class StudentSystemBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(StudentSystemBootApplication.class, args);
	}
}