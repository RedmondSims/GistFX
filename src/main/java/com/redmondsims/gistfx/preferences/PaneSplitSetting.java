package com.redmondsims.gistfx.preferences;

import com.redmondsims.gistfx.enums.PaneState;

import java.util.HashMap;
import java.util.Map;
import static com.redmondsims.gistfx.enums.PaneState.*;

public class PaneSplitSetting {

	public PaneSplitSetting() {
		setDefaults();
	}

	private final Map<String, Double> settingMap = new HashMap<>();

	public void setPosition(PaneState state, double value) {
		settingMap.remove(state.toString());
		settingMap.put(state.toString(), value);
	}

	public double getPosition(PaneState state) {
		return settingMap.get(state.toString());
	}

	private void setDefaults() {
		settingMap.put(REST.toString(),.02);
		settingMap.put(EXPANDED.toString(),.20);
		settingMap.put(DEFAULT.toString(),.25);
		settingMap.put(DEFAULT_FULL.toString(),.20);
	}

	public void showValues() {
		System.out.println(REST + ": " + settingMap.get(REST.toString()));
		System.out.println(EXPANDED + ": " + settingMap.get(EXPANDED.toString()));
	}
}
