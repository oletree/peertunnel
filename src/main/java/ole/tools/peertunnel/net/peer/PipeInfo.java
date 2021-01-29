package ole.tools.peertunnel.net.peer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map.Entry;

import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;

public class PipeInfo {
	
	private String pipeChannelId;
	private Channel pipeChannel;
	private LocalDateTime runPipeDate;
	private LocalDateTime lastPingDate;
	private PeerTunnelFrontend frontend;
	private int pingDuration = 120;
	private HashMap<String, Channel> tunnelMap = new HashMap<>();
	private NioEventLoopGroup backandGroup = new NioEventLoopGroup();
	private String error;
	private EnPipeStatus status;
	
	public PipeInfo(String pipeChannelId, Channel pipeChannel, int pingDuration) {
		this.pipeChannelId = pipeChannelId;
		this.pipeChannel = pipeChannel;
		this.pingDuration = pingDuration;
		lastPingDate = LocalDateTime.now();
		runPipeDate =  LocalDateTime.now();
		status = EnPipeStatus.PIPE_NO_TUNNEL;
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
	
	public String getError() {
		return error;
	}
	
	
	public EnPipeStatus getStatus() {
		return status;
	}

	public void setStatus(EnPipeStatus status) {
		this.status = status;
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
	
	public boolean isStatusOK() {
		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(lastPingDate, now);
		if(status == EnPipeStatus.PIPE_NO_TUNNEL) {
			Duration runDuration = Duration.between(runPipeDate, now);
			if( runDuration.getSeconds() > 300 ) {
				error = "no tunnel 300 seconds";
				return false;
			}
		}
		if( status == EnPipeStatus.PIPE_TUNNEL && duration.getSeconds() > pingDuration ) {
			error = "ping time over last time (" + pingDuration + ")" ;
			return false;
		}
		return true;
	}

	public void closeAll() {
		
		if(pipeChannel != null)
			pipeChannel.close();
		
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
