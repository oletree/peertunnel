package ole.tools.peertunnel.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PeerTunnelConfig {

	@Autowired
	private PeerTunnelProperties peerTunnelProperties;
	
	
}
