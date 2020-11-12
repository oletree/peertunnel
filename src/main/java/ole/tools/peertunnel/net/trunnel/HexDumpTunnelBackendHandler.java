package ole.tools.peertunnel.net.trunnel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ole.tools.peertunnel.net.PeerPipe;
import ole.tools.peertunnel.net.pkg.PeerHeader;
import ole.tools.peertunnel.net.pkg.PeerMessage;
import ole.tools.peertunnel.net.pkg.enums.EnPeerCommand;

public class HexDumpTunnelBackendHandler extends ChannelInboundHandlerAdapter {

	
	private Logger logger = LoggerFactory.getLogger(HexDumpTunnelBackendHandler.class);
	private PeerPipe peerPipe;
	private PeerHeader header;

	public HexDumpTunnelBackendHandler(PeerPipe peerPipe, PeerHeader header) {
		this.peerPipe = peerPipe;
		this.header = header;
	}
	
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object inData) throws Exception {
    	Channel pipeChannel = peerPipe.getChannel();
        ByteBuf in = (ByteBuf) inData;
        int size = in.readableBytes();
        if(size > 1024 ) {
        	size = 1024;
        }
        byte[] body = new byte[size]; 
        in.readBytes(body);
        PeerHeader backHeader = new PeerHeader(1, size, EnPeerCommand.SEND_TUNNEL);
        backHeader.setFrontChannelId(header.getFrontChannelId());
        PeerMessage msg = new PeerMessage(backHeader, body);
        pipeChannel.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    	logger.info(" inactivate : " + ctx.channel().id().asLongText() +"," + header.getFrontChannelId());
    	peerPipe.removeTunnelChannel(header.getFrontChannelId());
    	Channel pipeChannel = peerPipe.getChannel();
    	
		PeerHeader header = new PeerHeader(1,0, EnPeerCommand.REMOVE_TUNNEL );
		header.setFrontChannelId(header.getFrontChannelId());
		PeerMessage msg = new PeerMessage(header, null);
		pipeChannel.writeAndFlush(msg);

    }
}
