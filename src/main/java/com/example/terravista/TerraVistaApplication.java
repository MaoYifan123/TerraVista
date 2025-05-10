package com.example.terravista;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class TerraVistaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TerraVistaApplication.class, args);
    }

}
