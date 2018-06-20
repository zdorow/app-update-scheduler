package com.app.update.scheduler.applicaitonlistget;

import com.app.update.scheduler.jamfpro.api.JssApi;
import com.app.update.scheduler.jamfpro.model.MobileDeviceApplication;
import com.app.update.scheduler.jamfpro.model.MobileDeviceApplications;
import com.app.update.scheduler.jaxb.JaxbObjectConverter;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import javafx.scene.text.Text;


/*
Programmer: Zachary Dorow
 */
public class ApplicationListGet extends Task <List<Integer>> {
    
    	public JssApi jssApi;
	public Text actiontarget;

	
	public ApplicationListGet(JssApi jssApi, Text actiontarget) {
		this.jssApi = jssApi;
		this.actiontarget = actiontarget;
                
	}
//    public List<Integer> getDeviceIdList() throws Exception {
//        	actiontarget.setText("Status: Gathering information on Mobile Device Applications");
//			
//			String result = jssApi.get("mobiledeviceapplications");
//			MobileDeviceApplications mobileDeviceApplications = JaxbObjectConverter.unmarshall(MobileDeviceApplications.class, result);
//			int count = 0;
//			List<Integer> deviceIdList = new ArrayList<>();
//			
//			for (MobileDeviceApplications.MobileDeviceApplicationShell appShell : mobileDeviceApplications.getMobileDeviceApplicationList()) {
//				String applicationString = jssApi.get("mobiledeviceapplications/id/" + appShell.getId() + "/subset/General");
//				MobileDeviceApplication application = JaxbObjectConverter.unmarshall(MobileDeviceApplication.class, applicationString);
//                                updateProgress(count, deviceIdList.size());
//                                count++;
//				
//				if (application.getGeneral() != null && application.getGeneral().isKeepDescriptionAndIconUpToDate()) {
//					deviceIdList.add(application.getGeneral().getId());
//				}
//			}
//			
//			System.out.println("Checking size of device ID list");
//			if (deviceIdList.isEmpty()) {
//				actiontarget.setText("No applications were found with app updates set.");
//                          return null;
//        }
//        return deviceIdList;
//    }               

    @Override
    protected List<Integer> call() throws Exception {
         	actiontarget.setText("Gathering information on Mobile Device Applications");
			
			String result = jssApi.get("mobiledeviceapplications");
			MobileDeviceApplications mobileDeviceApplications = JaxbObjectConverter.unmarshall(MobileDeviceApplications.class, result);
			int count = 0;
			List<Integer> deviceIdList = new ArrayList<>();
                        updateProgress(0, 1);
			
			for (MobileDeviceApplications.MobileDeviceApplicationShell appShell : mobileDeviceApplications.getMobileDeviceApplicationList()) {
				String applicationString = jssApi.get("mobiledeviceapplications/id/" + appShell.getId() + "/subset/General");
				MobileDeviceApplication application = JaxbObjectConverter.unmarshall(MobileDeviceApplication.class, applicationString);
                                
                                updateProgress(count, mobileDeviceApplications.getMobileDeviceApplicationList().size());
                                
                                count++;
				
				if (application.getGeneral() != null && application.getGeneral().isKeepDescriptionAndIconUpToDate()) {
					deviceIdList.add(application.getGeneral().getId());
				}
			}
			updateProgress(1, 1);
			System.out.println("Checking size of device ID list");
			if (deviceIdList.isEmpty()) {
				actiontarget.setText("No applications were found with app updates set.");
                          return null;
        }
                        
        return deviceIdList;
    }

}
