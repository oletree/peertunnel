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
import ole.tools.peertunnel.net.peer.EnPipeStatus;
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
				PipeInfo info = c.getValue();
				Channel ch = info.getPipeChannel();
				if( ! ch.isActive() ) {
					info.closeAll();
					map.remove(c.getKey());
				}
			}
			if( map.isEmpty()  ) {
				peerTunnel.start();
			}
		}
	}
	
	@Scheduled(fixedRate=30000)
	public void sendPingMessage() throws Exception {

		if(!peerTunnelProperties.isServerMode()) {
			HashMap<String, PipeInfo> map = peerTunnel.getPipeChannelMap();
			for(Entry<String, PipeInfo> c : map.entrySet()) {
				PipeInfo info = c.getValue();
				Channel ch = info.getPipeChannel();
				if( info.getStatus() == EnPipeStatus.PIPE_TUNNEL &&  ch.isActive() ) {
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
	
	@Scheduled(fixedRate=10000)
	public void checkPipeStatus() throws Exception {

		HashMap<String, PipeInfo> map = peerTunnel.getPipeChannelMap();
		for(Entry<String, PipeInfo> c : map.entrySet()) {
			PipeInfo info = c.getValue();
			if( info.isStatusOK() ) {
				logger.debug("is ok pipe channel id = " , info.getPipeChannelId() );
			}else {
				logger.error(info.getError());
				logger.info("remove frontend pipe by channel not Active");
				map.remove(c.getKey());
				info.closeAll();
			}
		}
			
		
	}
}
