package ole.tools.peertunnel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

import ole.tools.peertunnel.conf.PeerTunnelProperties;
import ole.tools.peertunnel.net.peer.PeerTunnelFrontend;

@EnableCaching
@SpringBootApplication
public class PeerTunnelApp {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(PeerTunnelApp.class, args);
	}

}
