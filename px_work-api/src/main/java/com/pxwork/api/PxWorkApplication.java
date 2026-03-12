package com.pxwork.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// 暂时排除数据库自动配置，防止因为还没连数据库导致启动报错
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PxWorkApplication {

    public static void main(String[] args) {
        SpringApplication.run(PxWorkApplication.class, args);
        System.out.println("====== PxWork 后端启动成功！ ======");
    }
}
