package ole.tools.peertunnel.net;

import java.util.HashMap;

import io.netty.channel.Channel;
import ole.tools.peertunnel.net.peer.PeerTunnelFrontend;

public interface PeerPipe {

	
	public void putFrontend(String pipeChannelId, PeerTunnelFrontend frontend);
	
	public PeerTunnelFrontend removeFrontend(String pipeChannelId);
	
	public PeerTunnelFrontend getFrontend(String pipeChannelId);
	public HashMap <String, PeerTunnelFrontend> getFrontendMap();
	
	public void start() throws Exception;
	
	public void putPipeChannel(String pipeChannelId, Channel ch);
	
	public Channel getPipeChannel(String pipeChannelId);
	
	public Channel removePipeChannel(String pipeChannelId);
	
	public HashMap <String, Channel> getPipeChannelMap();
	
	public void putTunnelChannel(String channelId, Channel ch);

	public Channel getTunnelChannel(String channelId);
	
	public Channel removeTunnelChannel(String channelId);
	
	

}
