package ole.tools.peertunnel.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ole.tools.peertunnel.net.PeerPipe;
import ole.tools.peertunnel.net.peer.PeerPipeClient;
import ole.tools.peertunnel.net.peer.PeerPipeServer;


@Configuration
public class PeerTunnelConfig {

	@Autowired
	private PeerTunnelProperties peerTunnelProperties;
	
	@Bean(name="peerTunnel")
	public PeerPipe createPeerTunnel() throws Exception {
		PeerPipe retval;
		if(peerTunnelProperties.isServerMode()) {
			retval = new PeerPipeServer(peerTunnelProperties);
			retval.start();
		}else {
			retval = new PeerPipeClient(peerTunnelProperties);
		}
		
		
		return retval;
	}
	
	
}
