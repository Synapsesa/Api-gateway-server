package com.synapse.api_gateway_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(excludeName = {
	"org.springframework.cloud.gateway.config.GatewayRedisAutoConfiguration"
})
public class ApiGatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayServerApplication.class, args);
	}

}
