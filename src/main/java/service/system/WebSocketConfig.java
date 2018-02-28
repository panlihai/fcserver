package service.system;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket封装配置类
 * 
 * */
@Configuration
public class WebSocketConfig {

	/**
	 * 加载ServerEndpointExporter
	 * 
	 * */
	@Bean
	public ServerEndpointExporter serverEndpointExporter(){
		return new ServerEndpointExporter();
	}
	
	
	
	
}
