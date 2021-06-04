package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {
	
	private Controller control;
	
	public Main() {
		control = new Controller();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setMaximized(true);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
		fxmlLoader.setController(control);
		Parent root = fxmlLoader.load();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Trip Routing");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.jpg")));
		control.setStage(primaryStage);
		control.loader("add-from-csv.fxml");
		primaryStage.show();
	}
}