package com.app.update.scheduler.jamfpro.model;

import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mobile_device_application")
@XmlAccessorType(XmlAccessType.FIELD)
public class MobileDeviceApplication {
    private static final Logger LOG = Logger.getLogger(MobileDeviceApplication.class.getName());

	@XmlElement(name = "general")
	private MobileDeviceApplicationGeneral general;

	public MobileDeviceApplicationGeneral getGeneral() {
		return general;
	}

	public void setGeneral(MobileDeviceApplicationGeneral general) {
		this.general = general;
	}

	@Override
	public String toString() {
		return "MobileDeviceApplication [general=" + general + "]";
	}
}
