package pers.kun.reptilemgr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author liuzhikun
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ReptileMgrApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReptileMgrApplication.class);
    }
}
