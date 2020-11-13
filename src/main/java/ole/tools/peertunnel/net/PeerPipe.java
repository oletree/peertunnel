package ole.tools.peertunnel.net;

import io.netty.channel.Channel;

public interface PeerPipe {

	
	public void start() throws Exception;
	
	public void setPipeChannel(String pipeChannelId, Channel ch);
	
	public Channel getPipeChannel(String pipeChannelId);
	
	
	public void putTunnelChannel(String channelId, Channel ch);

	public Channel getTunnelChannel(String channelId);
	
	public void removeTunnelChannel(String channelId);

}
