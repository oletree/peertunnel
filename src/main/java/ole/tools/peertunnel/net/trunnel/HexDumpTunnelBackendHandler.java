package ole.tools.peertunnel.net.trunnel;

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

public class HexDumpTunnelBackendHandler extends ChannelInboundHandlerAdapter {
	private final int HEADER_VERSION = 1;
	
	private Logger logger = LoggerFactory.getLogger(HexDumpTunnelBackendHandler.class);
	private PeerPipe peerPipe;
	private PeerHeader header;
	private String pipeChannelId;

	public HexDumpTunnelBackendHandler(String pipeChannelId, PeerPipe peerPipe, PeerHeader header) {
		this.peerPipe = peerPipe;
		this.header = header;
		this.pipeChannelId = pipeChannelId;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		PipeInfo info = peerPipe.getPipeInfo(pipeChannelId);
		info.putTunnelChannel(header.getFrontChannelId(), ctx.channel());
	}
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object inData) throws Exception {
    	Channel pipeChannel = peerPipe.getPipeChannel(pipeChannelId);
        ByteBuf in = (ByteBuf) inData;
        while(in.readableBytes() != 0) {
            int size = in.readableBytes();
            if(size > PeerHeader.BODY_SIZE ) {
            	size = PeerHeader.BODY_SIZE;
            }
            byte[] body = new byte[size]; 
            in.readBytes(body);
            PeerHeader backHeader = new PeerHeader(HEADER_VERSION, size, EnPeerCommand.SEND_TUNNEL);
            backHeader.setFrontChannelId(header.getFrontChannelId());
            backHeader.setPipeChannelId(pipeChannelId);
            PeerMessage msg = new PeerMessage(backHeader, body);
            pipeChannel.writeAndFlush(msg).sync();        	
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    	logger.info(" inactivate : " + ctx.channel().id().asLongText() +"," + header.getFrontChannelId());
    	PipeInfo info = peerPipe.getPipeInfo(pipeChannelId);
    	Channel ch = info.getTunnelChannel(header.getFrontChannelId());
    	if(ch != null) {
    		info.removeTunnelChannel(header.getFrontChannelId());
	    	Channel pipeChannel = peerPipe.getPipeChannel(pipeChannelId);
	    	
			PeerHeader backHeader = new PeerHeader(HEADER_VERSION,0, EnPeerCommand.REMOVE_TUNNEL );
			backHeader.setFrontChannelId(header.getFrontChannelId());
			backHeader.setPipeChannelId(pipeChannelId);
			PeerMessage msg = new PeerMessage(backHeader, null);
			pipeChannel.writeAndFlush(msg);
    	}

    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	logger.info(" exception : " + ctx.channel().id().asLongText() +"," + header.getFrontChannelId());
    	PipeInfo info = peerPipe.getPipeInfo(pipeChannelId);
    	Channel ch = info.getTunnelChannel(header.getFrontChannelId());
    	if(ch != null) {
    		info.removeTunnelChannel(header.getFrontChannelId());
	    	Channel pipeChannel = peerPipe.getPipeChannel(pipeChannelId);
	    	
			PeerHeader backHeader = new PeerHeader(HEADER_VERSION,0, EnPeerCommand.REMOVE_TUNNEL );
			backHeader.setFrontChannelId(header.getFrontChannelId());
			backHeader.setPipeChannelId(pipeChannelId);
			PeerMessage msg = new PeerMessage(backHeader, null);
			pipeChannel.writeAndFlush(msg);
    	}
    	

    }
}
