package ole.tools.peertunnel.net.peer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import ole.tools.peertunnel.net.PeerPipe;
import ole.tools.peertunnel.net.trunnel.HexDumpTunnelFrontendHandler;


public class PeerTunnelFrontendInitializer extends ChannelInitializer<SocketChannel> {


	private String pipeChannelId;
	private PeerPipe peerPipe;

	
	
	public PeerTunnelFrontendInitializer(PeerPipe peerPipe, String pipeChannelId) {
		super();
		this.peerPipe = peerPipe;
		this.pipeChannelId = pipeChannelId;
	}


	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new HexDumpTunnelFrontendHandler(peerPipe, pipeChannelId));
		
	}


}
