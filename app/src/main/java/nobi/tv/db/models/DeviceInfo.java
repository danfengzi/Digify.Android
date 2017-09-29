package nobi.tv.db.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Joel on 2/13/2017.
 */

public class DeviceInfo extends RealmObject {
    @PrimaryKey
    private int deviceId;
    private Date createdAt;
    private Date updatedAt;
    private String name;
    private int userId;
    private String deviceIdString;
    private String mode;
    private String portraitLogo;
    private String portraitMessage;
    private boolean kioskMode;
    private boolean queueMode;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDeviceIdString() {
        return deviceIdString;
    }

    public void setDeviceIdString(String deviceIdString) {
        this.deviceIdString = deviceIdString;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPortraitLogo() {
        return portraitLogo;
    }

    public void setPortraitLogo(String portraitLogo) {
        this.portraitLogo = portraitLogo;
    }

    public String getPortraitMessage() {
        return portraitMessage;
    }

    public void setPortraitMessage(String portraitMessage) {
        this.portraitMessage = portraitMessage;
    }

    public boolean isKioskMode() {
        return kioskMode;
    }

    public void setKioskMode(boolean kioskMode) {
        this.kioskMode = kioskMode;
    }

    public boolean isQueueMode() {
        return queueMode;
    }

    public void setQueueMode(boolean queueMode) {
        this.queueMode = queueMode;
    }
}
