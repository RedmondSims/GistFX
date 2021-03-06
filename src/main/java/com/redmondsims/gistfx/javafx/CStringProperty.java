package com.redmondsims.gistfx.javafx;

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

	public boolean isEqualTo(CStringProperty stringProperty) {
		return getValue().equals(stringProperty.getValue());
	}

	public CStringProperty getProperty() {
		return this;
	}
}
