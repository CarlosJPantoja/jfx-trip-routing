package ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Graph;

public class Controller {
	
	@FXML
	private VBox mainVBox;
	@FXML
	private Label warning;
	@FXML
	private TextField URL, from, to, route, miles;
	@FXML
	private ComboBox<String> chooseFrom, chooseTo, sptAlgorithm;
	
	private Graph graph;
	private Stage stage;
	private Alert alert;
	
	private void launchAlert(String msg) throws IOException {
		alert = new Alert(AlertType.WARNING);
		alert.setTitle("Trip Routing");
		alert.getDialogPane().getChildren().clear();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("alert.fxml"));
		fxmlLoader.setController(this);
		Parent root = fxmlLoader.load();
		alert.getDialogPane().setHeader(root);
		warning.setText(msg);
		Stage alertStage = (Stage)alert.getDialogPane().getScene().getWindow();
		alertStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.jpg")));
		alert.showAndWait();
	}
	
	@FXML
	public void alertTouch(ActionEvent event) {
		alert.close();
	}
	
	@FXML
	public void openFileChooser(ActionEvent event) throws IOException {
		launchAlert("Organize each line of your file as follows (No headings)"
				+ "\n                 From, To, Route, Miles                 "
				+ "\n           Remember that miles are an integer           ");
		try{
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
			URL.setText(fileChooser.showOpenDialog(stage).getAbsolutePath());
		} catch(NullPointerException e) {
			URL.setText("");
		}
	}
	
	@FXML
	public void addManually(ActionEvent event) throws IOException {
		loader("add-manually.fxml");
	}
	
	@FXML
	public void addFromCSV(ActionEvent event) throws IOException {
		loader("add-from-csv.fxml");
	}
	
	@FXML
	public void nextCSV(ActionEvent event) throws IOException {
		if(!isEmpty(URL)) {
			try {
				graph = new Graph(URL.getText().trim());
				loader("menu.fxml");
			} catch(NumberFormatException | IndexOutOfBoundsException e) {
				launchAlert("The file does not have the indicated format");
			}
		} else {
			launchAlert("Choose a file");
		}
	}
	
	@FXML
	public void backMenu(ActionEvent event) throws IOException {
		loader("menu.fxml");
	}
	
	@FXML
	public void back(ActionEvent event) throws IOException {
		loader("add-from-csv.fxml");
	}
	
	@FXML
	public void add(ActionEvent event) throws IOException {
		if(!isEmpty(from) && !isEmpty(to) && !isEmpty(route) && !isEmpty(miles)) {
			graph.addRoute(from.getText().trim(), to.getText().trim(), route.getText().trim(), Integer.parseInt(miles.getText().trim()));
			launchAlert("Route "+route.getText().trim()+" from "+from.getText().trim()+" to "
						+to.getText().trim()+" with "+miles.getText().trim()+" miles of travel was added successfully");
			from.setText("");
			to.setText("");
			route.setText("");
			miles.setText("");
		} else {
			launchAlert("Fill in all the boxes");
		}
	}
	
	@FXML
	public void shortestPath(ActionEvent event) throws IOException {
		loader("shortest-path.fxml");
		sptAlgorithm.getItems().addAll("Floyd-Warshall", "Dijkstra");
		sptAlgorithm.setValue("Floyd-Warshall");
		for(int i=0; i<graph.getNodes().size(); i++) {
			chooseFrom.getItems().add(graph.getNodes().get(i));
			chooseTo.getItems().add(graph.getNodes().get(i));
		}
	}
	
	@FXML
	public void shortestPathTree(ActionEvent event) throws IOException {
		if(chooseFrom.getValue()!=null && chooseTo.getValue()!=null && !chooseFrom.getValue().equals(chooseTo.getValue()))
			launchAlert(graph.shortestPath(chooseFrom.getValue(), chooseTo.getValue(), sptAlgorithm.getValue()));
		else
			launchAlert("From and To cannot be the same or be empty");
	}
	
	@FXML
	public void minimumSpanning(ActionEvent event) throws IOException {
		loader("minimum-spanning.fxml");
		sptAlgorithm.getItems().addAll("Kruskal", "Prim");
		sptAlgorithm.setValue("Kruskal");
	}
	
	@FXML
	public void next(ActionEvent event) throws IOException {
		if(graph.getNodes().size()>=2) {
			loader("menu.fxml");
		} else {
			launchAlert("Enter paths first");
		}
		
	}
	
	@FXML
	public void minimumSpanningTree(ActionEvent event) throws IOException {
		if(sptAlgorithm.getValue().equals("Kruskal"))
			launchAlert(graph.kruskal());
		else
			launchAlert(graph.prim());
	}
	
	public Controller() {
		graph = new Graph();
	}
	
	public void loader(String fmxl) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fmxl));
		fxmlLoader.setController(this);
		Parent root = fxmlLoader.load();
		mainVBox.getChildren().clear();
		mainVBox.getChildren().add(root);
	}
	
	private boolean isEmpty(TextField tf) {
		if(tf.getText() == null || tf.getText().trim().equals(""))
			return true;
		else
			return false;
	}
	
	public void setStage(Stage s) {
		stage = s;
	}
}
