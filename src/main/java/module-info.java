module com.redmondsims.gistfx {

	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.web;
	requires java.prefs;
	requires org.apache.commons.codec;
	requires org.apache.commons.io;
	requires org.apache.commons.lang3;
	requires github.api;
	requires java.desktop;
	requires okhttp3;
	requires eu.mihosoft.monacofx;
	requires spring.security.crypto;
	requires java.sql;
	requires com.google.gson;
	requires annotations;

	exports com.redmondsims.gistfx.javafx.controls;
	exports com.redmondsims.gistfx.github.gist;


	opens com.redmondsims.gistfx.github.gist to javafx.base;
	opens com.redmondsims.gistfx.data to com.google.gson, javafx.base;
	exports com.redmondsims.gistfx.ui.preferences to javafx.graphics;
	exports com.redmondsims.gistfx.ui.enums to javafx.graphics;
	exports com.redmondsims.gistfx.utils to javafx.graphics;
	exports com.redmondsims.gistfx.data to com.google.gson;
	exports com.redmondsims.gistfx.ui to javafx.graphics;
	exports com.redmondsims.gistfx to javafx.graphics;
}