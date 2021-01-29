package ole.tools.peertunnel.net.pipe;

import java.time.LocalDateTime;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import ole.tools.peertunnel.conf.PeerTunnelProperties;
import ole.tools.peertunnel.net.PeerPipe;
import ole.tools.peertunnel.net.peer.EnPipeStatus;
import ole.tools.peertunnel.net.peer.PeerTunnelFrontend;
import ole.tools.peertunnel.net.peer.PipeInfo;
import ole.tools.peertunnel.net.pkg.PeerHeader;
import ole.tools.peertunnel.net.pkg.PeerMessage;
import ole.tools.peertunnel.net.pkg.enums.EnPeerCommand;
import ole.tools.peertunnel.net.tunnel.HexDumpTunnelBackendHandler;

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
			readPingMessage(ctx, msg);
			break;
		case PING_DONE:
			readPingDoneMessage(ctx, msg);
			break;
		case CREATE_TUNNEL:
			createBackendClient(msg);
			break;
		case SEND_TUNNEL:
			sendTunnelData(msg);
			break;
		case REMOVE_TUNNEL:
			removeBackendClient(msg);
			break;
		case CREATE_PIPE_FRONTEND:
			createPipeFrontend(ctx, msg);
			break;
		case CREATE_PIPE_FRONTEND_DONE:
			createPipeFrontendDone(ctx, msg);
			break;
		default:
			throw new RuntimeException("command working not Setting");
		}
	}

	private void readPingMessage(ChannelHandlerContext ctx, PeerMessage msg) {
		PipeInfo p = peerPipe.getPipeInfo(msg.getHeader().getPipeChannelId());
		if(p == null) {
			logger.info("ping not found pipeInfo ");
			ctx.channel().close();
		}else {
			p.setLastPingDate(LocalDateTime.now());
			PeerHeader header = new PeerHeader(0, 0, EnPeerCommand.PING_DONE);
			header.setPipeChannelId(p.getPipeChannelId());
			header.setFrontChannelId(p.getPipeChannelId());
			PeerMessage msgDone = new PeerMessage(header, null);
			ctx.channel().writeAndFlush(msgDone);
		}
		
	}
	private void readPingDoneMessage(ChannelHandlerContext ctx, PeerMessage msg) {
		PipeInfo p = peerPipe.getPipeInfo(msg.getHeader().getPipeChannelId());
		if(p == null) {
			logger.info("ping doen not found pipeInfo ");
			ctx.channel().close();
		}else {
			p.setLastPingDate(LocalDateTime.now());
		}
		
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {

		logger.info(" inactivate pipe : ");
		for (Entry<String, PipeInfo> cm : peerPipe.getPipeChannelMap().entrySet()) {
			PipeInfo info = cm.getValue();
			Channel channel = info.getPipeChannel();
			if (!channel.isActive()) {
				logger.info("remove pipe " + cm.getKey());
				peerPipe.removePipeInfo(cm.getKey());
				info.closeAll();
			}
			
		}
	}

	private void createPipeFrontend(ChannelHandlerContext ctx, PeerMessage msg) {
		String portStr = new String(msg.getBody());
		PeerHeader header = msg.getHeader();
		int portNumber = Integer.parseInt(portStr);
		String pipeChannelId = header.getPipeChannelId();
		PeerTunnelFrontend frontend = new PeerTunnelFrontend(portNumber, peerPipe, pipeChannelId);
		try {
			frontend.start();
			PipeInfo info = new PipeInfo(header.getPipeChannelId(), ctx.channel(),prop.getServerInfo().getPingDuration() );
			info.setFrontend(frontend);
			info.setStatus(EnPipeStatus.PIPE_TUNNEL);
			peerPipe.putPipeInfo(pipeChannelId, info);
			
			PeerHeader doneHeader = new PeerHeader(0, 0, EnPeerCommand.CREATE_PIPE_FRONTEND_DONE);
			doneHeader.setPipeChannelId(pipeChannelId);
			doneHeader.setFrontChannelId(pipeChannelId);
			PeerMessage msgDone = new PeerMessage(doneHeader, null);
			ctx.channel().writeAndFlush(msgDone);

		} catch (InterruptedException e) {
			logger.error("FrontEnd Start Error", e);
			ctx.channel().close();
		}
				
	}
	private void createPipeFrontendDone(ChannelHandlerContext ctx, PeerMessage msg) {
		PeerHeader header = msg.getHeader();
		PipeInfo info = peerPipe.getPipeInfo(header.getPipeChannelId());
		if(info == null) {
			logger.error("pipeInfo not Found {}", header.getPipeChannelId());
			ctx.channel().close();
		}else {
			info.setStatus(EnPipeStatus.PIPE_TUNNEL);
		}
	}

	private void removeBackendClient(PeerMessage msg) {
		PeerHeader header = msg.getHeader();
		String pipeChannelId = header.getPipeChannelId();
		PipeInfo info = peerPipe.getPipeInfo(pipeChannelId);
		logger.info("removeTunnel:" + header.toString());
		Channel c = info.removeTunnelChannel(header.getFrontChannelId());
		if (c != null) {
			c.close();
		}
	}

	private void sendTunnelData(PeerMessage msg) throws InterruptedException {
		logger.info("sendTunnelData:" + msg.getHeader().getContentLength());
		PeerHeader header = msg.getHeader();
		String pipeChannelId = header.getPipeChannelId();
		PipeInfo info = peerPipe.getPipeInfo(pipeChannelId);
		Channel channel = info.getTunnelChannel(msg.getHeader().getFrontChannelId());
		if (channel != null) {
			ByteBuf body = Unpooled.copiedBuffer(msg.getBody());
			channel.writeAndFlush(body).sync();
		}else {
			logger.error("channel Not Found");
		}
	}

	private void createBackendClient(PeerMessage msg) {

		Bootstrap bootstrap = new Bootstrap();
		// bootstrap.option(ChannelOption.SO_SNDBUF, 10240);
		// bootstrap.option(ChannelOption.SO_RCVBUF, 10240);
		// bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		PeerHeader header = msg.getHeader();
		String pipeChannelId = header.getPipeChannelId();
		PipeInfo info = peerPipe.getPipeInfo(pipeChannelId);
		bootstrap.group(info.getBackendGroup()).channel(NioSocketChannel.class)
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
			info.putTunnelChannel(msg.getHeader().getFrontChannelId(), channel);
		} catch (InterruptedException e) {
			logger.error("createBackend Error", e);
			Channel pipe = peerPipe.getPipeChannel(pipeChannelId);
			// remove tunnel Connection
			PeerHeader rmHeader = new PeerHeader(4, 0, EnPeerCommand.REMOVE_TUNNEL);
			rmHeader.setPipeChannelId(pipeChannelId);
			rmHeader.setFrontChannelId(msg.getHeader().getFrontChannelId());
			PeerMessage rmMsg = new PeerMessage(rmHeader, null);
			pipe.writeAndFlush(rmMsg);


		}
		
		

	}

	

}
