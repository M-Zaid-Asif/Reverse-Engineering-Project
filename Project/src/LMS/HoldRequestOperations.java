package LMS;

import java.util.ArrayList;

public class HoldRequestOperations {

    ArrayList<HoldRequest> holdRequests;

    public HoldRequestOperations() {
        holdRequests = new ArrayList<>();
    }

    // Add a hold request
    public void addHoldRequest(HoldRequest hr) {
        holdRequests.add(hr);
    }

    // Remove the earliest hold request
    public void removeHoldRequest() {
        if (!holdRequests.isEmpty()) {
            holdRequests.remove(0);
        }
    }

    // Getter for hold requests
    public ArrayList<HoldRequest> getHoldRequests() {
        return holdRequests;
    }

    // Check if there are any hold requests
    public boolean hasHoldRequests() {
        return !holdRequests.isEmpty();
    }
}
