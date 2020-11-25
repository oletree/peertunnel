package ole.tools.peertunnel.net.pipe;

import java.util.Map.Entry;

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
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import ole.tools.peertunnel.conf.PeerTunnelProperties;
import ole.tools.peertunnel.net.PeerPipe;
import ole.tools.peertunnel.net.peer.PeerTunnelFrontend;
import ole.tools.peertunnel.net.pkg.PeerHeader;
import ole.tools.peertunnel.net.pkg.PeerMessage;
import ole.tools.peertunnel.net.pkg.enums.EnPeerCommand;
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
		switch (header.getCmd()) {
		case PING:
			logger.debug("get Ping");
			break;
		case CREATE_TUNNEL:
			createBackendClient(ctx.channel().id().asLongText(), msg);
			break;
		case SEND_TUNNEL:
			sendTunnelData(msg);
			break;
		case REMOVE_TUNNEL:
			removeBackendClient(msg);
			break;
		case CREATE_PIPE:
			createPipeFrontend(ctx, msg);
			break;
		default:
			throw new RuntimeException("command working not Setting");
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {

		logger.info(" inactivate pipe : ");
		for (Entry<String, Channel> cm : peerPipe.getPipeChannelMap().entrySet()) {

			if (!cm.getValue().isActive()) {
				logger.info("remove pipe " + cm.getKey());
				peerPipe.removePipeChannel(cm.getKey());
				PeerTunnelFrontend pf = peerPipe.getFrontend(cm.getKey());
				pf.getChannel().close();
			}
		}
	}

	private void createPipeFrontend(ChannelHandlerContext ctx, PeerMessage msg) {
		String portStr = new String(msg.getBody());
		PeerHeader header = msg.getHeader();
		int portNumber = Integer.parseInt(portStr);
		PeerTunnelFrontend frontend = new PeerTunnelFrontend(portNumber, peerPipe, header.getPipeChannelId());
		try {
			frontend.start();
		} catch (InterruptedException e) {
			logger.error("FrontEnd Start Error", e);
			ctx.channel().close();
		}
		peerPipe.putFrontend(header.getPipeChannelId(), frontend);
		peerPipe.putPipeChannel(header.getPipeChannelId(), ctx.channel());

	}

	private void removeBackendClient(PeerMessage msg) {
		PeerHeader header = msg.getHeader();
		logger.info("removeTunnel:" + header.toString());
		Channel c = peerPipe.getTunnelChannel(header.getFrontChannelId());
		if (c != null) {
			peerPipe.removeTunnelChannel(header.getFrontChannelId());
			c.close();
		}
	}

	private void sendTunnelData(PeerMessage msg) throws InterruptedException {
		logger.info("sendTunnelData:" + msg.getHeader().getContentLength());
		Channel channel = peerPipe.getTunnelChannel(msg.getHeader().getFrontChannelId());
		if (channel != null) {
			ByteBuf body = Unpooled.copiedBuffer(msg.getBody());
			channel.writeAndFlush(body).sync();
		}else {
			logger.error("channel Not Found");
		}
	}

	private void createBackendClient(String pipeChannelId, PeerMessage msg) {

		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup group = null;
		group = new NioEventLoopGroup();

		// bootstrap.option(ChannelOption.SO_SNDBUF, 10240);
		// bootstrap.option(ChannelOption.SO_RCVBUF, 10240);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.group(group).channel(NioSocketChannel.class)
				.handler(new HexDumpTunnelBackendHandler(pipeChannelId, peerPipe, msg.getHeader()));

		// Bind the corresponding port number and start the connection on the listening
		// port
		Channel channel;
		try {
			channel = bootstrap.connect(prop.getTunnel().getConnectIp(), prop.getTunnel().getConnectPort())
					.addListener(new FutureListener<Void>() {

						@Override
						public void operationComplete(Future<Void> future) throws Exception {
							if(! future.isSuccess() ) {
								logger.error("connect Error", future.cause());
								Channel pipe = peerPipe.getPipeChannel(pipeChannelId);
								// remove tunnel Connection
								PeerHeader header = new PeerHeader(4, 0, EnPeerCommand.REMOVE_TUNNEL);
								header.setPipeChannelId(pipeChannelId);
								header.setFrontChannelId(msg.getHeader().getFrontChannelId());
								PeerMessage rmMsg = new PeerMessage(header, null);
								pipe.writeAndFlush(rmMsg);
							}
						}

					}).sync().channel();
			logger.info("create Backend Ok");
			peerPipe.putTunnelChannel(msg.getHeader().getFrontChannelId(), channel);
		} catch (InterruptedException e) {
			logger.error("createBackend Error", e);
			Channel pipe = peerPipe.getPipeChannel(pipeChannelId);
			// remove tunnel Connection
			PeerHeader header = new PeerHeader(4, 0, EnPeerCommand.REMOVE_TUNNEL);
			header.setPipeChannelId(pipeChannelId);
			header.setFrontChannelId(msg.getHeader().getFrontChannelId());
			PeerMessage rmMsg = new PeerMessage(header, null);
			pipe.writeAndFlush(rmMsg);


		}
		
		

	}

	

}
