package model;

import java.util.HashMap;
import java.util.Map;

public enum PlayMode {

	AUTO("0"), MANUAL("1");

	private final String code;

	private PlayMode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	// Lookup Map
	private static final Map<String, PlayMode> lookup = new HashMap<String, PlayMode>();

	static {
		for (PlayMode env : PlayMode.values()) {
			lookup.put(env.getCode(), env);
		}
	}

	public static PlayMode get(String code) {
		return lookup.get(code);
	}
	
	public boolean isAuto()
	{
		return this == AUTO;
	}

}
