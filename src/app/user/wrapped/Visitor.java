package app.user.wrapped;

import app.user.Artist;
import app.user.Host;
import app.user.User;

import java.util.LinkedHashMap;

public interface Visitor {
    /**
     * Visit user.
     *
     * @param user the user
     * @return user wrapped
     */
    LinkedHashMap<String, Object> visit(User user);

    /**
     * Visit artist.
     *
     * @param artist the artist
     * @return artist wrapped
     */
    LinkedHashMap<String, Object> visit(Artist artist);

    /**
     * Visit host.
     *
     * @param host the host
     * @return host wrapped
     */
    LinkedHashMap<String, Object> visit(Host host);
}
