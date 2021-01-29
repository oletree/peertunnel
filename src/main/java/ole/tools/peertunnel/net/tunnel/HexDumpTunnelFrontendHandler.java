package ole.tools.peertunnel.net.tunnel;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ole.tools.peertunnel.net.PeerPipe;
import ole.tools.peertunnel.net.peer.PipeInfo;
import ole.tools.peertunnel.net.pkg.PeerHeader;
import ole.tools.peertunnel.net.pkg.PeerMessage;
import ole.tools.peertunnel.net.pkg.enums.EnPeerCommand;

public class HexDumpTunnelFrontendHandler extends ChannelInboundHandlerAdapter {
	private final int HEADER_VERSION = 0;
	
	private Logger logger = LoggerFactory.getLogger(HexDumpTunnelFrontendHandler.class);
	private PeerPipe peerPipe;
	private String pipeChannelId;

	public HexDumpTunnelFrontendHandler(PeerPipe peerPipe, String pipeChannelId) {
		this.peerPipe = peerPipe;
		this.pipeChannelId = pipeChannelId;
	}

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	Channel inboundChannel = ctx.channel();
		Channel pipeChannel = peerPipe.getPipeChannel(pipeChannelId);
		if(pipeChannel == null) {
			inboundChannel.close();
			return;
		}
		PipeInfo info = peerPipe.getPipeInfo(pipeChannelId);
		PeerHeader header = new PeerHeader(HEADER_VERSION,0, EnPeerCommand.CREATE_TUNNEL );
		header.setFrontChannelId(inboundChannel.id().asLongText());
		header.setPipeChannelId(pipeChannelId);
		PeerMessage msg = new PeerMessage(header, null);
		pipeChannel.writeAndFlush(msg);
		info.putTunnelChannel(header.getFrontChannelId(), inboundChannel);
    }
    
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object inData) throws Exception {
    	Channel pipeChannel = peerPipe.getPipeChannel(pipeChannelId);
    	Channel frontCh = ctx.channel();
    	if( !frontCh.isActive() ) return;
    	if( frontCh.id().asLongText() == null) {
    		frontCh.close();
    		return;
    	}
        ByteBuf in = (ByteBuf) inData;
        while(in.readableBytes() != 0) {
	        int size = in.readableBytes();
	        if(size > PeerHeader.BODY_SIZE ) {
	        	size = PeerHeader.BODY_SIZE;
	        }
	        byte[] body = new byte[size];
	        in.readBytes(body);
	        PeerHeader frontHeader = new PeerHeader(HEADER_VERSION, size, EnPeerCommand.SEND_TUNNEL);
	        frontHeader.setFrontChannelId(ctx.channel().id().asLongText());
	        frontHeader.setPipeChannelId(pipeChannelId);
	        logger.info("server: " + frontHeader.toString());
	        PeerMessage msg = new PeerMessage(frontHeader, body);
	        pipeChannel.writeAndFlush(msg);
        }
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    	String channelId = ctx.channel().id().asLongText();
    	logger.info(" inactivate : " + channelId );
    	PipeInfo info = peerPipe.getPipeInfo(pipeChannelId);
    	if(info == null) return;
    	Channel ch = info.getTunnelChannel(channelId);
    	if(ch != null) {
    		info.removeTunnelChannel(channelId);
	    	Channel pipeChannel = peerPipe.getPipeChannel(pipeChannelId);
	    	if(pipeChannel != null) {
				PeerHeader frontHeader = new PeerHeader(HEADER_VERSION,0, EnPeerCommand.REMOVE_TUNNEL );
				frontHeader.setFrontChannelId(channelId);
				frontHeader.setPipeChannelId(pipeChannelId);
				PeerMessage msg = new PeerMessage(frontHeader, null);
				pipeChannel.writeAndFlush(msg);
	    	}
    	}
    }
	
}
