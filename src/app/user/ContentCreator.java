package app.user;

import app.pages.Page;
import app.user.notifications.Observer;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The type Content creator.
 */
public abstract class ContentCreator extends UserAbstract {
    private String description;
    private Page page;
    @Getter
    @Setter
    private ArrayList<Observer> subscribers;

    /**
     * Instantiates a new Content creator.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public ContentCreator(final String username, final int age, final String city) {
        super(username, age, city);
        subscribers = new ArrayList<>();
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets page.
     *
     * @return the page
     */
    public Page getPage() {
        return page;
    }

    /**
     * Sets page.
     *
     * @param page the page
     */
    public void setPage(final Page page) {
        this.page = page;
    }

    /**
     * Subscribe.
     *
     * @param user the user
     * @return the message
     */
    public String subscribe(final Observer user) {
        if (subscribers.contains(user)) {
            subscribers.remove(user);
            return user.getUsername() + " unsubscribed from " + getUsername() + " successfully.";
        }
        subscribers.add(user);
        return user.getUsername() + " subscribed to " + getUsername() + " successfully.";
    }

    /**
     * Notify subscribers.
     *
     * @param name the name
     * @param description the description
     */
    public void notifySubs(final String name, final String description) {
        for (Observer user : subscribers) {
            HashMap<String, String> notification = new HashMap<>();
            notification.put("name", name);
            notification.put("description", description);
            user.notify(notification);
        }
    }
}
