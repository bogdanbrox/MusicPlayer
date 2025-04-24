package app.user.navigation;

import app.pages.Page;
import app.user.User;
import app.user.navigation.Command;

public class ConcreteCommand implements Command {
    private final User user;
    public ConcreteCommand(final User user) {
        this.user = user;
    }

    /**
     * Previous page.
     */
    @Override
    public void execute() {
        if (user.getPrevPages().isEmpty()) {
            return;
        }
        Page newPage = user.getPrevPages().pop();
        user.getNextPages().push(user.getCurrentPage());
        user.setCurrentPage(newPage);
    }

    /**
     * Next page.
     */
    @Override
    public void undo() {
        if (user.getNextPages().isEmpty()) {
            return;
        }
        Page newPage = user.getNextPages().pop();
        user.getPrevPages().push(user.getCurrentPage());
        user.setCurrentPage(newPage);
    }
}
