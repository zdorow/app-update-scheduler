package com.app.update.scheduler.controller.validator;

import org.apache.commons.lang3.StringUtils;

import com.app.update.scheduler.controller.form.AppUpdateForm;
import com.app.update.scheduler.option.AppUpdateSchedulerOption;

public class AppUpdateValidator {

	private AppUpdateForm appUpdateForm;
	private int errorCount;
	
	public AppUpdateValidator(AppUpdateForm appUpdateForm) {
		this.appUpdateForm = appUpdateForm;
	}
	
	public void validate() {
		if (StringUtils.isEmpty(appUpdateForm.getJamfProServerUrl().getText())) {
			markError("Jamf Pro Server URL may not be empty");
			return;
		}
		
		if (StringUtils.isEmpty(appUpdateForm.getUserName().getText())) {
			markError("Jamf Pro User Name may not be empty");
			return;
		}
		
		if (StringUtils.isEmpty(appUpdateForm.getPassword().getText())) {
			markError("Jamf Pro Password may not be empty");
			return;
		}
		
		AppUpdateSchedulerOption schedulerOption = AppUpdateSchedulerOption.fromDisplayText(appUpdateForm.getAppSchedulerOptions().getValue());
		if (schedulerOption == AppUpdateSchedulerOption.TimeInterval) {
			if (StringUtils.isEmpty(appUpdateForm.getTimeFrameStartOptions().getValue())) {
				markError("Scheduling Timeframe Start Time may not be empty");
				return;
			}
			
			if (StringUtils.isEmpty(appUpdateForm.getTimeFrameEndOptions().getValue())) {
				markError("Scheduling Timeframe End Time may not be empty");
				return;
			}
		}
	}
	
	public boolean hasErrors() {
		return errorCount > 0;
	}
	
	private void markError(String errorMessage) {
		appUpdateForm.getActiontargetPane().getStyleClass().clear();
		appUpdateForm.getActiontargetPane().getStyleClass().add("alert");
		appUpdateForm.getActiontargetPane().getStyleClass().add("alert-danger");
		appUpdateForm.getActiontarget().setText(errorMessage);
		
		errorCount++;
	}
}
