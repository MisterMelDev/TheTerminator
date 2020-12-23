package tech.mistermel.terminator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.mistermel.terminator.file.AccountsFile;
import tech.mistermel.terminator.map.MapHandler;
import tech.mistermel.terminator.mc.Account;
import tech.mistermel.terminator.mc.BotPlayer;
import tech.mistermel.terminator.util.BlockTypeRegistry;
import tech.mistermel.terminator.util.TextureRegistry;
import tech.mistermel.terminator.web.WebServer;
import tech.mistermel.terminator.web.route.AccountsAddRoute;
import tech.mistermel.terminator.web.route.AccountsListRoute;
import tech.mistermel.terminator.web.route.MapRoute;

public class TheTerminator {

	private static final Logger logger = LoggerFactory.getLogger(TheTerminator.class);
	
	private WebServer webServer;
	private AccountsFile accountsFile;
	private BlockTypeRegistry blockStateRegistry;
	private TextureRegistry textureRegistry;
	private MapHandler mapHandler;
	
	private String ip;
	private int port;
	
	private List<Account> accounts = new ArrayList<>();
	private Map<Account, BotPlayer> players = new HashMap<>();
	
	public void start() {
		this.blockStateRegistry = new BlockTypeRegistry();
		blockStateRegistry.load();
		
		this.accountsFile = new AccountsFile();
		this.accounts = accountsFile.loadAccounts();
		this.sortAccounts();
		
		this.textureRegistry = new TextureRegistry();
		textureRegistry.loadTextures();
		
		this.mapHandler = new MapHandler();
		
		this.webServer = new WebServer();
		webServer.registerRoute("/accounts/list", new AccountsListRoute());
		webServer.registerRoute("/accounts/add", new AccountsAddRoute());
		webServer.registerRoute("/map", new MapRoute());
		
		// TEMP
		this.setServer("127.0.0.1", 25565);
		
		logger.info("Startup completed (took {}ms)", (System.currentTimeMillis() - Launcher.startupTime));
	}
	
	public void setServer(String ip, int port) {
		logger.info("Setting server, disconnecting {} bot player(s) already connected", players.size());
		for(BotPlayer player : this.getPlayers()) {
			player.disconnect();
		}
		
		this.ip = ip;
		this.port = port;
	}
	
	public void addAccount(Account account) {
		accounts.add(account);
		this.sortAccounts();
		
		accountsFile.saveAccount(account);
		
		logger.info("Account added (username: {}, uuid: {})", account.getUsername(), account.getUuid().toString());
	}
	
	private void sortAccounts() {
		Collections.sort(accounts, new Comparator<Account>() {
			@Override
			public int compare(Account o1, Account o2) {
				return o1.getUsername().compareTo(o2.getUsername());
			}
		});
	}
	
	public Account getAccount(UUID uuid) {
		for(Account account : accounts) {
			if(account.getUuid().equals(uuid))
				return account;
		}
		
		return null;
	}
	
	public void connectAccount(int index) {
		this.connectAccount(accounts.get(index));
	}
	
	public void connectAccount(Account account) {
		if(ip == null) {
			logger.warn("Could not connect account, server not specified");
			return;
		}
		
		if(players.get(account) != null) {
			logger.warn("Could not connect account, already connected");
			return;
		}
		
		BotPlayer player = new BotPlayer(account);
		players.put(account, player);
		player.connect(ip, port);
	}
	
	public BotPlayer getPlayer(Account account) {
		return players.get(account);
	}
	
	public Collection<BotPlayer> getPlayers() {
		return players.values();
	}
	
	public List<Account> getAccounts() {
		return accounts;
	}
	
	public WebServer getWebServer() {
		return webServer;
	}
	
	public BlockTypeRegistry getBlockStateRegistry() {
		return blockStateRegistry;
	}
	
	public MapHandler getMapHandler() {
		return mapHandler;
	}
	
	public TextureRegistry getTextureRegistry() {
		return textureRegistry;
	}
	
}
