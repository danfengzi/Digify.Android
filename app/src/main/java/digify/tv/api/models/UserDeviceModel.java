package digify.tv.api.models;

import java.util.Date;

/**
 * Created by Joel on 1/24/2017.
 */

public class UserDeviceModel {
    private String deviceId;
    private Date createdAt;
    private Date updatedAt;
    private String name;
    private int userId;
    private String deviceIdString;
    private String tenant;
    private String tenantUrl;


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

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getTenantUrl() {
        return tenantUrl;
    }

    public void setTenantUrl(String tenantUrl) {
        this.tenantUrl = tenantUrl;
    }


}
