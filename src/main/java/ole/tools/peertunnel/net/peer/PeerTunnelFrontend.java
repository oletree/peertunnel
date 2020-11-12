package ole.tools.peertunnel.net.peer;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ole.tools.peertunnel.conf.PeerTunnelProperties;
import ole.tools.peertunnel.net.PeerPipe;

public class PeerTunnelFrontend {

	
	private Logger logger = LoggerFactory.getLogger(PeerTunnelFrontend.class);
	
	private PeerTunnelProperties prop;
	private PeerPipe peerPipe;
	

	public PeerTunnelFrontend(PeerTunnelProperties prop, PeerPipe peerPipe) {
		super();
		this.prop = prop;
		this.peerPipe = peerPipe;
	}

	ServerBootstrap serverBootstrap = new ServerBootstrap();
	EventLoopGroup bossGroup = null;
	EventLoopGroup workerGroup = null;
	Channel ch = null;
	

	@PreDestroy
	public void onDestroy() throws Exception {
		if (bossGroup != null)
			bossGroup.shutdownGracefully();
		if (workerGroup != null)
			workerGroup.shutdownGracefully();
	}
	
	public void start() {
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();

		serverBootstrap.option(ChannelOption.SO_SNDBUF, 1024);
		serverBootstrap.option(ChannelOption.SO_RCVBUF, 1024);
		serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
//	                    .handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new PeerTunnelFrontendInitializer(peerPipe));

		// Bind the corresponding port number and start the connection on the listening
		// port
		try {
			ch = serverBootstrap.bind(prop.getTunnel().getOpenPort()).sync().channel();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error("Frontend Error",e);
		}
	}
	
}
