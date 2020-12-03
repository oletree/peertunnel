package ole.tools.peertunnel.net;

import java.util.HashMap;

import io.netty.channel.Channel;
import ole.tools.peertunnel.net.peer.PipeInfo;

public interface PeerPipe {

	
	public void start() throws Exception;
	
	public void putPipeChannel(String pipeChannelId, PipeInfo ch);
	
	public Channel getPipeChannel(String pipeChannelId);
	
	public PipeInfo getPipeInfo(String pipeChannelId);
	
	public PipeInfo removePipeChannel(String pipeChannelId);
	
	public HashMap <String, PipeInfo> getPipeChannelMap();
	
	public void putTunnelChannel(String channelId, Channel ch);

	public Channel getTunnelChannel(String channelId);
	
	public Channel removeTunnelChannel(String channelId);
	
	public HashMap <String, Channel> getTunnelChannelMap();
	
	

}
