/*
 * File: DragFactory.java
 * Copyright (C) 29/03/2021 David Thaler.
 * All rights reserved
 */
package com.redmondsims.gistfx.ui.gist.treefactory;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.alerts.ToolWindow;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.Theme;
import com.redmondsims.gistfx.enums.TreeType;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.utils.Status;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;

import static com.redmondsims.gistfx.enums.TreeType.*;

public class TreeCellFactory implements Callback<TreeView<TreeNode>, TreeCell<TreeNode>> {

    private static final DataFormat JAVA_FORMAT = new DataFormat("application/x-java-serialized-object");
    private static final String     BOTTOM_DROP_LINE_STYLE = "-fx-border-insets: 0px 0px 0px %dpx, 0px 0px 0px 0px; -fx-border-color: #eea82f; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 %dpx";
    private static final String     TOP_DROP_LINE_STYLE = "-fx-border-insets: 0px 0px 0px %dpx, 0px 0px 0px 0px; -fx-border-color: #eea82f; -fx-border-width: 2 0 0 0; -fx-padding: 1 3 3 %dpx";
    private TreeItem<TreeNode>      dropParent;
    private TreeCell<TreeNode>      styledCell;
    private int                     dropIndex;
    private TreeItem<TreeNode>      draggedItem;

    //FIXME Figure out why I can't edit the label of a TreeNode and have that change affect their related object

    public TreeCell<TreeNode> call(final TreeView treeView) {
        TreeCell<TreeNode> cell = new TreeNodeCell
                () {
            @Override
            public void updateItem(final TreeNode item, final boolean empty) {
                super.updateItem(item, empty);
                if (item == null) return;
                setText(getTreeItem().getValue().getName());
                setGraphic(getTreeItem().getGraphic());
            }
        };

        cell.setOnDragDetected((MouseEvent event) -> dragDetected(event, cell, treeView));
        cell.setOnDragOver((DragEvent event) -> dragOver(event, cell, treeView));
        cell.setOnDragDropped((DragEvent event) -> drop(event, cell, treeView));
        cell.setOnDragDone((DragEvent event) -> clearDropLocation());

        return cell;
    }

    private void dragDetected(final MouseEvent event, final TreeCell<TreeNode> treeCell, final TreeView<TreeNode> treeView) {
        //Cannot drag blank cell.
        if (treeCell == null) return;

        //Cannot drag root cell.
        if (treeCell.getParent() == null) return;

        //Select this cell to be dragged.
        Dragboard db = treeCell.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        draggedItem = treeCell.getTreeItem();
        content.put(JAVA_FORMAT, draggedItem.getValue());
        db.setContent(content);
        db.setDragView(treeCell.snapshot(null, null));
        event.consume();
    }

    private void dragOver(final DragEvent event, final TreeCell<TreeNode> treeCell, final TreeView<TreeNode> treeView) {
        if (draggedItem == null) return;

        TreeItem thisItem = treeCell.getTreeItem();

        if (draggedItem != null && thisItem != null && thisItem == draggedItem) return;

        if (thisItem == null) {
            dropParent = treeView.getRoot();
            dropIndex = -1;
            event.acceptTransferModes(TransferMode.MOVE);
            return;
        }

        if (childrenContains(draggedItem, thisItem)) return;

        Point2D sceneCoordinates = treeCell.localToScene(0d, 0d);
        double height = treeCell.getHeight();
        double y = event.getSceneY() - sceneCoordinates.getY();

        clearDropLocation();
        event.acceptTransferModes(TransferMode.MOVE);

        dropParent = thisItem.getParent();
        styledCell = treeCell;

        int parentCount = countParents(styledCell.getTreeItem());
        parentCount--;
        int indent = parentCount * 18;
        int negativePadding = 3 - indent;

        if (y < (height * .5d)) {
            dropIndex = dropParent.getChildren().indexOf(thisItem);
            styledCell.setStyle(String.format(TOP_DROP_LINE_STYLE, indent, negativePadding));
        } else {
            dropIndex = dropParent.getChildren().indexOf(thisItem) + 1;
            styledCell.setStyle(String.format(BOTTOM_DROP_LINE_STYLE, indent, negativePadding));
            if (thisItem.getValue() instanceof GistInterface dragObj) {
                if (dragObj.canContainChildren()) {
                    dropParent = thisItem;
                    dropIndex = 0;
                }
            }
        }
    }

    private int countParents(final TreeItem<TreeNode> child) {
        int                ret     = 0;
        TreeItem<TreeNode> current = child;
        while (current.getParent() != null) {
            current = current.getParent();
            ret++;
        }
        return ret;
    }

    private boolean childrenContains(final TreeItem<TreeNode> targetItem, final TreeItem<TreeNode> searchingFor) {
        boolean ret = false;
        for (TreeItem<TreeNode> obj : targetItem.getChildren()) {
            if (obj instanceof TreeItem) {
                if (obj == searchingFor) {
                    ret = true;
                    break;
                } else if (obj.getChildren().size() != 0) {
                    ret = childrenContains(obj, searchingFor);
                    if (ret) break;
                } else {
                    ret = false;
                }
            }
        }
        return ret;
    }

    private void drop(final DragEvent event, final TreeCell<TreeNode> treeCell, final TreeView<TreeNode> treeView) {
        if(draggedItem == null) return;
        boolean proceed     = false;
        boolean moveFile    = false;
        TreeType draggedType = draggedItem.getValue().getType();
        TreeType dropType    = dropParent.getValue().getType();
        if (dropType == null) return;
        if (draggedType == null) return;
        if (draggedType.equals(FILE) && dropType.equals(GIST)) {
            boolean doMove = true;
            if (Status.comparingLocalDataWithGitHub()) {
                showComparingWarning();
            }
            else {
                if (AppSettings.get().fileMoveWarning()) {
                    doMove = showFileMoveWarning();
                }
                if (draggedItem.getValue().getFile().isDirty() || draggedItem.getValue().getFile().isInConflict()) {
                    Platform.runLater(() -> CustomAlert.showWarning("This file is either in conflict or it needs to be saved.\n\nTherefore, you either need to resolve the conflict, or upload the file before you can move it to another Gist"));
                    doMove = false;
                }
                if (doMove) {
                    proceed  = true;
                    moveFile = true;
                }
            }
        }
        if (draggedType.equals(GIST) && (dropType.equals(CATEGORY))) {
            String gistId      = draggedItem.getValue().getGistId();
            String oldCategory = draggedItem.getValue().toString();
            String newCategory = dropParent.getValue().toString();
            if (!oldCategory.equals(newCategory)) {
                Action.mapCategoryNameToGist(gistId, newCategory);
                proceed = true;
            }
        }
        if (proceed) {
            draggedItem.getParent().getChildren().remove(draggedItem);
            if (dropIndex != -1) {
                dropParent.getChildren().add(dropIndex, draggedItem);
            }
            else {
                dropParent.getChildren().add(draggedItem);
            }
            treeView.getSelectionModel().select(draggedItem);
        }
        event.setDropCompleted(true);
        if(moveFile) {
            new Thread(() -> {
                Gist     oldGist = draggedItem.getValue().getGist();
                Gist     newGist = dropParent.getValue().getGist();
                GistFile file    = draggedItem.getValue().getFile();
                if (oldGist != newGist) {
                    Action.moveFile(oldGist, newGist, file);
                }
            }).start();
        }
    }

    private void clearDropLocation() {
        if (styledCell != null) styledCell.setStyle("");
    }

    private boolean yesNo = false;

    private void showComparingWarning() {
        Platform.runLater(() -> CustomAlert.showWarning("Please wait until I'm done comparing local Gists with GitHub"));
    }

    private boolean showFileMoveWarning() {
        String name = "FileMoveWarning";
        String message1 = """
                Moving a file from one Gist to another is NOT a feature that GitHub offers.
                Therefore, the only way to accomplish it is to first add the file to the new
                Gist, then delete the file from the old Gist.
                """;
        String message2 = """
                This means that the GitHub version history for this file will be deleted.
                """;
        String message3 = """
                Are you sure you wish to proceed?
                """;
        Text  text1 = new Text(message1);
        Text  text2 = new Text(message2);
        Text  text3 = new Text(message3);
        text1.setFont(Font.font("Avenir",15));
        text2.setFont(Font.font("Avenir",15));
        text3.setFont(Font.font("Avenir",15));
        Color color1;
        Color color2;
        if (AppSettings.get().theme().equals(Theme.DARK)) {
            color1 = Color.rgb(164,203,167);
            color2 = Color.rgb(255,255,0);
        }
        else {
            color1 = Color.BLACK;
            color2 = Color.DARKRED;
        }
        text1.setFill(color1);
        text2.setFill(color2);
        text3.setFill(color1);
        CheckBox cbShow = new CheckBox("Do not show this message again");
        Button btnYes = new Button("Yes");
        Button btnNo = new Button("No");
        btnYes.setOnAction(e -> {
            yesNo = true;
            AppSettings.set().fileMoveWarning(!cbShow.isSelected());
            SceneOne.close(name);
        });
        btnNo.setOnAction(e -> {
            yesNo = false;
            AppSettings.set().fileMoveWarning(!cbShow.isSelected());
            SceneOne.close(name);
        });
        HBox box1 = newHBox(20,text1);
        HBox box2 = newHBox(20,text2);
        HBox box3 = newHBox(20,text3);
        HBox box4 = newHBox(20,cbShow);
        VBox vbox = new VBox(box1,box2,box3,box4);
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.CENTER);
        ToolWindow toolWindow = new ToolWindow.Builder(vbox).size(550,375).setSceneId(name).addButton(btnYes).addButton(btnNo).build();
        toolWindow.showAndWait();
        return yesNo;
    }

    private HBox newHBox(double spacing, Node... nodes) {
        HBox hbox = new HBox(nodes);
        hbox.setSpacing(spacing);
        hbox.setCenterShape(true);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

}
