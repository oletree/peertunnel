package ole.tools.peertunnel.schedule;

import java.time.Duration;
import java.time.LocalDateTime;
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
import ole.tools.peertunnel.net.peer.PeerTunnelFrontend;
import ole.tools.peertunnel.net.peer.PipeInfo;
import ole.tools.peertunnel.net.pkg.PeerHeader;
import ole.tools.peertunnel.net.pkg.PeerMessage;
import ole.tools.peertunnel.net.pkg.enums.EnPeerCommand;

@Component
public class PingSendTasks {

	private Logger logger = LoggerFactory.getLogger(PingSendTasks.class);
	
	@Autowired
	private PeerPipe peerTunnel;

	@Autowired
	private PeerTunnelProperties peerTunnelProperties;

	
	@Scheduled(fixedRate=60000)
	public void reConnectMessage() throws Exception {
		logger.debug("start Scheduler");
		if(!peerTunnelProperties.isServerMode()) {
			HashMap<String, PipeInfo> map = peerTunnel.getPipeChannelMap();
			
			for(Entry<String, PipeInfo> c : map.entrySet()) {
				if(c.getValue() == null ) map.remove(c.getKey());
				Channel ch = c.getValue().getPipeChannel();
				if( ! ch.isActive() ) {
					map.remove(c.getKey());
				}
			}
			if( map.isEmpty()  ) {
				HashMap<String, Channel> mapTunnel = peerTunnel.getTunnelChannelMap();
				for( Entry<String, Channel> v : mapTunnel.entrySet()) {
					String key = v.getKey();
					Channel tchannel = v.getValue();
					tchannel.close();
					mapTunnel.remove(key);
				}
				peerTunnel.start();
			}
		}
	}
	
	@Scheduled(fixedRate=60000)
	public void sendPingMessage() throws Exception {

		if(!peerTunnelProperties.isServerMode()) {
			HashMap<String, PipeInfo> map = peerTunnel.getPipeChannelMap();
			for(Entry<String, PipeInfo> c : map.entrySet()) {
				Channel ch = c.getValue().getPipeChannel();
				if( ch.isActive() ) {
					String pipeChannelId = c.getKey();
					PeerHeader header = new PeerHeader(0, 0, EnPeerCommand.PING);
					header.setPipeChannelId(pipeChannelId);
					header.setFrontChannelId(pipeChannelId);
					PeerMessage msg = new PeerMessage(header, null);
					ch.writeAndFlush(msg);
				}
			}
			
		}
	}
	
	@Scheduled(fixedRate=60000)
	public void removePipe() throws Exception {

		if(peerTunnelProperties.isServerMode()) {
			HashMap<String, PipeInfo> map = peerTunnel.getPipeChannelMap();
			for(Entry<String, PipeInfo> c : map.entrySet()) {
				PipeInfo info = c.getValue();
				Channel ch = c.getValue().getPipeChannel();
				if( ch.isActive() ) {
					LocalDateTime now = LocalDateTime.now();
					Duration duration = Duration.between(info.getLastPingDate(), now);
					if( duration.getSeconds() > peerTunnelProperties.getServerInfo().getPingDuration() ) {
						map.remove(c.getKey());
						logger.info("remove frontend pipe " + c.getKey());
						PeerTunnelFrontend frontend = info.getFrontend();
						if(frontend == null) {
							logger.error("remove frontend pipe " + c.getKey());
						} else {
							frontend.getChannel().close();
						}
						
						ch.close();
						
					}
				}
			}
			
		}
	}
}
