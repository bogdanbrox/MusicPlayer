package app.user.notifications;

import java.util.HashMap;

public interface Observer {
    /**
     * Get username.
     *
     * @return the username
     */
    String getUsername();

    /**
     * Notify subscribers.
     *
     * @param notification the notification
     */
    void notify(HashMap<String, String> notification);
}
