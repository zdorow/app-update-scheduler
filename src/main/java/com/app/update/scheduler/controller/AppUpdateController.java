package com.app.update.scheduler.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.app.update.scheduler.jamfpro.api.JssApi;
import com.app.update.scheduler.jamfpro.api.JssApi.FORMAT;
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
	protected void handleSubmitButtonAction(ActionEvent event) {
            
		// Clear any existing text
		actiontarget.setText("");
		
		AppUpdateSchedulerOption schedulerOption = AppUpdateSchedulerOption.fromDisplayText(appSchedulerOptions.getValue());
		JssApi jssApi = new JssApi(jamfProServerUrl.getText(), userName.getText(), password.getText(), FORMAT.XML, FORMAT.XML);
		
		try { 
			actiontarget.setText("Status: Gathering information on Mobile Device Applications");
			
			String result = jssApi.get("mobiledeviceapplications");
			MobileDeviceApplications mobileDeviceApplications = JaxbObjectConverter.unmarshall(MobileDeviceApplications.class, result);
			
			List<Integer> deviceIdList = new ArrayList<>();
			
			for (MobileDeviceApplicationShell appShell : mobileDeviceApplications.getMobileDeviceApplicationList()) {
				String applicationString = jssApi.get("mobiledeviceapplications/id/" + appShell.getId() + "/subset/General");
				MobileDeviceApplication application = JaxbObjectConverter.unmarshall(MobileDeviceApplication.class, applicationString);
				
				if (application.getGeneral() != null && application.getGeneral().isKeepDescriptionAndIconUpToDate()) {
					deviceIdList.add(application.getGeneral().getId());
				}
			}
			
			System.out.println("Checking size of device ID list");
			if (deviceIdList.isEmpty()) {
				actiontarget.setText("No applications were found with app updates set.");
				return;
			}
			
			Task<Boolean> scheduler = null;
			
			switch (schedulerOption) {
				case EvenlySpread:
					scheduler = new EvenlySpreadSchedulerOption(jssApi, deviceIdList, actiontarget);
					break;
				case TimeInterval:
					TimeFrame timeFrameStart = TimeFrame.fromDisplayText(timeFrameStartOptions.getValue());
					TimeFrame timeFrameEnd = TimeFrame.fromDisplayText(timeFrameEndOptions.getValue());
					
					scheduler = new TimeFrameSchedulerOption(jssApi, deviceIdList, actiontarget, timeFrameStart, timeFrameEnd);
					break;
				default:
					break;
			}
			
			scheduler.run();
			
			boolean success = scheduler.get();
			
			if (success) {
				actiontarget.setText("The operation has completed successfully.");
			}
			
		} catch (Exception e) {
			actiontarget.setText("There was an error processing your request.");
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
						timeFrameLabel.setVisible(true);
						timeFrameOptions.setVisible(true);
					} else {
						timeFrameLabel.setVisible(false);
						timeFrameOptions.setVisible(false);
					}
				} catch (Exception e) {  }
			}
		});
	}
}
