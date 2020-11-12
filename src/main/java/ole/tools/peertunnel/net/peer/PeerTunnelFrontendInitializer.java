package ole.tools.peertunnel.net.peer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import ole.tools.peertunnel.net.PeerPipe;
import ole.tools.peertunnel.net.trunnel.HexDumpTunnelFrontendHandler;


public class PeerTunnelFrontendInitializer extends ChannelInitializer<SocketChannel> {


	private PeerPipe peerPipe;

	
	
	public PeerTunnelFrontendInitializer(PeerPipe peerPipe) {
		super();
		this.peerPipe = peerPipe;
	}


	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new HexDumpTunnelFrontendHandler(peerPipe));
		
	}


}
