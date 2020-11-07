package ole.tools.peertunnel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class PeerTunnelApp {

	 public static void main(String[] args) {
	        SpringApplication.run(PeerTunnelApp.class, args);
	    }


}
