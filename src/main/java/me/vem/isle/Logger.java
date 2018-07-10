package me.vem.isle;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private enum Severity{ INFO, WARNING, ERROR, FATAL_ERROR, DEBUG; }
	
	/**
	 * @param i
	 * @param obj
	 */
	public static void log(int i, Object obj) {
		Severity sev = Severity.values()[i];
		log(sev, obj);
	}
	
	public static void log(Severity sev, Object obj) {
		if(sev == Severity.ERROR || sev == Severity.FATAL_ERROR) {
			System.err.printf("[%s][%s]> %s%n", sev, timeFormat(), obj);
			if(sev == Severity.FATAL_ERROR)
				System.exit(1);
		}else
			System.out.printf("[%s][%s]> %s%n", sev, timeFormat(), obj);
	}
	
	public static void info(Object out) { log(0, out); }
	public static void warning(Object out) { log(1, out); }
	public static void error(Object out) { log(2, out); }
	public static void fatalError(Object out) { log(3, out); }
	public static void debug(Object out) { log(4, out); }
	
	private static String timeFormat() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());	}
}