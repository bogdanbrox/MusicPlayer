package app.user.wrapped;

import app.user.Artist;
import app.user.Host;
import app.user.User;
import app.user.wrapped.Visitor;

import java.util.*;

public class WrappedVisitor implements Visitor {
    private final int maxCount = 5;

    /**
     * Sort HashMap by Value.
     *
     * @param hashMap the HashMap
     * @return the sorted HashMap
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
        for (Map.Entry<String, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    /**
     * Visit user implementation.
     *
     * @param user the user
     * @return user wrapped
     */
    @Override
    public LinkedHashMap<String, Object> visit(final User user) {
        LinkedHashMap<String, Integer> sortedEpisodes = sortHashMapByValue(user.getTopEpisodes());
        LinkedHashMap<String, Integer> sortedSongs = sortHashMapByValue(user.getTopSongs());
        LinkedHashMap<String, Integer> sortedAlbums = sortHashMapByValue(user.getTopAlbums());
        LinkedHashMap<String, Integer> sortedGenres = sortHashMapByValue(user.getTopGenres());
        LinkedHashMap<String, Integer> sortedArtists = sortHashMapByValue(user.getTopArtists());

        LinkedHashMap<String, Integer> top5Episodes = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> top5Songs = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> top5Albums = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> top5Genres = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> top5Artists = new LinkedHashMap<>();

        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedEpisodes.entrySet()) {
            String episodeName = entry.getKey();
            String[] parts = episodeName.split("_");
            episodeName = parts[0];
            top5Episodes.put(episodeName, entry.getValue());
            count++;
            if (count == maxCount) {
                break;
            }
        }
        count = 0;
        for (Map.Entry<String, Integer> entry : sortedSongs.entrySet()) {
            String songName = entry.getKey();
            String[] parts = songName.split("_");
            songName = parts[0];
            top5Songs.put(songName, entry.getValue());
            count++;
            if (count == maxCount) {
                break;
            }
        }
        count = 0;
        for (Map.Entry<String, Integer> entry : sortedGenres.entrySet()) {
            top5Genres.put(entry.getKey(), entry.getValue());
            count++;
            if (count == maxCount) {
                break;
            }
        }
        count = 0;
        for (Map.Entry<String, Integer> entry : sortedArtists.entrySet()) {
            top5Artists.put(entry.getKey(), entry.getValue());
            count++;
            if (count == maxCount) {
                break;
            }
        }
        count = 0;
        for (Map.Entry<String, Integer> entry : sortedAlbums.entrySet()) {
            top5Albums.put(entry.getKey(), entry.getValue());
            count++;
            if (count == maxCount) {
                break;
            }
        }

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("topArtists", top5Artists);
        result.put("topGenres", top5Genres);
        result.put("topSongs", top5Songs);
        result.put("topAlbums", top5Albums);
        result.put("topEpisodes", top5Episodes);
        return result;
    }

    /**
     * Visit artist implementation.
     *
     * @param artist the artist
     * @return artist wrapped
     */
    @Override
    public LinkedHashMap<String, Object> visit(final Artist artist) {
        LinkedHashMap<String, Integer> sortedAlbums = sortHashMapByValue(artist.getTopAlbums());
        LinkedHashMap<String, Integer> sortedSongs = sortHashMapByValue(artist.getTopSongs());
        LinkedHashMap<String, Integer> sortedFans = sortHashMapByValue(artist.getTopFans());

        ArrayList<String> top5Fans = new ArrayList<>();
        LinkedHashMap<String, Integer> top5Songs = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> top5Albums = new LinkedHashMap<>();

        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedSongs.entrySet()) {
            top5Songs.put(entry.getKey(), entry.getValue());
            count++;
            if (count == maxCount) {
                break;
            }
        }
        count = 0;
        for (Map.Entry<String, Integer> entry : sortedAlbums.entrySet()) {
            top5Albums.put(entry.getKey(), entry.getValue());
            count++;
            if (count == maxCount) {
                break;
            }
        }
        count = 0;
        for (Map.Entry<String, Integer> entry : sortedFans.entrySet()) {
            top5Fans.add(entry.getKey());
            count++;
            if (count == maxCount) {
                break;
            }
        }

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("topAlbums", top5Albums);
        result.put("topSongs", top5Songs);
        result.put("topFans", top5Fans);
        result.put("listeners", sortedFans.size());
        return result;
    }

    /**
     * Visit host implementation.
     *
     * @param host the host
     * @return host wrapped
     */
    @Override
    public LinkedHashMap<String, Object> visit(final Host host) {
        LinkedHashMap<String, Integer> sortedEpisodes = sortHashMapByValue(host.getTopEpisodes());

        LinkedHashMap<String, Integer> top5Episodes = new LinkedHashMap<>();

        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedEpisodes.entrySet()) {
            top5Episodes.put(entry.getKey(), entry.getValue());
            count++;
            if (count == maxCount) {
                break;
            }
        }

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("topEpisodes", top5Episodes);
        result.put("listeners", host.getTopFans().size());
        return result;
    }
}
