package ole.tools.peertunnel.net.pipe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ole.tools.peertunnel.net.pkg.PeerHeader;
import ole.tools.peertunnel.net.pkg.PeerMessage;

public class PeerMessageEncoder extends MessageToByteEncoder<PeerMessage>{

	
	private Logger logger = LoggerFactory.getLogger(PeerMessageEncoder.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, PeerMessage msg, ByteBuf out) throws Exception {
		
		PeerHeader header = msg.getHeader();
		logger.info("encode: " + header.toString());
		header.writeToBytebuf(out);
		
		if(msg.getBody() != null)
			out.writeBytes(msg.getBody());

		
	}

}
