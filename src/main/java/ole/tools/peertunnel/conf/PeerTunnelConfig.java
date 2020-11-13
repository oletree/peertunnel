package ole.tools.peertunnel.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ole.tools.peertunnel.net.PeerPipe;
import ole.tools.peertunnel.net.peer.PeerPipeClient;
import ole.tools.peertunnel.net.peer.PeerPipeServer;
import ole.tools.peertunnel.net.peer.PeerTunnelFrontend;


@Configuration
public class PeerTunnelConfig {

	@Autowired
	private PeerTunnelProperties peerTunnelProperties;
	
	//private PeerTunnelFrontend peertunnelfrontend;
	
	
	@Bean(name="peerTunnel")
	public PeerPipe createPeerTunnel() throws Exception {
		PeerPipe retval;
		if(peerTunnelProperties.isServerMode()) {
			retval = new PeerPipeServer(peerTunnelProperties);
		}else {
			retval = new PeerPipeClient(peerTunnelProperties);
		}
		retval.start();
		
		return retval;
	}
	
	
}
