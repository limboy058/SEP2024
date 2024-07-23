package alex.UTFProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

import java.io.IOException;
import java.sql.SQLException;

@MapperScan("alex.UTFProject.mapper")
@SpringBootApplication
@EnableScheduling
public class SpringbootMybatisApplication {
	public static void main(String[] args){
		SpringApplication.run(SpringbootMybatisApplication.class, args);
	}
}
