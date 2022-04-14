package com.redmondsims.gistfx.networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.alerts.ToolWindow;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.simtechdata.waifupnp.UPnP;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.List;

import static com.redmondsims.gistfx.enums.Type.*;

public class Transport {

	public Transport() {}

	public void shareGist(Gist gist) {
		this.gist = gist;
		this.type = GIST;
	}

	public void shareCategory(List<Gist> gistList, String category) {
		this.gistList     = gistList;
		this.categoryName = category;
		this.type = CATEGORY;
	}

	public void shareGistFile(GistFile gistFile) {
		this.gistFile = gistFile;
		this.type = FILE;
	}


	private final Gson              gson         = new GsonBuilder().setPrettyPrinting().create();
	private       Gist              gist;
	private       GistFile          gistFile;
	private       List<Gist>        gistList;
	private final String            payload      = "payload";
	private final String            received     = "received";
	private       String            categoryName = "";
	private final String            sceneId      = "DataTransport";
	private final double            width        = 500;
	private final double            height       = 300;
	private       Type              type;
	private       AnchorPane        ap;
	private       Label             lblTo;
	private       Label             lblPassword;
	private       TextField         tfPassword;
	private       CheckBox          cbPassword;
	private       TextArea          taPayload;
	private       Button            btnSend;
	private       Button            btnCancel;
	private       Button            btnEditHosts;
	private       ChoiceBox<String> cbHosts;
	private       HBox              buttonBox;
	private final String            en           = "\n";
	private final String            en2          = "\n\n";
	private final String            tab          = "\t";
	private final String            tab2         = "\t\t";
	private  boolean connecting = false;


	private void setAnchors(Node node, double left, double right, double top, double bottom) {
		if (top != -1) AnchorPane.setTopAnchor(node, top);
		if (bottom != -1) AnchorPane.setBottomAnchor(node, bottom);
		if (left != -1) AnchorPane.setLeftAnchor(node, left);
		if (right != -1) AnchorPane.setRightAnchor(node, right);
	}

	private AnchorPane ap(Node... nodes) {
		return new AnchorPane(nodes);
	}

	private HBox newHBox(double width, double spacing, Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(spacing);
		hbox.setAlignment(Pos.CENTER_LEFT);
		hbox.setMinWidth(width);
		return hbox;
	}

	public void closeReceiveWindow() {
		new Thread(() -> {
			if(!receiveWindowClosed) {
				upNp(null,false);
				SceneOne.close(waitingSceneId);
				receiveWindowClosed = true;
			}
		}).start();
	}
	private boolean receiveWindowClosed = false;
	private boolean upNp(Label lblInfo, boolean map) {
		boolean response = false;
			if (map) {
				mappingPorts = true;
				Platform.runLater(() -> lblInfo.setText("Attempting to map port through router.\nThis could take a while."));
				if(!UPnP.isUPnPAvailable()) Platform.runLater(() -> {
					lblInfo.setText("UPnP is not available on your router, or it is not enabled.");
				});
				else {
					Action.sleep(3000);
					Platform.runLater(() -> lblInfo.setText("UPnP IS available on your router.\nAttempting to map the port..."));
					if(UPnP.isMappedTCP(LiveSettings.getTcpPortNumber())) {
						if(UPnP.closePortTCP(LiveSettings.getTcpPortNumber())) {
							if(UPnP.openPortTCP(LiveSettings.getTcpPortNumber())) {
								Platform.runLater(() -> {
									Platform.runLater(() -> lblInfo.setText("Port mapping was successful!\nYou can now receive Gists at your IP address."));
								});
								response = true;
							}
							else {
								Platform.runLater(() -> lblInfo.setText("Port mapping failed.\nWait a 20 seconds and try again, or map it manually."));
							}
							Platform.runLater(() -> lblInfo.setText("Port was already mapped. Attempted to unmap it but was unsuccessful. Try again in 20 seconds or map the port manually."));
						}
					}
					else if(!UPnP.openPortTCP(LiveSettings.getTcpPortNumber())) {
						Platform.runLater(() -> lblInfo.setText("Port mapping failed.\nWait a 20 seconds and try again, or map it manually."));
					}
					else {
						if(UPnP.isMappedTCP(LiveSettings.getTcpPortNumber())) {
							Platform.runLater(() -> lblInfo.setText("Port mapping was successful!\nYou can now receive Gists at your IP address."));
							response = true;
						}
					}
				}
			}
			else {
				if (UPnP.isMappedTCP(LiveSettings.getTcpPortNumber())) {
					if(UPnP.closePortTCP(LiveSettings.getTcpPortNumber())) {
						Platform.runLater(() -> {
							if(lblInfo != null)
								lblInfo.setText("Port successfully unmapped from router.");
						});
						response = true;
					}
					else Platform.runLater(() -> {
						if(lblInfo != null)
							lblInfo.setText("Could not unmap port from your router.\nNeeds to be done manually.");
					});
				}
			}
		return response;
	}

	private boolean mappingPorts = false;
	public void waitForTransport() {
		receiveWindowClosed = false;
		double width=350;
		double height = 350;
		double lblWidth = width*.8;
		Label text = new Label();
		text.setText("Click on Receive to start waiting for an incoming Gist object. For more information, click on Info.");
		text.setWrapText(true);
		text.setMinHeight(60);
		String txtInfo = "You can share Gists with other GistFX users. GistFX uses standard TCP/IP communication to transport Gists. If the person you are exchanging Gists with is on the Internet (and not within your local network), GistFX will need port number " + LiveSettings.getTcpPortNumber() + " redirected from your router to this machine. If your router has UPnP ability, then check the UPnP box so GistFX can attempt to redirect that port for you, or you can add the port redirection rule into your router manually if you desire.\n\nIf you chose to let GistFX do that, then after checking the box, you will be notified if the port was redirected successfully or not.\n\nOnce everything is ready, click on the Receive button so GistFX can start listening for an inbound Gist. Next, give the sender your public IP address if they are sending the Gist over the Internet, or your private IP address if they are on your local network.\n\nGists are encrypted with a strong password before they are sent over any network. The password is hard coded into GistFX. The sender can optionally assign their own password before sending the Gist, and you must then type in that password after the Gist has been delivered to your machine.";
		Button btnReceive = new Button("Receive");
		Button btnInfo = new Button("Info");
		Button btnClose = new Button("Close");
		CheckBox cbUPnP= new CheckBox();
		Label lblCB = new Label("UPnP");
		Label lblUPnPInfo = new Label();
		Label lblServer = new Label();
		Label lblPublic = new Label("Internet IP Address");
		Label lblPrivate = new Label("Private IP Address");
		Label lblPublicIP = new Label();
		Label lblPrivateIP = new Label();
		text.setMinWidth(lblWidth);
		lblServer.setMinWidth(lblWidth);
		lblPublic.setMinWidth(width*.4);
		lblPrivate.setMinWidth(width*.4);
		lblPublicIP.setMinWidth(width*.4);
		lblPrivateIP.setMinWidth(width*.4);
		lblServer.setWrapText(true);
		lblServer.setMinHeight(50);
		lblServer.setAlignment(Pos.CENTER);
		cbUPnP.setOnAction(e->{
			new Thread(() -> {
				if(!upNp(lblServer,cbUPnP.isSelected())) {
					cbUPnP.setSelected(false);
				}
			}).start();
		});
		btnReceive.setOnAction(e -> {
			Listener.acceptGists();
			Listener.startServer(lblServer);
			new Thread(() -> {
				String info = lblServer.getText();
				while(!info.toLowerCase().contains("success") || !info.toLowerCase().contains("fail")) {
					Action.sleep(100);
					info = lblServer.getText();
				}
				Listener.rejectGists();
			}).start();
		});
		btnClose.setOnAction(e->{
			closeReceiveWindow();
		});
		btnInfo.setOnAction(e->CustomAlert.showInfo(txtInfo,SceneOne.getWindow(waitingSceneId)));
		new Thread(() -> {
			Platform.runLater(() -> {
				lblServer.setText("Retrieving your IP Addresses\nThis could take a minute.");
			});
			String publicIP = UPnP.getExternalIP();
			String privateIP = UPnP.getLocalIP();

			if(publicIP == null) Platform.runLater(() -> lblServer.setText("Unable to retrieve IP Addresses, check your router."));
			else if (publicIP.isEmpty()) Platform.runLater(() -> lblServer.setText("Unable to retrieve IP Addresses, check your router."));
			if(privateIP == null) Platform.runLater(() -> lblServer.setText("Unable to retrieve IP Addresses, check your router."));
			else if (privateIP.isEmpty()) Platform.runLater(() -> lblServer.setText("Unable to retrieve IP Addresses, check your router."));
			if (publicIP != null && privateIP != null) Platform.runLater(() -> {
				lblPublicIP.setText(publicIP);
				lblPrivateIP.setText(privateIP);
			});
			if(!mappingPorts) Platform.runLater(() -> lblServer.setText(""));
		}).start();
		HBox boxIPLabels = newHBox(lblWidth,35,lblPublic,lblPrivate);
		boxIPLabels.setPadding(new Insets(1,1,1,1));
		HBox boxIPAddys = newHBox(lblWidth,37,lblPublicIP,lblPrivateIP);
		boxIPAddys.setPadding(new Insets(1,1,1,1));
		HBox boxUPnP = newHBox(lblWidth,20,lblCB,cbUPnP,lblUPnPInfo);
		VBox boxIPs = new VBox(boxIPLabels,boxIPAddys);
		boxIPs.setSpacing(1);
		boxIPs.setPadding(new Insets(1,1,1,1));
		VBox vbox = new VBox(text,boxUPnP,boxIPs,boxIPAddys,lblServer);
		vbox.setPadding(new Insets(20,20,20,20));
		vbox.setSpacing(20);
		vbox.setMinWidth(lblWidth);
		ToolWindow toolWindow = new ToolWindow.Builder(vbox)
				.size(width,height)
				.title("Gist Listener")
				.setSceneId(waitingSceneId)
				.addButton(btnInfo)
				.addButton(btnClose)
				.addButton(btnReceive)
				.onCloseEvent(e->closeReceiveWindow())
				.attachToStage(SceneOne.getStage("GistWindow"))
				.build();
		toolWindow.showAndWait();
	}


	private final String waitingSceneId = "Listener";

	private void createControls() {
		lblTo        = new Label("Send To");
		cbHosts      = new ChoiceBox<>();
		lblPassword  = new Label("Use Password");
		tfPassword   = new TextField();
		cbPassword   = new CheckBox();
		taPayload    = new TextArea();
		btnSend      = new Button("Send");
		btnCancel    = new Button("Cancel");
		btnEditHosts = new Button("Manage Hosts");
		buttonBox    = new HBox(btnSend,btnCancel);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(15);
		btnSend.setMinWidth(55);
		cbHosts.setMinWidth(155);
		cbHosts.setMaxWidth(155);
		btnEditHosts.setMinWidth(105);
		btnSend.setMinHeight(30);
		btnCancel.setMinHeight(30);
		lblTo.setMinWidth(35);
		lblTo.setAlignment(Pos.CENTER_LEFT);
		lblTo.setMinHeight(30);
		lblPassword.setMinWidth(75);
		lblPassword.setAlignment(Pos.CENTER_LEFT);
		lblPassword.setMinHeight(30);
		tfPassword.setMinHeight(30);
		ap = ap(lblTo, cbHosts, lblPassword, cbPassword, tfPassword, taPayload, btnEditHosts, buttonBox);
		setAnchors(lblTo,10,-1,10,-1);
		setAnchors(cbHosts,65,-1,10,-1);
		setAnchors(btnEditHosts,227, -1, 10, -1);
		setAnchors(lblPassword,10, -1, 45, -1);
		setAnchors(cbPassword,97.5, -1, 52.5, -1);
		setAnchors(tfPassword,125, 10, 45, -1);
		setAnchors(buttonBox, 10, 10, -1, 10);
		setAnchors(btnCancel,-1,10,-1,10);
		setAnchors(taPayload,10,10,85,50);
		Tooltip.install(tfPassword,Action.newTooltip(
				"""
				GistFX uses a VERY strong password to encrypt data.
				However, if you wish to use your own password, set it
				here and make sure the receiver knows it. This might
				be a good idea in an environment with many developers
				to ensure that the right person gets the payload.
				"""));
		Tooltip.install(cbHosts,Action.newTooltip("Select the host machine to send the payload to"));
	}

	private void setControlActions() {
		btnEditHosts.setOnAction(e-> {
			Collection<String> hostCollection = Hosts.showWindow(SceneOne.getStage(sceneId));
			cbHosts.getItems().setAll(hostCollection);
		});
		btnCancel.setOnAction(e-> SceneOne.close(sceneId));
		btnSend.setOnAction(e-> {
			String host = cbHosts.getValue();
			if (host != null) {
				if (!host.isEmpty()) {
					sendObject();
				}
			}
		});
		taPayload.setEditable(false);
		Collection<String> hostCollection = Action.getHostCollection();
		if (hostCollection != null) cbHosts.getItems().setAll();
		cbHosts.getItems().setAll(Action.getHostCollection());
		tfPassword.visibleProperty().bind(cbPassword.selectedProperty());
		cbPassword.setSelected(false);
	}

	private void sendObject() {
		new Thread(() -> {
			String         host           = cbHosts.getValue();
			PayloadBuilder payloadBuilder = null;
			btnSend.setDisable(true);
			StringBuilder message = new StringBuilder("Hashing Password");
			if(cbPassword.isSelected()){
				connecting = true;
				showConnecting(message);
			}
			switch(type) {
				case CATEGORY -> 	payloadBuilder = new PayloadBuilder(Action.getGitHubUsername(), gistList, categoryName, tfPassword.getText());
				case GIST -> 		payloadBuilder = new PayloadBuilder(Action.getGitHubUsername(), gist, tfPassword.getText());
				case FILE -> 		payloadBuilder = new PayloadBuilder(Action.getGitHubUsername(), gistFile, tfPassword.getText());
			}
			Payload payload = payloadBuilder.getPayload();
			connecting = false;
			try {
				InetAddress   hostAddress   = InetAddress.getByName(host);
				Integer       port          = LiveSettings.getTcpPortNumber();
				connecting = true;
				message = new StringBuilder("Attempting To Connect to ");
				message.append(cbHosts.getValue());
				showConnecting(message);
				Socket        socket        = new Socket(hostAddress, port);
				connecting = false;
				DataOutputStream   dataOutputStream = new DataOutputStream(socket.getOutputStream());
				ObjectOutputStream oos              = new ObjectOutputStream(dataOutputStream);
				DataInputStream    dataInputStream  = new DataInputStream(socket.getInputStream());
				dataOutputStream.writeUTF(this.payload);
				oos.writeObject(payload);
				String response = dataInputStream.readUTF();
				if (response.equals(received)) {
					Platform.runLater(() -> {
						CustomAlert.showInfo("Data sent successfully", SceneOne.getStage(sceneId));
						SceneOne.close(sceneId);
					});
				}
				else {
					Platform.runLater(() -> {
						CustomAlert.showInfo("Data send FAILED", SceneOne.getStage(sceneId));
						SceneOne.close(sceneId);
					});
				}
				dataInputStream.close();
				dataOutputStream.close();
				oos.close();
				socket.close();
			}
			catch (IOException e) {
				connecting = false;
				System.err.println(e.getMessage());
				Platform.runLater(() -> {
					CustomAlert.showWarning("Connection Errored or Timed Out");
					btnSend.setDisable(false);
				});
			}
		}).start();
	}

	private void showConnecting(StringBuilder message) {
		new Thread(() -> {
			int count = 1;
			while (connecting) {
				if (count >=10) {
					final String msg = message.toString();
					Platform.runLater(() -> taPayload.setText(msg));
					count = 1;
				}
				count++;
				Action.sleep(100);
				message.append(".");
			}
			showPayloadInfo();
		}).start();
	}

	private void showPayloadInfo() {
		StringBuilder sb = new StringBuilder();
		if (!categoryName.isEmpty()) {
			sb.append("Category: ").append(categoryName).append(en2);
		}
		if(gist != null) {
			sb.append(tab).append("Gist: ").append(gist.getName()).append(en).append(tab).append("Files:").append(en);
			for(GistFile file : gist.getFiles()) {
				sb.append(tab2).append(file.getFilename()).append(en);
			}
		}
		else if (gistList != null) {
			for(Gist gist : gistList) {
				sb.append(tab).append("Gist: ").append(gist.getName()).append(en).append(tab).append("Files:").append(en);
				for(GistFile file : gist.getFiles()) {
					sb.append(tab2).append(file.getFilename()).append(en);
				}
				sb.append(en);
			}
		}
		else {
			sb.append(tab).append("Gist File: ").append(gistFile.getFilename());
		}
		taPayload.setText(sb.toString());
	}

	public void show (Stage callingStage) {
		createControls();
		setControlActions();
		showPayloadInfo();
		SceneOne.set(ap,sceneId,callingStage).centered().size(width,height).showAndWait();
	}
}
