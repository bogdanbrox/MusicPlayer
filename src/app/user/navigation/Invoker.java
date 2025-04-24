package app.user.navigation;

import java.util.Stack;

public class Invoker {
    private Stack<Command> history = new Stack<>();

    /**
     * Previous page method.
     *
     * @param command the command
     */
    public void edit(final Command command) {
        history.push(command);
        command.execute();
    }

    /**
     * Next page method.
     */
    public void undo() {
        if (history.isEmpty()) {
            return;
        }

        Command command = history.pop();
        if (command != null) {
            command.undo();
        }
    }
}
