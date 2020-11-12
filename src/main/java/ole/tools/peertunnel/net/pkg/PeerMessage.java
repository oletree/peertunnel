package ole.tools.peertunnel.net.pkg;

public class PeerMessage {

	private PeerHeader header;
	private byte[] body;

	public PeerMessage(PeerHeader header, byte[] body) {
		super();
		this.header = header;
		this.body = body;
	}

	public PeerHeader getHeader() {
		return header;
	}

	public void setHeader(PeerHeader header) {
		this.header = header;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

}
