package ole.tools.peertunnel.net.peer;

import javax.annotation.PreDestroy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ole.tools.peertunnel.conf.PeerTunnelProperties;
import ole.tools.peertunnel.conf.PeerTunnelProperties.ServerInfo;

public class PeerPipeServer extends AbstractPeerPipe {


	PeerTunnelProperties prop;
	private int bossCount = 1;
	private int workerCount = 10;
	private int port;

	ServerBootstrap serverBootstrap = new ServerBootstrap();
	EventLoopGroup bossGroup = null;
	EventLoopGroup workerGroup = null;
	Channel ch = null;

	public PeerPipeServer(PeerTunnelProperties prop) {
		this.prop = prop;

		ServerInfo info = prop.getServerInfo();
		port = info.getPort();

	}

	@PreDestroy
	public void onDestroy() throws Exception {
		if (bossGroup != null)
			bossGroup.shutdownGracefully();
		if (workerGroup != null)
			workerGroup.shutdownGracefully();
	}

	public void start() throws Exception {

		bossGroup = new NioEventLoopGroup(bossCount);
		workerGroup = new NioEventLoopGroup(workerCount);

		// serverBootstrap.option(ChannelOption.SO_SNDBUF, 20480);
		// serverBootstrap.option(ChannelOption.SO_RCVBUF, 20480);
		//serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new PeerMesaageInitializer(this, prop));

		// Bind the corresponding port number and start the connection on the listening
		// port
		serverBootstrap.bind(port).sync();
	}


}
