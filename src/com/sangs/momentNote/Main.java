package com.sangs.momentNote;
	

import com.sangs.momentNote.UIController;
import com.sangs.momentNote.UIModel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class Main extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		try {
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("ui.fxml"));
				loader.setControllerFactory(t -> new UIController(new UIModel()));
				stage.setScene(new Scene(loader.load()));

				stage.show();
				UIController controller = loader.getController();
				controller.setTextArea();
				
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
	
	
	}
	
	public static void main(String[] args) {
		dbDriver data = new dbDriver();
		data.createNewDatabase("platypus.db");
		data.createTables();
		launch(args);
	}
	
}
