package app.user.recommendations;

import app.audio.LibraryEntry;
import app.user.User;
import app.user.recommendations.Strategy;
import lombok.Setter;

public class Context {
    @Setter
    private Strategy strategy;
    public Context() {
        this.strategy = null;
    }
    public Context(final Strategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Generate Recommendation.
     *
     * @param user the user
     * @return the recommendation
     */
    public LibraryEntry generateRecommendation(final User user) {
        return strategy.execute(user);
    }
}
