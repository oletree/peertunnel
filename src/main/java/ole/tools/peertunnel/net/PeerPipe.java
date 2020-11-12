package ole.tools.peertunnel.net;

import io.netty.channel.Channel;

public interface PeerPipe {

	
	public void start() throws Exception;
	
	public void setChannel(Channel ch);
	
	public Channel getChannel();
	
	public void putTunnelChannel(String channelId, Channel ch);

	public Channel getTunnelChannel(String channelId);
	
	public void removeTunnelChannel(String channelId);

}
