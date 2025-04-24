package app.user.navigation;

public interface Command {
    /**
     * Previous page.
     */
    void execute();
    /**
     * Next page.
     */
    void undo();
}
