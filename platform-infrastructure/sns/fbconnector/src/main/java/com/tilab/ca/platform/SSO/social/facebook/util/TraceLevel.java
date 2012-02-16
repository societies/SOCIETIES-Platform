/*********************************************
  Context Awareness Platform
 ********************************************
  Copyright (c) 2006-2010 Telecom Italia S.p.A
 ******************************************** 
  $Id: TraceLevel.java 6992 2010-02-24 18:39:40Z papurello $
  $HeadURL: http://163.162.93.163:90/svn/ca/platform/commons-ca/branches/rel-1_0-ev/src/com/tilab/ca/platform/commons/log4j/TraceLevel.java $
 *********************************************
 */
package com.tilab.ca.platform.SSO.social.facebook.util;

import org.apache.log4j.Level;

/**
 * The TraceLevel class extends the Level class by introducing a new level called TRACE.
 * TRACE has a lower level than DEBUG.
 * @author UE014084
 */
public class TraceLevel extends Level {
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 7494076966034046496L;

	static public final int TRACE_INT = Level.DEBUG_INT - 1;

	private static String TRACE_STR = "TRACE";

	public static final TraceLevel TRACE = new TraceLevel(TRACE_INT, TRACE_STR, 7);

	/**
	 * @param level
	 * @param strLevel
	 * @param syslogEquiv
	 */
	protected TraceLevel(int level, String strLevel, int syslogEquiv) {
		super(level, strLevel, syslogEquiv);
	}

	/**
	 * Convert the String argument to a level. If the conversion fails then this method returns {@link #TRACE}.
	 */
	public static Level toLevel(String sArg) {
		return (Level) toLevel(sArg, TraceLevel.TRACE);
	}

	/**
	 * Convert the String argument to a level. If the conversion fails, return the level specified by the second argument, i.e. defaultValue.
	 */
	public static Level toLevel(String sArg, Level defaultValue) {
		if (sArg == null) {
			return defaultValue;
		}
		String stringVal = sArg.toUpperCase();
		if (stringVal.equals(TRACE_STR)) {
			return TraceLevel.TRACE;
		}
		return Level.toLevel(sArg, (Level) defaultValue);
	}

	/**
	 * Convert an integer passed as argument to a level. If the conversion fails, then this method returns {@link #DEBUG}.
	 */
	public static Level toLevel(int i) throws IllegalArgumentException {
		if (i == TRACE_INT) {
			return TraceLevel.TRACE;
		} else {
			return Level.toLevel(i);
		}
	}
}
