package tech.mistermel.terminator.mc;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

import tech.mistermel.terminator.util.BlockType;
import tech.mistermel.terminator.util.Location;

public class BotPlayer extends SessionAdapter {

	private static final Logger logger = LoggerFactory.getLogger(BotPlayer.class);
	private static final String DISCONNECT_REASON = "Disconnected"; // Same as vanilla
	
	private MinecraftProtocol protocol;
	private Client client;
	private BlockRegistry blockRegistry;
	private PlayerThread playerThread;
	
	private Location loc = new Location(0, 0, 0);
	private float velocityX, velocityY, velocityZ;
	private boolean onGround;
	
	private float health, saturation;
	private int food;
	
	public BotPlayer(Account account) {
		this.protocol = new MinecraftProtocol(new GameProfile(account.getUuid(), account.getUsername()), account.getClientToken(), account.getAccessToken());
		this.blockRegistry = new BlockRegistry();
	}
	
	public void connect(String ip, int port) {
		logger.info("Connecting {} ({}) to {}:{}", protocol.getProfile().getName(), protocol.getProfile().getIdAsString(), ip, port);
		
		this.client = new Client(ip, port, protocol, new TcpSessionFactory());
		client.getSession().addListener(this);
		client.getSession().connect();
		
		this.playerThread = new PlayerThread(this);
		playerThread.start();
	}
	
	public void disconnect() {
		client.getSession().disconnect(DISCONNECT_REASON);
		playerThread.setRunning(false);
	}
	
	@Override
	public void packetReceived(PacketReceivedEvent event) {
		try {
			if(event.getPacket() instanceof ServerPlayerHealthPacket) {
				ServerPlayerHealthPacket packet = (ServerPlayerHealthPacket) event.getPacket();
				this.health = packet.getHealth();
				this.saturation = packet.getSaturation();
				this.food = packet.getFood();
			
				if(health <= 0) {
					logger.info("{} died, respawning", protocol.getProfile().getName());
					client.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
				}
			
				return;
			}
		
			if(event.getPacket() instanceof ServerChunkDataPacket) {
				ServerChunkDataPacket packet = (ServerChunkDataPacket) event.getPacket();
				blockRegistry.registerColumn(packet.getColumn());
			
				return;
			}
		
			if(event.getPacket() instanceof ServerBlockChangePacket) {
				ServerBlockChangePacket packet = (ServerBlockChangePacket) event.getPacket();
				blockRegistry.processBlockChange(packet.getRecord());
				
				return;
			}
		
			if(event.getPacket() instanceof ServerMultiBlockChangePacket) {
				ServerMultiBlockChangePacket packet = (ServerMultiBlockChangePacket) event.getPacket();
				for(BlockChangeRecord record : packet.getRecords()) {
					blockRegistry.processBlockChange(record);
				}
			
				return;
			}
		
			if(event.getPacket() instanceof ServerPlayerPositionRotationPacket) {
				ServerPlayerPositionRotationPacket packet = (ServerPlayerPositionRotationPacket) event.getPacket();
				client.getSession().send(new ClientTeleportConfirmPacket(packet.getTeleportId()));
				
				this.loc = new Location(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
			
				return;
			}
		} catch(Exception e) {
			logger.error("Exception occurred while receiving packet", e);
		}
	}
	
	protected void applyVelocity() {
		loc.add(velocityX, velocityY, velocityZ);
		client.getSession().send(new ClientPlayerPositionPacket(onGround, loc.getX(), loc.getBlockY(), loc.getZ()));
	}
	
	public float getVelocityX() {
		return velocityX;
	}
	
	public float getVelocityY() {
		return velocityY;
	}
	
	public float getVelocityZ() {
		return velocityZ;
	}
	
	public boolean isOnGround() {
		return onGround;
	}
	
	public void setVelocityX(float velocityX) {
		this.velocityX = velocityX;
	}
	
	public void setVelocityY(float velocityY) {
		this.velocityY = velocityY;
	}
	
	public void setVelocityZ(float velocityZ) {
		this.velocityZ = velocityZ;
	}
	
	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}
	
	public BlockType getBlock(Location loc) {
		return blockRegistry.getBlock(loc);
	}
	
	public Column getColumn(int chunkX, int chunkZ) {
		return blockRegistry.getColumn(chunkX, chunkZ);
	}
	
	public void sendMessage(String msg) {
		client.getSession().send(new ClientChatPacket(msg));
	}
	
	public float getHealth() {
		return health;
	}
	
	public float getSaturation() {
		return saturation;
	}
	
	public int getFood() {
		return food;
	}
	
	public String getUsername() {
		return protocol.getProfile().getName();
	}
	
	public UUID getUUID() {
		return protocol.getProfile().getId();
	}
	
	public Location getLocation() {
		return loc.clone();
	}
	
	public MinecraftProtocol getProtocol() {
		return protocol;
	}
	
	public Client getClient() {
		return client;
	}
	
}
