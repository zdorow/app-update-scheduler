package com.app.update.scheduler.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.app.update.scheduler.eventhandler.JssApiResponseHandler;
import com.app.update.scheduler.jamfpro.api.JssApi;
import com.app.update.scheduler.jamfpro.api.JssApi.FORMAT;
import com.app.update.scheduler.option.AppUpdateSchedulerOption;
import com.app.update.scheduler.option.TimeFrame;
import com.app.update.scheduler.service.ApplicationListService;
import com.app.update.scheduler.service.TimeFrameSchedulerService;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	protected void handleSubmitButtonAction(ActionEvent event) {
		// Clear any existing text
		actiontarget.setText("");
		
		progressBar.setVisible(true);

		AppUpdateSchedulerOption schedulerOption = AppUpdateSchedulerOption.fromDisplayText(appSchedulerOptions.getValue());
		//JssApi jssApi = new JssApi(jamfProServerUrl.getText(), userName.getText(), password.getText(), FORMAT.XML, FORMAT.XML);
JssApi jssApi = new JssApi("https://zdorow.jamfcloud.com", "Eauk", "jamf1234", FORMAT.XML, FORMAT.XML);

		try {

			ApplicationListService appListService = new ApplicationListService(jssApi, actiontarget, progressBar);
			
			appListService.setOnSucceeded(e -> {
				System.out.println("ApplicationListService has succeeded.");
				
				List<Integer> appIdList = appListService.getValue();
				System.out.println(appIdList);
				TimeFrameSchedulerService timeFrameService = new TimeFrameSchedulerService(jssApi, appIdList, actiontarget, timeFrameStartOptions, timeFrameEndOptions, schedulerOption, progressBar);
                                timeFrameService.setOnSucceeded(ex -> {
                                    System.out.println("TimeFrameSchedulerService has succeeded.");
                                });
                                timeFrameService.setOnFailed(ex -> {
                                    System.out.println("TimeFrameSchedulerService has been stopped for no time frame set.");
                                });
				timeFrameService.start();
				
			});
			
			appListService.setOnFailed(new JssApiResponseHandler(jssApi, actiontarget));
			appListService.start();
			
		} catch(Exception e){
                        actiontarget.setText("Something really went wrong. Please file an issue on Github.");
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		for (AppUpdateSchedulerOption schedulerOption : AppUpdateSchedulerOption.values()) {
			appSchedulerOptions.getItems().add(schedulerOption.getDisplayText());
			
			if (schedulerOption.isDefaultSelectedValue()) {
				appSchedulerOptions.getSelectionModel().select(schedulerOption.getDisplayText());
			}
		}

		for (TimeFrame timeFrameOption : TimeFrame.values()) {
                        timeFrameStartOptions.getItems().add(timeFrameOption.getDisplayText());
		}

		for (TimeFrame timeFrameOption : TimeFrame.values()) {
			timeFrameEndOptions.getItems().add(timeFrameOption.getDisplayText());
		}

		appSchedulerOptions.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    try {
                        AppUpdateSchedulerOption schedulerOption = AppUpdateSchedulerOption.fromDisplayText(observable.getValue());
                        
                        if (schedulerOption == AppUpdateSchedulerOption.TimeInterval) {
                            timeFrameLabel.setDisable(false);
                            timeFrameOptions.setDisable(false);
                        } else {
                            timeFrameLabel.setDisable(true);
                            timeFrameOptions.setDisable(true);
                        }
                    } catch (Exception e) {
                        actiontarget.setText("Something really went wrong. Please file an issue on Github.");
                    }
                });
	}
}