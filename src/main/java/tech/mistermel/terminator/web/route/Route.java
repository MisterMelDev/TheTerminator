package tech.mistermel.terminator.web.route;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public interface Route {

	public Response serve(IHTTPSession session);
	
}
