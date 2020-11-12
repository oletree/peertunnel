package ole.tools.peertunnel.net.peer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import ole.tools.peertunnel.conf.PeerTunnelProperties;
import ole.tools.peertunnel.net.PeerPipe;
import ole.tools.peertunnel.net.pipe.PeerMessageDecoder;
import ole.tools.peertunnel.net.pipe.PeerMessageEncoder;
import ole.tools.peertunnel.net.pipe.PeerMessageHandler;

public class PeerMesaageInitializer extends ChannelInitializer<SocketChannel> {

	PeerPipe peerPipe;
	PeerTunnelProperties prop;
	
	public PeerMesaageInitializer(PeerPipe peerPipe, PeerTunnelProperties prop) {
		this.peerPipe = peerPipe;
		this.prop = prop;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {

		peerPipe.setChannel(ch);
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new PeerMessageEncoder());
		pipeline.addLast(new PeerMessageDecoder());
		pipeline.addLast(new PeerMessageHandler(peerPipe, prop));
	}

}
