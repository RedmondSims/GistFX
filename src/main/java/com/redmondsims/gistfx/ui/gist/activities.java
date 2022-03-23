package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.alerts.ToolWindow;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.Response;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.networking.FileRecord;
import com.redmondsims.gistfx.networking.Payload;
import com.redmondsims.gistfx.networking.PayloadBuilder;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.ui.gist.factory.DragNode;
import com.redmondsims.gistfx.utils.Status;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.List;
import java.util.Map;

import static com.redmondsims.gistfx.enums.Response.*;
import static com.redmondsims.gistfx.enums.Type.CATEGORY;
import static com.redmondsims.gistfx.enums.Type.FILE;

public class activities {

	public activities(Stage gistStage, Window stageWindow, GistWindow gistWindow) {
		this.gistStage   = gistStage;
		this.stageWindow = stageWindow;
		this.gistWindow  = gistWindow;
	}

	private final Stage      gistStage;
	private final Window     stageWindow;
	private final String     compareWarning = "Please wait until I'm done comparing local Gists with GitHub.";
	private final GistWindow gistWindow;

	public void receiveData(Payload payload) {
		Platform.runLater(() -> {
			PayloadBuilder payloadBuilder = new PayloadBuilder(payload);
			boolean        needsPassword  = payloadBuilder.usePassword();
			String         password       = notifyUserIncomingPayload(needsPassword, payloadBuilder);
			if(password.equals(reject)) return;
			if(!payloadBuilder.getType().equals(FILE))
				addingDataMessage(true);
			new Thread(() -> {
				if(payloadBuilder.usePassword())
					if (!payloadBuilder.passwordValid(password)){
						CustomAlert.showWarning("Password Invalid");
						gistWindow.closeShareWindow();
						return;
					}
				if(payloadBuilder.getType().equals(FILE)) {
					dropFileIntoGist(payloadBuilder.getGistPayloadFile());
					if(!newFileGistId.isEmpty()) {
						System.out.println("Filling Tree");
						WindowManager.fillTree();
						refreshFiles(newFileGistId);
					}
				}
				else {
					List<String> gistIdList = payloadBuilder.createGists();
					WindowManager.fillTree();
					for (String gistId : gistIdList) {
						refreshFiles(gistId);
					}
				}
				Platform.runLater(() -> addingDataMessage(false));
				gistWindow.closeShareWindow();
			}).start();
		});
	}

	public void refreshFiles(String gistId) {
		Gist gist = GistManager.getGist(gistId);
		for (GistFile file : gist.getFiles()) {
			file.reCheckWithGitHub();
		}
	}

	private HBox newHBox(Node...nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(5);
		hbox.setAlignment(Pos.CENTER_LEFT);
		return hbox;
	}

	private String newFileGistId = "";
	public void dropFileIntoGist(FileRecord fileRecord) {
		newFileGistId = "";
		Label           label       = new Label("Select the Gist that you want to drop the file into");
		String          thisSceneId = "placeNewFile";
		ChoiceBox<Gist> cbGists     = new ChoiceBox<>();
		cbGists.getItems().setAll(GistManager.getGists());
		VBox vbox = new VBox(label, cbGists);
		vbox.setSpacing(20);
		vbox.setAlignment(Pos.CENTER);
		EventHandler<ActionEvent> addEventHandler = event -> {
			Gist gist = cbGists.getValue();
			if(gist != null) {
				System.out.println("Adding File...");
				Platform.runLater(() -> addingDataMessage(true));
				String filename    = fileRecord.filename();
				String content     = fileRecord.content();
				String description = fileRecord.description();
				newFileGistId = gist.getGistId();
				GistManager.addNewFile(newFileGistId, filename, content, description);
				System.out.println("File Added");
				SceneOne.close(thisSceneId);
			}
		};
		EventHandler<ActionEvent> closeEventHandler = event -> {
			newFileGistId = "";
			SceneOne.close(thisSceneId);
		};
		ToolWindow toolWindow = new ToolWindow.Builder(vbox)
				.size(300,200)
				.attachToStage(gistStage)
				.addButton("Save",30,addEventHandler)
				.addButton("Close",30,closeEventHandler)
				.setSceneId(thisSceneId)
				.build();
		Platform.runLater(toolWindow::showAndWait);
	}

	private int passwordAttempts;
	private String btnPressed = "";
	private final String reject = "RL:P>V*FxN)?Ns©7A=g<DPamfNF2E=kXA2mG>a=e?UH7-5)";
	private String notifyUserIncomingPayload(boolean needsPassword, PayloadBuilder payloadBuilder) {
		String type    = payloadBuilder.getType().Name();
		Text   textOne = new Text("You have received a " + type + " from: ");
		Text   textTwo = new Text(payloadBuilder.getSenderName());
		Text textThree = new Text("Having name: ");
		Text textFour = new Text(payloadBuilder.getTitle());
		HBox boxOne = newHBox(textOne,textTwo);
		HBox boxTwo = newHBox(textThree,textFour);
		textOne.setStrokeWidth(4);
		textTwo.setStrokeWidth(4);
		textThree.setStrokeWidth(4);
		textFour.setStrokeWidth(4);
		if(LiveSettings.getTheme().equals(UISettings.Theme.DARK)) {
			textTwo.setFill(Color.DARKORANGE);
			textFour.setFill(Color.DARKORANGE);
		}
		else {
			textTwo.setFill(Color.DARKBLUE);
			textFour.setFill(Color.DARKBLUE);
		}
		passwordAttempts = 1;
		String name = "notifyPayload";
		String password;
		double width = 350;
		double    height       = 230;
		TextField tfPassword   = new TextField();
		Text      textPassword = new Text("This data requires a password for decryption.\n\n");
		if(needsPassword) {
			tfPassword.setVisible(true);
		}
		Text textReceive = new Text("Do you wish to receive it?");
		textPassword.setLineSpacing(.2);
		textPassword.setFill(Color.RED);
		textPassword.setTextAlignment(TextAlignment.CENTER);
		Button btnYes = new Button("Yes");
		Button btnNo  = new Button("No");
		tfPassword.setAlignment(Pos.CENTER);
		tfPassword.setVisible(needsPassword);
		HBox buttonBox = newHBox(btnYes,btnNo);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(30);
		AnchorPane nuAP = new AnchorPane(boxOne, boxTwo, textReceive, tfPassword, buttonBox);
		if(needsPassword) {
			nuAP.getChildren().add(textPassword);
			setAnchors(textPassword,45,0,-1,45);
		}
		setAnchors(boxOne,10,10,10,-1);
		setAnchors(boxTwo,10,10,40,-1);
		setAnchors(textReceive,10,10,70,-1);
		setAnchors(tfPassword,40,40,-1,40);
		setAnchors(buttonBox,40,40,-1,5);
		btnYes.setOnAction( e->{
			btnPressed = "yes";
			if(needsPassword) {
				if (tfPassword.getText().isEmpty()) {
					CustomAlert.showInfo("Need a password", SceneOne.getWindow(name));
				}
				else {
					if (!payloadBuilder.passwordValid(tfPassword.getText())) {
						String msg = (passwordAttempts > 4) ? "Too many password attempts, canceling inbound payload." : "Invalid Password, Try again";
						CustomAlert.showInfo(msg,SceneOne.getWindow(name));
						passwordAttempts++;
						if(passwordAttempts > 4){
							Platform.runLater(() -> {
								btnPressed = "no";
								SceneOne.close(name);
							});
						}
					}
					else {
						SceneOne.close(name);
					}
				}
			}
			else {
				SceneOne.close(name);
			}
		});
		btnNo.setOnAction(e->{
			btnPressed = "no";
			Platform.runLater(() -> SceneOne.close(name));
		});
		EventHandler<WindowEvent> eventHandler = event -> {
			if(btnPressed.equals("yes")) {
				if(!payloadBuilder.usePassword()) tfPassword.setText("");
			}
			else {
				tfPassword.setText(reject);
			}
		};
		SceneOne.set(nuAP,name,gistStage).centered().size(width,height).onCloseEvent(eventHandler).showAndWait();
		return tfPassword.getText();
	}

	public void addingDataMessage(boolean show) {
		double width  = 400;
		double height = 100;
		String name   = "DataInfo";
		Label  label  = new Label("Creating new Gists and adding them to your GitHub account");
		label.setAlignment(Pos.CENTER);
		AnchorPane dmAP = new AnchorPane(label);
		if (LiveSettings.getTheme().equals(UISettings.Theme.DARK)) {
			label.setStyle("-fx-text-fill: ghostwhite");
			dmAP.setStyle("-fx-border-color: yellow;-fx-border-width: 1em");
		}
		else {
			label.setStyle("-fx-text-fill: black");
			dmAP.setStyle("-fx-border-color: darkred;-fx-border-width: 1em");
		}
		setAnchors(label, 0, 0, 0, 0);
		if (show) {
			SceneOne.set(dmAP, name, gistStage).size(width, height).initStyle(StageStyle.TRANSPARENT).centered().show();
		}
		else {
			if(SceneOne.sceneExists(name))
				SceneOne.close(name);
		}
	}

	public void setAnchors(Node node, double left, double right, double top, double bottom) {
		if (top != -1) AnchorPane.setTopAnchor(node, top);
		if (bottom != -1) AnchorPane.setBottomAnchor(node, bottom);
		if (left != -1) AnchorPane.setLeftAnchor(node, left);
		if (right != -1) AnchorPane.setRightAnchor(node, right);
	}

	public void newGist(DragNode selectedNode) {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		if(selectedNode == null) return;
		Platform.runLater(() -> {
			boolean categorySet = false;
			String selectedCategory = "";
			if (selectedNode != null) {
				if (selectedNode.getType().equals(CATEGORY)) {
					categorySet = true;
					selectedCategory = selectedNode.getCategory();
				}
			}
			String[] choices = CustomAlert.newGistAlert(getDefaultJavaText("File.java"), categorySet,selectedCategory);
			if (choices != null) {
				boolean isPublic    = choices[0].equals("Public");
				String  gistName    = choices[1];
				String  filename    = choices[2];
				String  description = choices[3];
				String  gistFile    = choices[4];
				String  category    = choices[5];
				String  newGistID   = GistManager.addNewGistToGitHub(gistName, description, filename, gistFile, isPublic);
				if (!newGistID.isEmpty()) {
					if (!category.trim().equals("!@#none#@!")) Action.mapCategoryNameToGist(newGistID, category);
					else if (categorySet) {
						Action.mapCategoryNameToGist(newGistID, selectedCategory);
					}

					WindowManager.fillTree();
				}
			}
		});
	}

	public void newFile(Gist gist) {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		else if(gist == null) return;
		String                             gistId      = gist.getGistId();
		StringProperty                     filename    = new SimpleStringProperty();
		StringProperty                     contents    = new SimpleStringProperty();
		Map<Response, Map<String, String>> responseMap = CustomAlert.showNewFileAlert(gist.getName(), getDefaultJavaText(gist.getName()));
		new Thread(() -> {
			for (Response response : responseMap.keySet()) {
				if (response == PROCEED) {
					for (Map<String, String> fileMap : responseMap.values()) {
						for (String mapFilename : fileMap.keySet()) {
							filename.setValue(mapFilename);
							contents.setValue(fileMap.get(mapFilename));
						}
					}
					GistFile file = GistManager.addNewFile(gistId, filename.getValue(), contents.getValue(), "");
					if (file != null) {
						String newFilename = file.getFilename();
						gistWindow.getTree().addFileToBranch(gistId, newFilename);
						TreeItem<DragNode> branch = gistWindow.getTree().getBranch(gistId);
						branch.setExpanded(true);
						for (TreeItem<DragNode> leaf : branch.getChildren()) {
							if (leaf.getValue().toString().equals(filename.getValue())) {
								gistWindow.getTreeView().getSelectionModel().select(leaf);
								Platform.runLater(() -> gistWindow.setSelectedNode(leaf.getValue()));
							}
						}
					}
				}
			}
		}).start();
	}

	public void deleteFile(GistFile file) {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		else if(file == null) return;
		Platform.runLater(() -> {
			String gistName = GistManager.getGist(file.getGistId()).getName();
			if (CustomAlert.showConfirmation("Are you sure you want to delete the file\n\n" + file.getFilename() + "\n\nFrom Gist: " + gistName + "?")) {
				GistManager.deleteFile(file);
				gistWindow.getTree().removeLeaf(file);
			}
		});
	}

	public void undoFile(GistFile file) {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		if(file == null) return;
		Platform.runLater(() -> {
			if (CustomAlert.showConfirmation("This action will overwrite your local changes with the last version that was uploaded to your GitHub account.\n\nAre you sure?")) {
				file.undo();
			}
		});
	}

	public void deleteGist(Gist gist) {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		if(gist == null) return;
			String   gistId   = gist.getGistId();
			Response response = deleteGistResponse(gistId);
			if (response == YES) {
				if (gistWindow.getTree().removeBranch(gistId)) {
					GistManager.deleteGist(gistId);
					Platform.runLater(() -> CustomAlert.showInfo("Gist deleted successfully.", stageWindow));
				}
			}
			if (response == MISTAKE) deleteGist(gist);
	}

	public void renameGist(Gist gist) {
		if (gist != null) {
			Platform.runLater(() -> {
				String gistId  = gist.getGistId();
				String newName = CustomAlert.showChangeNameAlert(gist.getName(), "Gist").replaceAll("\\n", " ").trim();
				if (!newName.isEmpty()) {
					gist.setName(newName);
					WindowManager.fillTree();
					TreeItem<DragNode> branch = gistWindow.getTree().getBranch(gistId);
					gistWindow.getTreeView().getSelectionModel().select(branch);
					gistWindow.setSelectedNode(branch.getValue());
				}
			});
		}
	}

	public void renameCategory() {
		if(gistWindow.getTreeView().getSelectionModel().getSelectedItem().getValue().getType().equals(CATEGORY)) {
			String oldName = gistWindow.getTreeView().getSelectionModel().getSelectedItem().getValue().toString();
			Platform.runLater(() -> {
				String newName = CustomAlert.showChangeNameAlert(oldName,"Category").replaceAll("\\n", " ").trim();
				if (!newName.isEmpty()) {
					Action.changeCategoryName(oldName,newName);
					WindowManager.fillTree();
				}
			});
		}
	}

	public void renameFile(GistFile file) {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		if(file != null) {
			String newFileName = CustomAlert.showFileRenameAlert(file.getFilename());
			if (!newFileName.isEmpty()) {
				new Thread(() -> {
					file.setName(newFileName);
					gistWindow.getTree().refreshGistBranch(file.getGistId());
					Platform.runLater(() -> {
						TreeItem<DragNode> leaf = gistWindow.getTree().getLeaf(file.getGistId(), newFileName);
						gistWindow.getTreeView().getSelectionModel().select(leaf);
						gistWindow.setSelectedNode(leaf.getValue());
					});
				}).start();
			}
		}
	}

	public void changeGistDescription(Gist gist) {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		else if (gist == null) return;
		String newDescription = CustomAlert.showChangeGistDescriptionAlert(gist.getDescription());
		if (!newDescription.isEmpty()) {
			gist.setDescription(newDescription);
		}
	}

	public void editCategories() {
		String    name                = "Categories";
		double    width               = 400;
		double    height              = 500;
		Label     lblNewCategory      = new Label("New Category");
		Label     lblSelectedCategory = new Label("Selected Category");
		Label     lblNewCategoryName  = new Label("New Name");
		TextField tfNewCategory       = new TextField();
		TextField tfSelectedCategory  = new TextField();
		TextField tfNewName           = new TextField();
		Tooltip.install(tfNewCategory, Action.newTooltip("Type in the category name and hit ENTER to save it"));
		Tooltip.install(tfSelectedCategory, Action.newTooltip("Click on a category from the list, then rename or delete it"));
		Tooltip.install(tfNewName, Action.newTooltip("Click on a category from the list, then Type in a new name here then press ENTER"));
		tfSelectedCategory.setEditable(false);
		ListView<String> lvCategories = new ListView<>();
		lvCategories.getItems().setAll(Action.getCategoryList());
		Button btnClose = new Button("Close");
		Button btnDelete = new Button("Delete Category");
		AnchorPane apCategories = new AnchorPane(lvCategories,
												 lblNewCategory,
												 lblSelectedCategory,
												 lblNewCategoryName,
												 tfNewCategory,
												 tfSelectedCategory,
												 tfNewName,
												 btnClose,
												 btnDelete);
		apCategories.setPrefSize(width, height);
		lblNewCategory.setMinWidth(85);
		lblSelectedCategory.setMinWidth(85);
		lblNewCategoryName.setMinWidth(55);
		btnClose.setMinWidth(55);
		btnClose.setMinHeight(35);
		btnDelete.setMinWidth(75);
		btnDelete.setMinHeight(35);
		setAnchors(lblNewCategory, 20, -1, 20, -1);
		setAnchors(tfNewCategory, 135, 20, 17.5, -1);
		setAnchors(lblSelectedCategory, 20, -1, 50, -1);
		setAnchors(tfSelectedCategory, 135, 20, 47.5, -1);
		setAnchors(lblNewCategoryName, 20, -1, 80, -1);
		setAnchors(tfNewName, 135, 20, 77.5, -1);
		setAnchors(lvCategories, 20, 20, 125, 65);
		setAnchors(btnClose, 80, -1, -1, 20);
		setAnchors(btnDelete,-1, 80, -1, 20);
		btnDelete.setDisable(true);
		tfSelectedCategory.textProperty().addListener((observable, oldValue, newValue) -> btnDelete.setDisable(newValue.length() == 0));
		tfNewCategory.setOnMouseClicked(e -> {
			tfSelectedCategory.clear();
			tfNewName.clear();
		});
		tfNewCategory.setOnAction(e -> {
			String categoryName = tfNewCategory.getText();
			Action.addCategoryName(categoryName);
			lvCategories.getItems().setAll(Action.getCategoryList());
			tfNewCategory.clear();
			tfNewCategory.requestFocus();
			WindowManager.fillTree();
		});
		tfNewName.setOnAction(e -> {
			Action.changeCategoryName(tfSelectedCategory.getText(), tfNewName.getText());
			tfSelectedCategory.clear();
			tfNewName.clear();
			lvCategories.getItems().setAll(Action.getCategoryList());
			tfNewName.clear();
			tfNewCategory.requestFocus();
			WindowManager.fillTree();
		});
		lvCategories.setOnMouseClicked(e -> {
			tfSelectedCategory.setText(lvCategories.getSelectionModel().getSelectedItem());
			tfNewName.requestFocus();
		});
		btnClose.setOnAction(e -> {
			if (!tfNewCategory.getText().isEmpty()) {
				String categoryName = tfNewCategory.getText();
				Action.addCategoryName(categoryName);
				lvCategories.getItems().setAll(Action.getCategoryList());
				tfNewCategory.clear();
				tfNewCategory.requestFocus();
				WindowManager.fillTree();
			}
			SceneOne.close(name);
		});
		btnDelete.setOnAction(e -> {
			Action.deleteCategoryName(tfSelectedCategory.getText());
			Platform.runLater(() -> tfSelectedCategory.setText(""));
			lvCategories.getItems().setAll(Action.getCategoryList());
			WindowManager.fillTree();
		});
		SceneOne.set(apCategories, name,gistStage)
				.centered()
				.newStage()
				.title("Edit Categories")
				.show();
	}

	public void deleteCategory(DragNode selectedNode) {
		if (selectedNode == null) return;
		if (selectedNode.getType().equals(CATEGORY)) {
			String category = selectedNode.getCategory();
			if (CustomAlert.showConfirmation("Are you sure you want to delete category: " + category + "?")) {
				Action.deleteCategoryName(category);
				WindowManager.fillTree();
			}
		}
	}

	public void assignCategory(String gistId) {
		String name     = "Assign";
		double width    = 450;
		double height   = 175;
		String gistName = Action.getGistName(gistId);
		Text   text1    = new Text("Assign ");
		Text   text2    = new Text(gistName);
		Text   text3    = new Text(" to category:");
		Color  color1;
		Color  color2;
		if (LiveSettings.getTheme().equals(UISettings.Theme.DARK)) {
			color1 = Color.rgb(144, 163, 127);
			//color2 = Color.rgb(185,55,0);
			color2 = Color.YELLOW;
		}
		else {
			color1 = Color.BLACK;
			color2 = Color.DARKRED;
		}
		text1.setFill(color1);
		text2.setFill(color2);
		text3.setFill(color1);
		text1.setFont(Font.font("Avenir", 15));
		text2.setFont(Font.font("Avenir", 15));
		text3.setFont(Font.font("Avenir", 15));
		HBox hbox = new HBox(text1, text2, text3);
		hbox.setSpacing(0);
		hbox.setPadding(new Insets(0, 0, 0, 0));
		hbox.setPrefWidth(width - 40);
		hbox.setAlignment(Pos.CENTER);
		ChoiceBox<String> cbCategories = new ChoiceBox<>(Action.getCategoryList());
		Button            btnClose     = new Button("Assign");
		cbCategories.setMinWidth(150);
		cbCategories.setMaxWidth(150);
		btnClose.setMinWidth(55);
		AnchorPane ap = new AnchorPane(hbox, cbCategories, btnClose);
		ap.setMinSize(width, height);
		setAnchors(hbox, 20, 20, 20, -1);
		btnClose.setOnAction(e -> {
			SceneOne.close(name);
			String category = cbCategories.getValue();
			if (category != null) {
				Action.mapCategoryNameToGist(gistId, category);
				WindowManager.fillTree();
				gistWindow.setSelectedNode(gistWindow.getTree().getBranchCategory(category).getValue());
				gistWindow.getTree().getBranchCategory(Action.getGistCategoryName(gistId)).setExpanded(true);
			}
		});
		SceneOne.set(ap, name).centered().size(width, height).newStage().show();
		double windowWidth = SceneOne.getWindow(name).getWidth();
		if (windowWidth > width) {
			setAnchors(cbCategories, (ap.getWidth() / 2) - 75, -1, 55, -1);
			setAnchors(btnClose, (ap.getWidth() / 2) - 25.0, -1, -1, 20);
		}
		else {
			setAnchors(cbCategories, (width / 2) - 75, -1, 55, -1);
			setAnchors(btnClose, (width / 2) - 25.0, -1, -1, 20);
		}
	}

	public Response deleteGistResponse(String gistId) {
		Gist gist = GistManager.getGist(gistId);
		int     forkCount = gist.getForkCount();
		boolean isPublic  = gist.isPublic();
		String  forkText  = "";
		if (forkCount > 0) {
			forkText = "This Gist currently has " + forkCount + " fork(s). When you delete this Gist, each fork will be converted into a local Gist for those users who have a fork.\n\n";
		}
		StringBuilder sb        = new StringBuilder(forkText);
		List<String>  fileNames = GistManager.getFilenamesFor(gistId);
		sb.append("Are you sure you wish to delete this gist and these files?\n");
		if (fileNames.size() > 10) {
			sb.append(" (partial list)");
		}
		sb.append("\n");
		int max = Math.min(fileNames.size(), 10);
		for (int x = 0; x < max; x++) {
			sb.append("\t").append(fileNames.get(x)).append("\n");
		}
		if (fileNames.size() > 10) sb.append("...");
		sb.append("\n");
		return CustomAlert.showHardConfirmation("Delete Gist", sb.toString());
	}

	private String getDefaultJavaText(String name) {
		return "public class " + name + " {\n" +
			   "\n" +
			   "\tpublic static void main(String[] args) {\n" +
			   "\t\tSystem.out.println(\"Hello, World!\");\n" +
			   "\t}\n" +
			   "}";
	}


}
