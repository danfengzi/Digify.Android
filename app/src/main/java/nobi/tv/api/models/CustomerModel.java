package nobi.tv.api.models;

import com.google.firebase.database.Exclude;

import org.joda.time.DateTime;

/**
 * Created by Joel on 4/22/2017.
 */

public class CustomerModel {

    private String firstName;
    private String customerId;
    private String customerKey;
    private String lastName;
    private String tenant;
    private String time;
    @Exclude
    private DateTime addedAt;
    private String enteredBy;
    private int position;
    private Boolean serving;

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Boolean getServing() {
        return serving;
    }

    public void setServing(Boolean serving) {
        this.serving = serving;
    }

    public DateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(DateTime addedAt) {
        this.addedAt = addedAt;
    }
}


