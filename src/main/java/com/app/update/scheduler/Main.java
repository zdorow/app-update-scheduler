package com.app.update.scheduler;

import java.time.format.DateTimeFormatter;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.app.update.scheduler.progress.ProgressForm;
import javafx.concurrent.WorkerStateEvent;

public abstract class Main extends Application {
    
        //ProgressForm pForm = new ProgressForm();   
        
            public static void main(String[] args)
    {
        launch(args);              
    }
//Task<Void> mainTask = new Task<Void>(){
            
            @Override
	public void start(Stage primaryStage) throws Exception {
    
                
		Parent root = FXMLLoader.load(getClass().getResource("/view/app_update_schedule.fxml"));
		
		Scene scene = new Scene(root, 500, 300);
		//pForm.activateProgressBar(mainTask);
		primaryStage.setTitle("App Update Scheduler");
		primaryStage.setScene(scene);
		primaryStage.show();
        }

//            @Override
//            protected Void call() throws Exception {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//        };
}


