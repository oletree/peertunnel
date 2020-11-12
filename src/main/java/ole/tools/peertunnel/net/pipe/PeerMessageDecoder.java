package ole.tools.peertunnel.net.pipe;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import ole.tools.peertunnel.net.pkg.PeerHeader;
import ole.tools.peertunnel.net.pkg.PeerMessage;
import ole.tools.peertunnel.net.pkg.enums.EnPeerCommand;

public class PeerMessageDecoder extends ByteToMessageDecoder {
	
	
	private Logger logger = LoggerFactory.getLogger(PeerMessageDecoder.class);
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		in.markReaderIndex();
		int version = in.readInt();
		int contentLength = in.readInt();
		int cmd = in.readInt();
		byte[] body = null;
		EnPeerCommand enCmd = EnPeerCommand.values()[cmd];
		byte[] frontChannelIdByte = new byte[PeerHeader.FRONT_CHANNEL_ID_SIZE];
		in.readBytes(frontChannelIdByte);
		String frontChannelId = new String(frontChannelIdByte);
		if(contentLength > 0 ) {
			if(in.readableBytes() < contentLength) {
				in.resetReaderIndex();
				return;
			}
			body = new byte[contentLength];
			in.readBytes(body);
		}
		
		PeerHeader header = new PeerHeader(version, contentLength, enCmd);
		logger.info("decode: " + enCmd.toString());
		header.setFrontChannelId(frontChannelId);
		PeerMessage message = new PeerMessage(header, body);
		
		out.add(message);
	}

	
}
