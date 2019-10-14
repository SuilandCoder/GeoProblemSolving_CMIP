package cn.edu.njnu.geoproblemsolving;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class GeoproblemsolvingApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeoproblemsolvingApplication.class, args);
    }
}
