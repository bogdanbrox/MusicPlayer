package app.user.recommendations;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.user.User;
import app.user.recommendations.Strategy;

import java.util.*;

public class PlaylistStrategy implements Strategy {
    private final int five = 5;
    private final int three = 3;
    private final int two = 2;

    /**
     * Add to HashMap
     *
     * @param hashMap the hashmap
     * @param name the name
     */
    public void addToHashMap(final HashMap<String, Integer> hashMap, final String name) {
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            if (entry.getKey().equals(name)) {
                entry.setValue(entry.getValue() + 1);
                return;
            }
        }
        hashMap.put(name, 1);
    }

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
            if (count == 3) {
                break;
            }
        }

        return sortedMap;
    }
    /**
     * Fans playlist recommendation.
     *
     * @param user the user
     * @return the fans playlist
     */
    @Override
    public LibraryEntry execute(final User user) {
        HashMap<String, Integer> topGenres = new HashMap<>();
        for (Song song : user.getLikedSongs()) {
            addToHashMap(topGenres, song.getGenre());
        }
        for (Playlist playlist : user.getPlaylists()) {
            for (Song song : playlist.getSongs()) {
                addToHashMap(topGenres, song.getGenre());
            }
        }
        for (Playlist playlist : user.getFollowedPlaylists()) {
            for (Song song : playlist.getSongs()) {
                addToHashMap(topGenres, song.getGenre());
            }
        }
        LinkedHashMap<String, Integer> top3Genres = sortHashMapByValue(topGenres);
        Playlist randomPlaylist
                = new Playlist(user.getUsername() + "'s recommendations", user.getUsername());
        int count = 1;
        for (Map.Entry<String, Integer> entry : top3Genres.entrySet()) {
            int maxSongs;
            if (count == 1) {
                maxSongs = five;
            } else if (count == two) {
                maxSongs = three;
            } else if (count == three) {
                maxSongs = two;
            } else {
                break;
            }
            ArrayList<Song> genreSongs = user.getSongsByGenre(entry.getKey());
            int secondCount = 0;
            for (Song song : genreSongs) {
                randomPlaylist.getSongs().add(song);
                secondCount++;
                if (secondCount == maxSongs) {
                    break;
                }
            }
            count++;
        }
        if (randomPlaylist.getSongs().isEmpty()) {
            return null;
        }
        return randomPlaylist;
    }
}
