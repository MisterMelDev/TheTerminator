package tech.mistermel.terminator;

public class Launcher {

	public static TheTerminator instance;
	public static long startupTime;
	
	public static void main(String[] args) {
		startupTime = System.currentTimeMillis();
		instance = new TheTerminator();
		instance.start();
	}
	
}
