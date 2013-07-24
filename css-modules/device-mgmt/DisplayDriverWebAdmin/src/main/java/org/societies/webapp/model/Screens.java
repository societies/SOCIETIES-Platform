package org.societies.webapp.model;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.persistence.*;

@Entity
@Table(name="screen")
@ManagedBean(name = "screenBean")
public class Screens implements Serializable {

    private String screenID;
    private String locationID;
    private String ipAddress;

    public Screens()
    {

    }

    public Screens(String screenID,String locationID, String ipAddress)
    {
        this.screenID=screenID;
        this.locationID=locationID;
        this.ipAddress=ipAddress;
    }

    @Id
    @Column(name="screenID")
    public String getScreenID() {
        return screenID;
    }

    public void setScreenID(String screenID) {
        this.screenID = screenID;
    }

    @Column(name="locationID")
    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }


    @Column(name="ipAddress")
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


}