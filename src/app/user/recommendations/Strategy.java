package app.user.recommendations;

import app.audio.LibraryEntry;
import app.user.User;

public interface Strategy {
    /**
     * Give user a recommendation
     *
     * @param user the user
     * @return recommendation
     */
    LibraryEntry execute(User user);
}
