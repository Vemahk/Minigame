package me.vem.isle;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private enum Severity{ INFO, WARNING, ERROR, FATAL_ERROR, DEBUG; }
	
	public static void log(int i, Object obj) {
		log(Severity.values()[i], obj);
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
	
	public static void logf(int i, String f, Object... objs) {
		logf(Severity.values()[i], f, objs);
	}
	
	public static void logf(Severity sev, String f, Object... objs) {
		if(sev == Severity.ERROR || sev == Severity.FATAL_ERROR) {
			System.err.printf("[%s][%s]> %s%n", sev, timeFormat(), String.format(f, objs));
			if(sev == Severity.FATAL_ERROR)
				System.exit(1);
		}else
			System.out.printf("[%s][%s]> %s%n", sev, timeFormat(), String.format(f, objs));
	}
	
	public static void infof(String f, Object... objs) { logf(0, f, objs); }
	public static void warningf(String f, Object... objs) { logf(1, f, objs); }
	public static void errorf(String f, Object... objs) { logf(2, f, objs); }
	public static void fatalErrorf(String f, Object... objs) { logf(3, f, objs); }
	public static void debugf(String f, Object... objs) { logf(4, f, objs); }
	
	private static String timeFormat() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());	}
}