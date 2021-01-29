package ole.tools.peertunnel.net.peer;

import javax.annotation.PreDestroy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import ole.tools.peertunnel.conf.PeerTunnelProperties;
import ole.tools.peertunnel.conf.PeerTunnelProperties.ServerInfo;
import ole.tools.peertunnel.net.pkg.PeerHeader;
import ole.tools.peertunnel.net.pkg.PeerMessage;
import ole.tools.peertunnel.net.pkg.enums.EnPeerCommand;

public class PeerPipeClient extends AbstractPeerPipe {
	
	PeerTunnelProperties prop;
	private String ip;
	private int port;

	Bootstrap  bootstrap = new Bootstrap();
	EventLoopGroup group = null;
	
	Channel ch = null;

	public PeerPipeClient(PeerTunnelProperties prop) {
		this.prop = prop;
		ServerInfo info = prop.getServerInfo();
		port = info.getPort();
		ip = info.getIp();
		
		
		group = new NioEventLoopGroup();

		//bootstrap.option(ChannelOption.SO_SNDBUF, 20480);
		//bootstrap.option(ChannelOption.SO_RCVBUF, 20480);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.group(group).channel(NioSocketChannel.class).handler(new PeerMesaageInitializer(this,prop));
	}
	
	@PreDestroy
	public void onDestroy() throws Exception {
		if(group != null) group.shutdownGracefully();
	}

	public void start() throws Exception {
		// Bind the corresponding port number and start the connection on the listening
		// port
		ch = bootstrap.connect(ip, port).sync().channel();
		
		if(ch.isActive()) {
			String pipeChannelId = ch.id().asLongText();
			String openPortNum = String.valueOf(prop.getTunnel().getOpenPort());
			byte[] msgBody = openPortNum.getBytes();
			PipeInfo info = new PipeInfo(pipeChannelId, ch, prop.getServerInfo().getPingDuration());
			putPipeInfo(pipeChannelId, info);
			PeerHeader header = new PeerHeader(3, msgBody.length, EnPeerCommand.CREATE_PIPE_FRONTEND);
			header.setPipeChannelId(pipeChannelId);
			header.setFrontChannelId(pipeChannelId);
			PeerMessage msg = new PeerMessage(header, msgBody);
			ch.writeAndFlush(msg);
		}

	}



}
