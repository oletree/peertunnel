package ole.tools.peertunnel.net.peer;

import java.util.HashMap;

import io.netty.channel.Channel;
import ole.tools.peertunnel.net.PeerPipe;

public abstract class AbstractPeerPipe implements PeerPipe {


	HashMap <String, Channel> tunnelMap = new HashMap<>();
	HashMap <String, PipeInfo> pipeMap = new HashMap<>();

	
	@Override
	public HashMap<String, PipeInfo> getPipeChannelMap() {
		return pipeMap;
	}
	
	@Override
	public void putPipeChannel(String pipeChannelId, PipeInfo ch) {
		pipeMap.put(pipeChannelId, ch);		
	}
	
	@Override
	public Channel getPipeChannel(String pipeChannel) {
		PipeInfo info = pipeMap.get(pipeChannel);
		if(info == null) return null;
		return info.getPipeChannel();
	}
	
	public PipeInfo getPipeInfo(String pipeChannelId) {
		return pipeMap.get(pipeChannelId);
	}

	@Override
	public PipeInfo removePipeChannel(String pipeChannelId) {
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
