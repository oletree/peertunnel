package ole.tools.peertunnel.net.pkg;

import ole.tools.peertunnel.net.pkg.enums.EnPeerCommand;

public class PeerHeader {
	public static final int FRONT_CHANNEL_ID_SIZE = 60;
	private int version;
	private int contentLength;
	private EnPeerCommand cmd;
	private String frontChannelId;

	public PeerHeader() {
		
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

	public String toString() {
		return "version="+version+",contentLength="+contentLength+",cmd="+cmd + ",chid=" + frontChannelId;
		
	}
}
