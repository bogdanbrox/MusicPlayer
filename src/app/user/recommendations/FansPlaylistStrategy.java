package app.user.recommendations;

import app.Admin;
import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.user.Artist;
import app.user.User;
import app.user.recommendations.Strategy;

import java.util.*;

public class FansPlaylistStrategy implements Strategy {
    private final int count = 5;
    /**
     * Sort HashMap by value.
     *
     * @param hashMap the hashmap
     * @return the sorted hashmap
     */
    public static LinkedHashMap<String, Integer> sortHashMapByValue(final HashMap<String, Integer>
                                                                            hashMap) {
        List<Map.Entry<String, Integer>> entryList = new LinkedList<>(hashMap.entrySet());
        Comparator<Map.Entry<String, Integer>> valueComparator
                = new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(final Map.Entry<String, Integer> entry1,
                               final Map.Entry<String, Integer> entry2) {
                int compare = entry2.getValue().compareTo(entry1.getValue());
                if (compare == 0) {
                    return entry1.getKey().compareTo(entry2.getKey());
                } else {
                    return compare;
                }
            }
        };
        Collections.sort(entryList, valueComparator);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        int count = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
            count ++;
            if (count == 5) {
                break;
            }
        }

        return sortedMap;
    }
    /**
     * Random playlist.
     *
     * @param user the user
     * @return the playlist
     */
    @Override
    public LibraryEntry execute(final User user) {
        Song sourceSong = (Song) user.getPlayer().getSource().getAudioFile();
        Artist artist = Admin.getInstance().getArtist(sourceSong.getArtist());
        HashMap<String, Integer> topFans = artist.getTopFans();
        LinkedHashMap<String, Integer> top5fans = sortHashMapByValue(topFans);
        Playlist fansPlaylist = new Playlist(artist.getUsername()
                + " Fan Club recommendations", user.getUsername());
        for (Map.Entry<String, Integer> entry : top5fans.entrySet()) {
            User fan = Admin.getInstance().getUser(entry.getKey());
            int added = 0;
            for (Song song : fan.getLikedSongs()) {
                if (fansPlaylist.containsSong(song)) {
                    continue;
                }
                fansPlaylist.getSongs().add(song);
                added++;
                if (added == count) {
                    break;
                }
            }
        }
        if (fansPlaylist.getSongs().isEmpty()) {
            return null;
        }
        return fansPlaylist;
    }
}
