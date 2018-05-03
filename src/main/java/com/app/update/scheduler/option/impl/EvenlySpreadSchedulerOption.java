package com.app.update.scheduler.option.impl;

import java.util.List;

import com.app.update.scheduler.jamfpro.api.JssApi;
import com.app.update.scheduler.option.TimeFrame;

import javafx.concurrent.Task;
import javafx.scene.text.Text;

public class EvenlySpreadSchedulerOption extends Task<Boolean> {
	
	private static final String updateXml = "<mobile_device_application><general><itunes_sync_time>%d</itunes_sync_time></general></mobile_device_application>";
	
	private JssApi jssApi;
	private List<Integer> deviceIdList;
	private Text actiontarget;
	private double startTime = 0;
	private double endTime = 86400; // Number of seconds in a day
	
	public EvenlySpreadSchedulerOption(JssApi jssApi, List<Integer> deviceIdList, Text actiontarget) {
		this.jssApi = jssApi;
		this.deviceIdList = deviceIdList;
		this.actiontarget = actiontarget;
	}
	
	public EvenlySpreadSchedulerOption(JssApi jssApi, List<Integer> deviceIdList, Text actiontarget, TimeFrame timeFrameStart, TimeFrame timeFrameEnd) {
		this(jssApi, deviceIdList, actiontarget);
		
		startTime = timeFrameStart.calculateNumberOfSecondsFromMidnight();
		endTime = timeFrameEnd.calculateNumberOfSecondsFromMidnight();
	}

	@Override
	protected Boolean call() throws Exception {
		
		actiontarget.setText("Status: Calculating spread of application updates");
		
		double spread = endTime / new Double(deviceIdList.size());
		
		try {
			actiontarget.setText("Status: Updating application update schedules");
			
			for (int id : deviceIdList) {
				jssApi.put("mobiledeviceapplications/id/" + id, String.format(updateXml, Math.round(startTime)));
				
				startTime = startTime + spread;
			}
		} catch (Exception e) {
			actiontarget.setText("There was an error while processing app updates.");
			return false;
		}
		
		return true;
	}
}
