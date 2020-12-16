package tech.mistermel.terminator.web;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public interface Route {

	public Response serve(IHTTPSession session);
	
}
