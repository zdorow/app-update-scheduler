package com.app.update.scheduler;

import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        launch(args);
    }

	@Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/app_update_schedule.fxml"));
        
        Scene scene = new Scene(root, 475, 325);
        
        primaryStage.setTitle("App Update Scheduler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}