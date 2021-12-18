package com.redmondsims.gistfx.javafx.controls;

import javafx.beans.property.SimpleStringProperty;

public class CStringProperty extends SimpleStringProperty {

	public CStringProperty() {super();}

	public CStringProperty(String value) {
		super(value);
	}

	public boolean notEqualTo(String value) {
		return !super.getValue().equals(value);
	}

	public String name() {
		return super.getValue();
	}

	public CStringProperty getProperty() {
		return this;
	}
}
