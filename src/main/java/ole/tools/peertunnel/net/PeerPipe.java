package ole.tools.peertunnel.net;

import java.util.HashMap;

import io.netty.channel.Channel;
import ole.tools.peertunnel.net.peer.PipeInfo;

public interface PeerPipe {

	
	public void start() throws Exception;
	
	public void putPipeInfo(String pipeChannelId, PipeInfo ch);
	
	public Channel getPipeChannel(String pipeChannelId);
	
	public PipeInfo getPipeInfo(String pipeChannelId);
	
	public PipeInfo removePipeInfo(String pipeChannelId);
	
	public HashMap <String, PipeInfo> getPipeChannelMap();
	
}
