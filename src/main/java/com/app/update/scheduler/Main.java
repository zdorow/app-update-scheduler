package com.app.update.scheduler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/view/app_update_schedule.fxml"));
		
		Scene scene = new Scene(root, 500, 300);
		
		primaryStage.setTitle("App Update Scheduler");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
