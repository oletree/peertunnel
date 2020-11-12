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

public class HexDumpTunnelFrontendHandler extends ChannelInboundHandlerAdapter {
	private final int HEADER_VERSION = 0;
	
	private Logger logger = LoggerFactory.getLogger(HexDumpTunnelFrontendHandler.class);
	PeerPipe peerPipe;

	public HexDumpTunnelFrontendHandler(PeerPipe peerPipe) {
		this.peerPipe = peerPipe;
	}

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	Channel inboundChannel = ctx.channel();
		Channel pipeChannel = peerPipe.getChannel();
		if(pipeChannel == null) {
			inboundChannel.close();
			return;
		}
		
		PeerHeader header = new PeerHeader(HEADER_VERSION,0, EnPeerCommand.CREATE_TUNNEL );
		header.setFrontChannelId(inboundChannel.id().asLongText());
		PeerMessage msg = new PeerMessage(header, null);
		pipeChannel.writeAndFlush(msg);
		peerPipe.putTunnelChannel(header.getFrontChannelId(), inboundChannel);
    }
    
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object inData) throws Exception {
    	Channel pipeChannel = peerPipe.getChannel();
    	Channel frontCh = ctx.channel();
    	if( !frontCh.isActive() ) return;
    	if( frontCh.id().asLongText() == null) {
    		frontCh.close();
    		return;
    	}
        ByteBuf in = (ByteBuf) inData;
        int size = in.readableBytes();
        if(size > 1024 ) {
        	size = 1024;
        }
        byte[] body = new byte[size];
        in.readBytes(body);
        PeerHeader frontHeader = new PeerHeader(HEADER_VERSION, size, EnPeerCommand.SEND_TUNNEL);
        frontHeader.setFrontChannelId(ctx.channel().id().asLongText());
        logger.info("server: " + frontHeader.toString());
        PeerMessage msg = new PeerMessage(frontHeader, body);
        pipeChannel.writeAndFlush(msg);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    	String channelId = ctx.channel().id().asLongText();
    	logger.info(" inactivate : " + channelId );
    	Channel ch = peerPipe.getTunnelChannel(channelId);
    	if(ch != null) {
	    	peerPipe.removeTunnelChannel(channelId);
	    	Channel pipeChannel = peerPipe.getChannel();
			PeerHeader frontHeader = new PeerHeader(HEADER_VERSION,0, EnPeerCommand.REMOVE_TUNNEL );
			frontHeader.setFrontChannelId(channelId);
			PeerMessage msg = new PeerMessage(frontHeader, null);
			pipeChannel.writeAndFlush(msg);
    	}
    }
	
}
