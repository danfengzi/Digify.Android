package digify.tv.api.models;

import java.util.Date;

/**
 * Created by Joel on 1/24/2017.
 */

public class UserDeviceModel {
    private int deviceId;
    private Date createdAt;
    private Date updatedAt;
    private String name;
    private int userId;
    private String deviceIdString;

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
}
