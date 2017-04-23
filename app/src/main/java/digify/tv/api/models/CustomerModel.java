package digify.tv.api.models;

import com.google.firebase.database.Exclude;

import java.util.Date;

/**
 * Created by Joel on 4/22/2017.
 */

public class CustomerModel {

    public enum QueueStatus {
        Waiting, Serving, Served
    }

    private String firstName;
    private String lastName;
    private String tenant;
    private Date time;
    private String enteredBy;
    private int position;
    private String status;
    private QueueStatus queueStatus;

    @Exclude
    public QueueStatus getQueueStatusAsEnum() {
        return queueStatus;
    }

    public String getQueueStatus() {
        if (queueStatus == null) {
            return null;
        } else {
            return queueStatus.name();
        }
    }

    public void setQueueStatus(String queueStatusString) {
        if (queueStatusString == null) {
            queueStatus = null;
        } else {
            this.queueStatus = QueueStatus.valueOf(queueStatusString);
        }
    }

}


