package com.redmondsims.gistfx.javafx;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

public class PaddedGridPane extends GridPane {

    public PaddedGridPane(double gaps, double padding) {
        this.setVgap(gaps);
        this.setHgap(gaps);
        this.setPadding(new Insets(padding));
    }

}
