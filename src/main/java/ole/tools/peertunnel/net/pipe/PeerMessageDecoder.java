package ole.tools.peertunnel.net.pipe;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import ole.tools.peertunnel.net.pkg.PeerHeader;
import ole.tools.peertunnel.net.pkg.PeerMessage;

public class PeerMessageDecoder extends ByteToMessageDecoder {
	
	
	private Logger logger = LoggerFactory.getLogger(PeerMessageDecoder.class);
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if( in.readableBytes() < PeerHeader.HEADER_SIZE) return;
		
		in.markReaderIndex();
		PeerHeader header = new PeerHeader(in);
		int contentLength = header.getContentLength();
		byte []body = null;
		
		if(header.getContentLength() > 0 ) {
			if(in.readableBytes() < contentLength) {
				in.resetReaderIndex();
				return;
			}
			body = new byte[contentLength];
			in.readBytes(body);
		}
		logger.info("decode: " + header.toString());
		PeerMessage message = new PeerMessage(header, body);
		
		out.add(message);
	}

	
}
