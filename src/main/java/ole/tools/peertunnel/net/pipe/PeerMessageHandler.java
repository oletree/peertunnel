package ole.tools.peertunnel.net.pipe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import ole.tools.peertunnel.conf.PeerTunnelProperties;
import ole.tools.peertunnel.net.PeerPipe;
import ole.tools.peertunnel.net.pkg.PeerHeader;
import ole.tools.peertunnel.net.pkg.PeerMessage;
import ole.tools.peertunnel.net.trunnel.HexDumpTunnelBackendHandler;

public class PeerMessageHandler extends SimpleChannelInboundHandler<PeerMessage> {

	
	PeerPipe peerPipe;
	PeerTunnelProperties prop;
	private Logger logger = LoggerFactory.getLogger(PeerMessageHandler.class);
	
	
	public PeerMessageHandler(PeerPipe peerPipe, PeerTunnelProperties prop) {
		super();
		this.peerPipe = peerPipe;
		this.prop = prop;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, PeerMessage msg) throws Exception {
		logger.info(msg.getHeader().toString());
		
		PeerHeader header = msg.getHeader();
		switch(header.getCmd()) {
			case CREATE_TUNNEL:
				createBackendClient(msg);
				break;
			case SEND_TUNNEL:
				sendTunnelData(msg);
				break;
			case REMOVE_TUNNEL:
				removeBackendClient(msg);
				break;
			default:
				throw new RuntimeException("command working not Setting");
		}
	}

	private void removeBackendClient(PeerMessage msg) {
		PeerHeader header = msg.getHeader();
		logger.info("removeTunnel" +header.toString() );
		Channel c = peerPipe.getTunnelChannel(header.getFrontChannelId());
		if(c != null) {
			peerPipe.removeTunnelChannel(header.getFrontChannelId());
			c.close();
		}
	}

	private void sendTunnelData(PeerMessage msg) {
		logger.info("sendTunnelData" + msg.getBody() );
		Channel channel = peerPipe.getTunnelChannel(msg.getHeader().getFrontChannelId());
		ByteBuf body = Unpooled.copiedBuffer(msg.getBody()); 
		channel.writeAndFlush(body);
	}

	private void createBackendClient(PeerMessage msg) throws InterruptedException {

		Bootstrap  bootstrap = new Bootstrap();
		EventLoopGroup group = null;
		group = new NioEventLoopGroup();

		bootstrap.option(ChannelOption.SO_SNDBUF, 1024);
		bootstrap.option(ChannelOption.SO_RCVBUF, 1024);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.group(group).channel(NioSocketChannel.class).handler(new HexDumpTunnelBackendHandler(peerPipe, msg.getHeader()));

		// Bind the corresponding port number and start the connection on the listening
		// port
		Channel  backendChannel = bootstrap.connect(prop.getTunnel().getConnectIp(), prop.getTunnel().getConnectPort()).sync().channel();
		
        peerPipe.putTunnelChannel(msg.getHeader().getFrontChannelId(), backendChannel);
	}

}
