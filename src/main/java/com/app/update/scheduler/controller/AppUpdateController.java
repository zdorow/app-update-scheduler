package com.app.update.scheduler.controller;

import com.app.update.scheduler.applicationlistget.ApplicationListGet;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.app.update.scheduler.jamfpro.api.JssApi;
import com.app.update.scheduler.jamfpro.api.JssApi.FORMAT;
import com.app.update.scheduler.jamfpro.api.JssApiException;
import com.app.update.scheduler.jamfpro.model.MobileDeviceApplication;
import com.app.update.scheduler.jamfpro.model.MobileDeviceApplications;
import com.app.update.scheduler.jamfpro.model.MobileDeviceApplications.MobileDeviceApplicationShell;
import com.app.update.scheduler.jaxb.JaxbObjectConverter;
import com.app.update.scheduler.option.AppUpdateSchedulerOption;
import com.app.update.scheduler.option.TimeFrame;
import com.app.update.scheduler.option.impl.EvenlySpreadSchedulerOption;
import com.app.update.scheduler.option.impl.TimeFrameSchedulerOption;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


public class AppUpdateController implements Initializable {
	
	@FXML
	private TextField jamfProServerUrl;
	
	@FXML
	private TextField userName;
	
	@FXML
	private PasswordField password;

	@FXML
	private ComboBox<String> appSchedulerOptions;
	
	@FXML
	private Label timeFrameLabel;
	
	@FXML
	private HBox timeFrameOptions;
	
	@FXML
	private ComboBox<String> timeFrameStartOptions;
	
	@FXML
	private ComboBox<String> timeFrameEndOptions;
	
	@FXML
	private Text actiontarget;
        
        @FXML
	private ProgressBar progressBar;
	
	@FXML
	protected void handleSubmitButtonAction(ActionEvent event){
            // Clear any existing text
            actiontarget.setText("");

            AppUpdateSchedulerOption schedulerOption = AppUpdateSchedulerOption.fromDisplayText(appSchedulerOptions.getValue());
            JssApi jssApi = new JssApi(jamfProServerUrl.getText(), userName.getText(), password.getText(), FORMAT.XML, FORMAT.XML);

	try {
            Task<List<Integer>> applicationList = new ApplicationListGet(jssApi, actiontarget);
                progressBar.progressProperty().bind(applicationList.progressProperty());
       
                Thread appThread = new Thread(applicationList);
                appThread.start();
                
            //Setting the device list up if all went well getting the data                   
            applicationList.setOnSucceeded(e -> {
                List<Integer> appIdList = applicationList.getValue();
   	
                    switch (schedulerOption) {
			case TimeInterval:
                            TimeFrame timeFrameStart = TimeFrame.fromDisplayText(timeFrameStartOptions.getValue());
                            TimeFrame timeFrameEnd = TimeFrame.fromDisplayText(timeFrameEndOptions.getValue());
					
                            Task<Boolean> timeFrameScheduler = new TimeFrameSchedulerOption(jssApi, appIdList, actiontarget, timeFrameStart, timeFrameEnd);
                                        
                            progressBar.progressProperty().bind(timeFrameScheduler.progressProperty());
                                        
                            Thread myThread1 = new Thread(timeFrameScheduler);
                            myThread1.start();
                                        
                timeFrameScheduler.setOnSucceeded(ex -> {                
                    actiontarget.setText("Done scheduling apps ");
            });                                        
			break;
			default:
                            Task<Boolean> evenScheduler = new EvenlySpreadSchedulerOption(jssApi, appIdList, actiontarget);
                                    
                            progressBar.progressProperty().bind(evenScheduler.progressProperty());
                                    
                            Thread myThread = new Thread(evenScheduler);
                            myThread.start();
                                    
                            evenScheduler.setOnSucceeded(ex -> {                
                    actiontarget.setText("Done scheduling apps ");
                    });                        
			break;

			}
                        		            });
    applicationList.setOnFailed(e ->
      {                
            Integer Response = JssApiException.getHttpResponseCode();
            System.out.println("Current Response:" + Response);
            switch (Response) {
                case 0:
                    actiontarget.setText("Username and/or password not accepted.");
                    break;
                case -1:
                    actiontarget.setText("URL was not found.");
                    break;
                default:
                    actiontarget.setText("Something really went wrong. Please file an issue on Github1");
                    break;
            }
                            });
	} catch(Exception e){
            actiontarget.setText("Something really went wrong. Please file an issue on Github2");
                }
        }
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		for (AppUpdateSchedulerOption schedulerOption : AppUpdateSchedulerOption.values()) {
			appSchedulerOptions.getItems().add(schedulerOption.getDisplayText());
		}
		
		for (TimeFrame timeFrameOption : TimeFrame.values()) {
			timeFrameStartOptions.getItems().add(timeFrameOption.getDisplayText());
		}
		
		for (TimeFrame timeFrameOption : TimeFrame.values()) {
			timeFrameEndOptions.getItems().add(timeFrameOption.getDisplayText());
		}
		
		appSchedulerOptions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					AppUpdateSchedulerOption schedulerOption = AppUpdateSchedulerOption.fromDisplayText(observable.getValue());
					
					if (schedulerOption == AppUpdateSchedulerOption.TimeInterval) {
						timeFrameLabel.setDisable(false);
						timeFrameOptions.setDisable(false);
					} else {
						timeFrameLabel.setDisable(true);
						timeFrameOptions.setDisable(true);
					}
				} catch (Exception e) {  }
			}
		});
	}
}