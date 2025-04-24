package app.user;

import java.util.*;

import app.audio.Collections.Album;
import app.audio.Collections.AlbumOutput;
import app.audio.Files.Song;
import app.pages.ArtistPage;
import app.user.wrapped.Visitor;
import lombok.Getter;
import lombok.Setter;

/**
 * The type Artist.
 */
public final class Artist extends ContentCreator {
    private ArrayList<Album> albums;
    private ArrayList<Merchandise> merch;
    private ArrayList<Event> events;
    @Getter
    @Setter
    private HashMap<String, Integer> topSongs;
    @Getter
    @Setter
    private HashMap<String, Integer> topAlbums;
    @Getter
    @Setter
    private HashMap<String, Integer> topFans;
    @Getter
    @Setter
    private double songRevenue;
    @Getter
    @Setter
    private double merchRevenue;
    private String mostProfitableSong;
    @Getter
    @Setter
    private HashMap<String, Double> eachSongRevenue;

    /**
     * Instantiates a new Artist.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public Artist(final String username, final int age, final String city) {
        super(username, age, city);
        albums = new ArrayList<>();
        merch = new ArrayList<>();
        events = new ArrayList<>();

        super.setPage(new ArtistPage(this));

        topSongs = new HashMap<>();
        topAlbums = new HashMap<>();
        topFans = new HashMap<>();
        songRevenue = 0.0;
        merchRevenue = 0.0;
        mostProfitableSong = "N/A";
        eachSongRevenue = new HashMap<>();
    }

    /**
     * Gets albums.
     *
     * @return the albums
     */
    public ArrayList<Album> getAlbums() {
        return albums;
    }

    /**
     * Gets merch.
     *
     * @return the merch
     */
    public ArrayList<Merchandise> getMerch() {
        return merch;
    }

    /**
     * Gets events.
     *
     * @return the events
     */
    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * Gets event.
     *
     * @param eventName the event name
     * @return the event
     */
    public Event getEvent(final String eventName) {
        for (Event event : events) {
            if (event.getName().equals(eventName)) {
                return event;
            }
        }

        return null;
    }

    /**
     * Gets album.
     *
     * @param albumName the album name
     * @return the album
     */
    public Album getAlbum(final String albumName) {
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }

        return null;
    }

    /**
     * Gets all songs.
     *
     * @return the all songs
     */
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        albums.forEach(album -> songs.addAll(album.getSongs()));

        return songs;
    }

    /**
     * Show albums array list.
     *
     * @return the array list
     */
    public ArrayList<AlbumOutput> showAlbums() {
        ArrayList<AlbumOutput> albumOutput = new ArrayList<>();
        for (Album album : albums) {
            albumOutput.add(new AlbumOutput(album));
        }

        return albumOutput;
    }

    /**
     * Get user type
     *
     * @return user type string
     */
    public String userType() {
        return "artist";
    }

    /**
     * Artist wrapped
     *
     * @param visitor visitor for the design pattern
     * @return the wrapped
     */
    @Override
    public LinkedHashMap<String, Object> wrapped(final Visitor visitor) {
        return visitor.visit(this);
    }

    /**
     * Check if artist wrapped has no stats.
     *
     * @return the message
     */
    @Override
    public String emptyWrapped() {
        if (topSongs.isEmpty()) {
            return "No data to show for artist "
                    + getUsername() + ".";
        }
        return "Has data.";
    }

    /**
     * Get total revenue.
     *
     * @return total revenue
     */
    public double getTotalRevenue() {
        return songRevenue + merchRevenue;
    }

    /**
     * Add to merch revenue.
     *
     * @param sum the sum
     */
    public void addToMerchRevenue(final double sum) {
        merchRevenue = merchRevenue + sum;
    }

    /**
     * Add to song revenue.
     *
     * @param sum the sum
     */
    public void addToSongRevenue(final double sum) {
        songRevenue = songRevenue + sum;
    }

    /**
     * Find merchandise by name.
     *
     * @param name the name
     * @return the merchandise
     */
    public Merchandise findMerchByName(final String name) {
        for (Merchandise merchandise : merch) {
            if (merchandise.getName().equals(name)) {
                return merchandise;
            }
        }
        return null;
    }

    /**
     * Add to specific song's revenue
     *
     * @param name the song name
     * @param value the amount
     */
    public void addToEachSongRevenue(final String name, final double value) {
        for (Map.Entry<String, Double> entry : eachSongRevenue.entrySet()) {
            if (entry.getKey().equals(name)) {
                entry.setValue(entry.getValue() + value);
                addToSongRevenue(value);
                return;
            }
        }
        eachSongRevenue.put(name, value);
        addToSongRevenue(value);
    }

    /**
     * Find most profitable song.
     *
     * @return the most profitable song
     */
    public String findMostProfitableSong() {
        double maxAmount = 0.0;
        for (Map.Entry<String, Double> entry : eachSongRevenue.entrySet()) {
            if (entry.getValue() > maxAmount) {
                maxAmount = entry.getValue();
                mostProfitableSong = entry.getKey();
            } if (entry.getValue() == maxAmount
                    && entry.getKey().compareTo(mostProfitableSong) < 0) {
                mostProfitableSong = entry.getKey();
            }
        }
        return mostProfitableSong;
    }
}
