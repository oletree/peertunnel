package ole.tools.peertunnel.net.peer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map.Entry;

import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;

public class PipeInfo {
	
	private String pipeChannelId;
	private Channel pipeChannel;
	private LocalDateTime lastPingDate;
	private PeerTunnelFrontend frontend;
	private HashMap<String, Channel> tunnelMap = new HashMap<>();
	private NioEventLoopGroup backandGroup = new NioEventLoopGroup();
	
	
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
	

	public void putTunnelChannel(String channelId, Channel ch) {
		tunnelMap.put(channelId, ch);
	}

	public Channel getTunnelChannel(String channelId) {
		return tunnelMap.get(channelId);
	}
	

	public Channel removeTunnelChannel(String channelId) {
		return tunnelMap.remove(channelId);
	}
	
	public NioEventLoopGroup getBackendGroup() {
		return backandGroup;
	}


	public void closeAll() {
		if(frontend != null)
			frontend.shutdown();
		if(backandGroup != null)
			backandGroup.shutdownGracefully();
		
		for( Entry<String, Channel> v : tunnelMap.entrySet()) {
			String key = v.getKey();
			Channel tchannel = v.getValue();
			tchannel.close();
			tunnelMap.remove(key);
		}

	}

}
