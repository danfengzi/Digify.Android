package digify.tv.api.models;

/**
 * Created by Joel on 12/29/2016.
 */

public class LoginResponseModel {
    private String code;
    private DeviceModel device;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DeviceModel getDevice() {
        return device;
    }

    public void setDevice(DeviceModel device) {
        this.device = device;
    }
}
