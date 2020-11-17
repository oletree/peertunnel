package ole.tools.peertunnel.net.peer;

import java.util.HashMap;

import io.netty.channel.Channel;
import ole.tools.peertunnel.net.PeerPipe;

public abstract class AbstractPeerPipe implements PeerPipe {

	HashMap <String, PeerTunnelFrontend> frontMap = new HashMap<>(); 
	HashMap <String, Channel> tunnelMap = new HashMap<>();
	HashMap <String, Channel> pipeMap = new HashMap<>();

	@Override
	public void putFrontend(String pipeChannelId, PeerTunnelFrontend frontend) {
		frontMap.put(pipeChannelId, frontend);
	}
	
	@Override
	public PeerTunnelFrontend removeFrontend(String pipeChannelId) {
		return frontMap.remove(pipeChannelId);
	}
	
	@Override
	public PeerTunnelFrontend getFrontend(String pipeChannelId) {
		return frontMap.get(pipeChannelId);
	}

	@Override
	public HashMap<String, PeerTunnelFrontend> getFrontendMap() {
		return frontMap;
	}
	
	
	@Override
	public HashMap<String, Channel> getPipeChannelMap() {
		return pipeMap;
	}
	
	@Override
	public void putPipeChannel(String pipeChannelId, Channel ch) {
		pipeMap.put(pipeChannelId, ch);		
	}
	
	@Override
	public Channel getPipeChannel(String pipeChannel) {
		return pipeMap.get(pipeChannel);
	}

	@Override
	public Channel removePipeChannel(String pipeChannelId) {
		return pipeMap.remove(pipeChannelId);		
	}

	
	@Override
	public void putTunnelChannel(String channelId, Channel ch) {
		tunnelMap.put(channelId, ch);
	}
	@Override
	public Channel getTunnelChannel(String channelId) {
		return tunnelMap.get(channelId);
	}
	
	
	@Override
	public Channel removeTunnelChannel(String channelId) {
		return tunnelMap.remove(channelId);
	}

}
