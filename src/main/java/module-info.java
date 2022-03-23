module com.redmondsims.gistfx {

	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.web;
	requires java.prefs;
	requires org.apache.commons.codec;
	requires org.apache.commons.io;
	requires org.apache.commons.lang3;
	requires spring.security.crypto;
	requires java.sql;
	requires com.google.gson;
	requires org.kohsuke.github.api;
	requires java.datatransfer;
	requires java.desktop;
	requires eu.mihosoft.monacofx;
	requires com.simtechdata.waifupnp;

	exports com.redmondsims.gistfx.preferences to javafx.graphics, com.google.gson;
	exports com.redmondsims.gistfx.enums to javafx.graphics;
	exports com.redmondsims.gistfx.utils to javafx.graphics;
	exports com.redmondsims.gistfx.ui to javafx.graphics;
	exports com.redmondsims.gistfx to javafx.graphics;
	exports com.redmondsims.gistfx.data.metadata to com.google.gson;
	exports com.redmondsims.gistfx.preferences.mail to com.google.gson, javafx.graphics;
	exports com.redmondsims.gistfx.gist;
	exports com.redmondsims.gistfx.ui.gist.factory;
	exports com.redmondsims.gistfx.javafx;
	exports com.redmondsims.gistfx.preferences.settings;
	exports com.redmondsims.gistfx.alerts;
	exports com.redmondsims.gistfx.help;
	exports com.redmondsims.gistfx.data;

	opens com.redmondsims.gistfx.gist to javafx.base;
	opens com.redmondsims.gistfx.data to com.google.gson, javafx.base;
	opens com.redmondsims.gistfx.preferences to com.google.gson;
	opens com.redmondsims.gistfx.alerts to javafx.base;
	opens com.redmondsims.gistfx.help to javafx.base;
	opens com.redmondsims.gistfx.ui.gist.factory to javafx.base;
	opens com.redmondsims.gistfx.data.metadata to com.google.gson, javafx.base;
	opens com.redmondsims.gistfx.preferences.mail to com.google.gson;
	exports com.redmondsims.gistfx.networking;
	opens com.redmondsims.gistfx.networking to com.google.gson;
	exports com.redmondsims.gistfx.ui.gist to javafx.graphics;

}
