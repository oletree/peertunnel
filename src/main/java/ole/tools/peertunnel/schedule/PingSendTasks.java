package ole.tools.peertunnel.schedule;

import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import ole.tools.peertunnel.conf.PeerTunnelProperties;
import ole.tools.peertunnel.net.PeerPipe;

@Component
public class PingSendTasks {

	private Logger logger = LoggerFactory.getLogger(PingSendTasks.class);
	
	@Autowired
	private PeerPipe peerTunnel;

	@Autowired
	private PeerTunnelProperties peerTunnelProperties;

	
	@Scheduled(fixedRate=60000)
	public void sendPingMessage() throws Exception {
		logger.info("start Scheduler");
		if(!peerTunnelProperties.isServerMode()) {
			HashMap<String, Channel> map = peerTunnel.getPipeChannelMap();
			
			for(Entry<String, Channel> c : map.entrySet()) {
				if(c.getValue() == null ) map.remove(c.getKey());
				if( ! c.getValue().isActive() ) {
					map.remove(c.getKey());
				}
			}
			if( map.isEmpty()  ) {
				peerTunnel.start();
			}
		}
	}
}
