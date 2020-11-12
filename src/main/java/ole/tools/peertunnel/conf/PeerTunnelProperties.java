package ole.tools.peertunnel.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "peer")
@Validated
public class PeerTunnelProperties {

	boolean serverMode = true;

	private ServerInfo serverInfo = new ServerInfo();

	private TunnelInfo tunnel = new TunnelInfo();

	public boolean isServerMode() {
		return serverMode;
	}

	public void setServerMode(boolean serverMode) {
		this.serverMode = serverMode;
	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	public TunnelInfo getTunnel() {
		return tunnel;
	}

	public void setTunnel(TunnelInfo tunnel) {
		this.tunnel = tunnel;
	}

	public static class ServerInfo {
		private int port = 9500;
		private String ip = "127.0.0.1";

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

	}

	public static class TunnelInfo {
		private int openPort = 9501;
		private int connectPort = 7502;
		private String connectIp = "127.0.0.1";

		public int getOpenPort() {
			return openPort;
		}

		public void setOpenPort(int openPort) {
			this.openPort = openPort;
		}

		public int getConnectPort() {
			return connectPort;
		}

		public void setConnectPort(int connectPort) {
			this.connectPort = connectPort;
		}

		public String getConnectIp() {
			return connectIp;
		}

		public void setConnectIp(String connectIp) {
			this.connectIp = connectIp;
		}

	}
}
