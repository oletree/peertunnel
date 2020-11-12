package ole.tools.peertunnel.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ole.tools.peertunnel.net.PeerPipe;

@Component
public class PingSendTasks {

	@Autowired
	private PeerPipe peerTunnel;
	
	
	@Scheduled(fixedRate=6000)
	public void sendPingMessage() {
			
	}
}
