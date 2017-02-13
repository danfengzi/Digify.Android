package digify.tv.api.models;

import java.util.Date;

/**
 * Created by Joel on 2/13/2017.
 */

public class DeviceInfoModel {
    private String deviceId;
    private Date createdAt;
    private Date updatedAt;
    private String name;
    private int userId;
    private String deviceIdString;
    private String mode;
    private String portraitLogo;
    private String portraitMessage;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
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
}
