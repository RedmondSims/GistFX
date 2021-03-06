package com.redmondsims.gistfx.javafx;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;

import java.io.Serializable;

public class CBooleanProperty extends SimpleBooleanProperty implements Serializable {

	public CBooleanProperty() {
		super();
		addListener((observable, wasTrue, isTrue) -> changed = wasTrue != isTrue);
	}

	public CBooleanProperty(Boolean value) {
		super(value);
		addListener((observable, wasTrue, isTrue) -> changed = wasTrue != isTrue);
	}

	private boolean changed;

	public boolean isTrue() {
		return super.getValue().equals(true);
	}

	public boolean isFalse() {
		return super.getValue().equals(false);
	}

	public void setTrue() {super.setValue(true);}

	public void setFalse() {super.setValue(false);}

	public void setChangeListener(ChangeListener<Boolean> listener) {
		super.removeListener(listener);
		super.addListener(listener);
	}

	public void toggle() {
		super.setValue(!super.getValue());
	}

	public boolean opposite() {
		return !super.getValue();
	}

	public boolean changed() {
		return changed;
	}

	@Override public void setValue(Boolean value) {super.setValue(value);}

	@Override public Boolean getValue() {return super.getValue();}
}
