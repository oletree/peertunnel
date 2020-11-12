package ole.tools.peertunnel.net.peer;

import java.util.HashMap;

import javax.annotation.PreDestroy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import ole.tools.peertunnel.conf.PeerTunnelProperties;
import ole.tools.peertunnel.conf.PeerTunnelProperties.ServerInfo;
import ole.tools.peertunnel.net.PeerPipe;

public class PeerPipeClient implements PeerPipe {
	
	HashMap <String, Channel> tunnelMap = new HashMap<>();
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
	}
	
	@PreDestroy
	public void onDestroy() throws Exception {
		if(group != null) group.shutdownGracefully();
	}

	public void start() throws Exception {

		group = new NioEventLoopGroup();

		//bootstrap.option(ChannelOption.SO_SNDBUF, 20480);
		//bootstrap.option(ChannelOption.SO_RCVBUF, 20480);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.group(group).channel(NioSocketChannel.class).handler(new PeerMesaageInitializer(this,prop));

		// Bind the corresponding port number and start the connection on the listening
		// port
		ch = bootstrap.connect(ip, port).sync().channel();

	}

	@Override
	public Channel getChannel() {
		return ch;
	}
	
	
	@Override
	public void putTunnelChannel(String channelId, Channel ch) {
		tunnelMap.put(channelId, ch);
	}
	@Override
	public Channel getTunnelChannel(String channelId) {
		return tunnelMap.get(channelId);
	}
	@Override
	public void removeTunnelChannel(String channelId) {
		tunnelMap.remove(channelId);
	}

	@Override
	public void setChannel(Channel ch) {
		// TODO Auto-generated method stub
		
	}

}
