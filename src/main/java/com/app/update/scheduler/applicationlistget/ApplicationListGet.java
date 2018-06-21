package com.app.update.scheduler.applicationlistget;

import com.app.update.scheduler.jamfpro.api.JssApi;
import com.app.update.scheduler.jamfpro.api.JssApiException;
import com.app.update.scheduler.jamfpro.model.MobileDeviceApplication;
import com.app.update.scheduler.jamfpro.model.MobileDeviceApplications;
import com.app.update.scheduler.jaxb.JaxbObjectConverter;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import javafx.scene.text.Text;
import javax.xml.bind.JAXBException;


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
          
    @Override
    protected List<Integer> call() throws JssApiException, JAXBException {
         	actiontarget.setText("Gathering information on Mobile Device Applications");
			
			String allApps = jssApi.get("mobiledeviceapplications");
			MobileDeviceApplications mobileDeviceApplications = JaxbObjectConverter.unmarshall(MobileDeviceApplications.class, allApps);
			int count = 0;
			List<Integer> deviceIdList = new ArrayList<>(0);
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
