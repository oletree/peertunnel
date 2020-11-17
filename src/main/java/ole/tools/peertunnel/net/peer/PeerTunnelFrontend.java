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
import ole.tools.peertunnel.net.PeerPipe;

public class PeerTunnelFrontend {

	
	private Logger logger = LoggerFactory.getLogger(PeerTunnelFrontend.class);
	
	private int openPort;
	private PeerPipe peerPipe;
	private String pipeChannelId;
	

	public PeerTunnelFrontend(int openPort, PeerPipe peerPipe, String pipeChannelId) {
		super();
		this.openPort = openPort;
		this.peerPipe = peerPipe;
		this.pipeChannelId = pipeChannelId; 
	}

	ServerBootstrap serverBootstrap = new ServerBootstrap();
	EventLoopGroup bossGroup = null;
	EventLoopGroup workerGroup = null;
	Channel ch = null;
	

	@PreDestroy
	public void onDestroy() throws Exception {
		logger.info("frontend Distroyed");
		if (bossGroup != null)
			bossGroup.shutdownGracefully();
		if (workerGroup != null)
			workerGroup.shutdownGracefully();
	}
	
	public void start() throws InterruptedException {
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();

		serverBootstrap.option(ChannelOption.SO_SNDBUF, 10240);
		serverBootstrap.option(ChannelOption.SO_RCVBUF, 10240);
		serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
//	                    .handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new PeerTunnelFrontendInitializer(peerPipe, pipeChannelId));

		// Bind the corresponding port number and start the connection on the listening
		// port
		ch = serverBootstrap.bind(openPort).sync().channel();
		logger.info("FrontEnd Start portNumber : " + openPort);
	}
	
	public Channel getChannel() {
		return ch;
	}
	
}
