package ole.tools.peertunnel.net.pkg;

import io.netty.buffer.ByteBuf;
import ole.tools.peertunnel.net.pkg.enums.EnPeerCommand;

public class PeerHeader {
	// int = 4byte
	public static final int HEADER_SIZE = 4 + 4 + 4 + ( 60 * 2);
	public static final int CHANNEL_ID_SIZE = 60;
	public static final int BODY_SIZE = 4096;
	private int version;
	private int contentLength;
	private EnPeerCommand cmd;
	private String frontChannelId;
	private String pipeChannelId;

	public PeerHeader() {
		
	}
	
	public PeerHeader(ByteBuf in) {
		version = in.readInt();
		contentLength = in.readInt();
		int cmdInt = in.readInt();
		cmd = EnPeerCommand.values()[cmdInt];
		byte[] frontChannelIdByte = new byte[PeerHeader.CHANNEL_ID_SIZE];
		in.readBytes(frontChannelIdByte);
		byte[] pipeChannelIdByte = new byte[PeerHeader.CHANNEL_ID_SIZE];
		in.readBytes(pipeChannelIdByte);
		frontChannelId = new String(frontChannelIdByte);
		pipeChannelId = new String(pipeChannelIdByte);
	}
	
	public PeerHeader(int version, int contentLength, EnPeerCommand cmd) {
		super();
		this.version = version;
		this.contentLength = contentLength;
		this.cmd = cmd;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getContentLength() {
		return contentLength;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public EnPeerCommand getCmd() {
		return cmd;
	}

	public void setCmd(EnPeerCommand cmd) {
		this.cmd = cmd;
	}

	public String getFrontChannelId() {
		return frontChannelId;
	}

	public void setFrontChannelId(String frontChannelId) {
		this.frontChannelId = frontChannelId;
	}
	
	
	public String getPipeChannelId() {
		return pipeChannelId;
	}

	public void setPipeChannelId(String pipeChannelId) {
		this.pipeChannelId = pipeChannelId;
	}

	public void writeToBytebuf(ByteBuf out) {
		byte[] frontChannelIdByte = frontChannelId.getBytes();
		byte[] pipeChannelIdByte = pipeChannelId.getBytes();
		if( PeerHeader.CHANNEL_ID_SIZE != frontChannelIdByte.length )throw new RuntimeException("front Channel Id Size not match");
		if( PeerHeader.CHANNEL_ID_SIZE != pipeChannelIdByte.length )throw new RuntimeException("pipe Channel Id Size not match");
		out.writeInt(version);
		out.writeInt(contentLength);
		out.writeInt(cmd.ordinal());

		out.writeBytes(frontChannelIdByte);
		out.writeBytes(pipeChannelIdByte);

	}

	public String toString() {
		return "version="+version+",contentLength="+contentLength+",cmd="+cmd + ",chid=" + frontChannelId+",pid=" + pipeChannelId;
		
	}
}
