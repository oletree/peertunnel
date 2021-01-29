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
	public Channel getPipeChannel(String pipeChannel) {
		PipeInfo info = pipeMap.get(pipeChannel);
		if(info == null) return null;
		return info.getPipeChannel();
	}
	
	@Override
	public void putPipeInfo(String pipeChannelId, PipeInfo ch) {
		pipeMap.put(pipeChannelId, ch);		
	}
	
	
	public PipeInfo getPipeInfo(String pipeChannelId) {
		return pipeMap.get(pipeChannelId);
	}
	
	public PipeInfo removePipeInfo(String pipeChannelId) {
		return pipeMap.remove(pipeChannelId);
	}


}
