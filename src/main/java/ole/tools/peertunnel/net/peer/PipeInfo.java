package ole.tools.peertunnel.net.peer;

import java.time.LocalDateTime;

import io.netty.channel.Channel;

public class PipeInfo {
	
	private String pipeChannelId;
	private Channel pipeChannel;
	private LocalDateTime lastPingDate;
	private PeerTunnelFrontend frontend;
	
	
	public PipeInfo(String pipeChannelId, Channel pipeChannel) {
		this.pipeChannelId = pipeChannelId;
		this.pipeChannel = pipeChannel;
		lastPingDate = LocalDateTime.now();
	}
	
	public String getPipeChannelId() {
		return pipeChannelId;
	}
	public void setPipeChannelId(String pipeChannelId) {
		this.pipeChannelId = pipeChannelId;
	}
	public Channel getPipeChannel() {
		return pipeChannel;
	}
	public void setPipeChannel(Channel pipeChannel) {
		this.pipeChannel = pipeChannel;
	}
	public LocalDateTime getLastPingDate() {
		return lastPingDate;
	}
	public void setLastPingDate(LocalDateTime lastPingDate) {
		this.lastPingDate = lastPingDate;
	}

	public PeerTunnelFrontend getFrontend() {
		return frontend;
	}

	public void setFrontend(PeerTunnelFrontend frontend) {
		this.frontend = frontend;
	}

}
