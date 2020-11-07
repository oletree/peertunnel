package ole.tools.peertunnel.net.server;

import javax.annotation.PreDestroy;

import io.netty.bootstrap.ServerBootstrap;
import ole.tools.peertunnel.net.PeerTunnel;

public class PeerTunnelServer implements PeerTunnel {

	 ServerBootstrap serverBootstrap = new ServerBootstrap();
	
	@PreDestroy
	 public void onDestroy() throws Exception {
		
	}
}
