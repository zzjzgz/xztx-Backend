package xyz.zzj.springbootxztxbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
//开始定时任务
@EnableScheduling
//@MapperScan("xyz.zzj.springbootusercenter.mapper")
public class SpringbootXztxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootXztxApplication.class, args);
    }

}
