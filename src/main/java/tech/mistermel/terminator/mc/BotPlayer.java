package tech.mistermel.terminator.mc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.steveice10.mc.auth.exception.request.InvalidCredentialsException;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

public class BotPlayer extends SessionAdapter {

	private static final Logger logger = LoggerFactory.getLogger(BotPlayer.class);
	private static final String DISCONNECT_REASON = "Disconnected"; // Same as vanilla
	
	private MinecraftProtocol protocol;
	private Client client;
	
	public BotPlayer(Account account) {
		try {
			this.protocol = new MinecraftProtocol(account.getUsername(), account.getClientToken(), account.getAccessToken());
		} catch(InvalidCredentialsException e) {
			logger.warn("Unable to log in, credentials invalid");
		} catch (RequestException e) {
			logger.error("Error occurred while attempting to log in", e);
		}
	}
	
	public void connect(String ip, int port) {
		if(protocol == null) {
			logger.warn("Cannot connect, login failed");
			return;
		}
		
		logger.info("Connecting {} ({}) to {}:{}", protocol.getProfile().getName(), protocol.getProfile().getIdAsString(), ip, port);
		
		this.client = new Client(ip, port, protocol, new TcpSessionFactory());
		client.getSession().addListener(this);
		client.getSession().connect();
	}
	
	@Override
	public void packetReceived(PacketReceivedEvent event) {
		Packet packet = event.getPacket();
		
		if(packet instanceof ServerPlayerHealthPacket) {
			float health = ((ServerPlayerHealthPacket) packet).getHealth();
			logger.info("{} health is {}", protocol.getProfile().getName(), health);
			
			if(health <= 0) {
				logger.info("{} died, respawning", protocol.getProfile().getName());
				client.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
			}
		}
	}
	
	public void disconnect() {
		client.getSession().disconnect(DISCONNECT_REASON);
	}
	
	public MinecraftProtocol getProtocol() {
		return protocol;
	}
	
}
