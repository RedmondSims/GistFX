package com.redmondsims.gistfx.preferences;

import com.redmondsims.gistfx.preferences.settings.Clear;
import com.redmondsims.gistfx.preferences.settings.Get;
import com.redmondsims.gistfx.preferences.settings.Set;


public class AppSettings {

	private static final Get   getter  = new Get();
	private static final Set   setter  = new Set();
	private static final Clear clearer = new Clear();

	public static Get get() {
		return getter;
	}

	public static Set set() {
		return setter;
	}

	public static Clear clear() {
		return clearer;
	}

	public static void resetPreferences() {
		clearer.clearAll();
		setter.setDefaults();
	}

	public static void resetCredentials() {
		clearer.passwordHash();
		clearer.tokenHash();
	}


}
