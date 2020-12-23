package tech.mistermel.terminator.web.route;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import tech.mistermel.terminator.Launcher;
import tech.mistermel.terminator.mc.Account;
import tech.mistermel.terminator.mc.BotPlayer;

public class MapRoute implements Route {

	private static Logger logger = LoggerFactory.getLogger(MapRoute.class);
	
	@Override
	public Response serve(IHTTPSession session) {
		String botUuid = session.getParms().get("player");
		if(botUuid == null)
			return NanoHTTPD.newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Missing bot UUID");
		
		String showPlayerStr = session.getParms().get("showPlayer");
		boolean showPlayer = showPlayerStr == null ? false : Boolean.parseBoolean(showPlayerStr);
		
		Account account = Launcher.instance.getAccount(UUID.fromString(botUuid));
		if(account == null)
			return NanoHTTPD.newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Account not found");
		
		BotPlayer botPlayer = Launcher.instance.getPlayer(account);
		if(botPlayer == null)
			return NanoHTTPD.newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Bot player not logged in");
		
		BufferedImage img = Launcher.instance.getMapHandler().createImage(botPlayer, showPlayer);
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(img, "png", out);
			InputStream in = new ByteArrayInputStream(out.toByteArray());
			
			return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "image/png", in, in.available());
		} catch (IOException e) {
			logger.error("Error occurred while attempting to send image", e);
			return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", e.getMessage());
		}
	}

}
