package app.user.recommendations;

import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.user.User;
import app.user.recommendations.Strategy;

import java.util.ArrayList;
import java.util.Random;

public class SongStrategy implements Strategy {
    private final int minTime = 30;

    /**
     * Random song
     *
     * @param user the user
     * @return the random song
     */
    @Override
    public LibraryEntry execute(final User user) {
        Song song = (Song) user.getPlayer().getSource().getAudioFile();
        int remainedDuration = user.getPlayer().getSource().getDuration();
        int passedTime = song.getDuration() - remainedDuration;
        if (passedTime < minTime) {
            return null;
        }
        String genre = song.getGenre();
        ArrayList<Song> genreSongs = user.getSongsByGenre(genre);
        Random random = new Random(passedTime);
        return genreSongs.get(random.nextInt(genreSongs.size()));
    }

}
