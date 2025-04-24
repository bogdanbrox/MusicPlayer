package app.user;

import app.Admin;
import app.audio.Collections.AudioCollection;
import app.audio.Collections.Playlist;
import app.audio.Collections.PlaylistOutput;
import app.audio.Collections.Podcast;
import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.pages.HomePage;
import app.pages.LikedContentPage;
import app.pages.Page;
import app.player.Player;
import app.player.PlayerSource;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.searchBar.SearchBar;
import app.user.navigation.Command;
import app.user.navigation.ConcreteCommand;
import app.user.navigation.Invoker;
import app.user.notifications.Observer;
import app.user.recommendations.*;
import app.user.wrapped.Visitor;
import app.utils.Enums;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * The type User.
 */
public final class User extends UserAbstract implements Observer {
    @Getter
    private ArrayList<Playlist> playlists;
    @Getter
    private ArrayList<Song> likedSongs;
    @Getter
    private ArrayList<Playlist> followedPlaylists;
    @Getter
    private final Player player;
    @Getter
    private boolean status;
    private final SearchBar searchBar;
    private boolean lastSearched;
    @Getter
    @Setter
    private Page currentPage;
    @Getter
    @Setter
    private HomePage homePage;
    @Getter
    @Setter
    private LikedContentPage likedContentPage;
    @Getter
    @Setter
    private HashMap<String, Integer> topEpisodes;
    @Getter
    @Setter
    private HashMap<String, Integer> topSongs;
    @Getter
    @Setter
    private HashMap<String, Integer> topAlbums;
    @Getter
    @Setter
    private HashMap<String, Integer> topGenres;
    @Getter
    @Setter
    private HashMap<String, Integer> topArtists;
    @Getter
    @Setter
    private ContentCreator lastSearchedContentCreator;
    @Getter
    @Setter
    private ArrayList<String> boughtMerch;
    @Getter
    @Setter
    private ArrayList<HashMap<String, String>> notifications;
    @Getter
    @Setter
    private boolean premium;
    @Getter
    @Setter
    private HashMap<String, Integer> premiumListenedSongs;
    @Getter
    @Setter
    private HashMap<String, Integer> nonPremiumListenedSongs;
    @Getter
    private final double premiumMoney = 1000000.0;
    @Getter
    @Setter
    private double adMoney;
    @Getter
    @Setter
    private Stack<Page> prevPages;
    @Getter
    @Setter
    private Stack<Page> nextPages;
    @Getter
    @Setter
    private Invoker invoker;
    @Getter
    @Setter
    private ArrayList<Song> songRecommendations;
    @Getter
    @Setter
    private ArrayList<Playlist> playlistRecommendations;
    @Getter
    @Setter
    private Context context;
    @Getter
    @Setter
    private Song lastSongRecommendation;
    @Getter
    @Setter
    private Playlist lastPlaylistRecommendation;

    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public User(final String username, final int age, final String city) {
        super(username, age, city);
        playlists = new ArrayList<>();
        likedSongs = new ArrayList<>();
        songRecommendations = new ArrayList<>();
        playlistRecommendations = new ArrayList<>();
        followedPlaylists = new ArrayList<>();
        player = new Player();
        searchBar = new SearchBar(username);
        lastSearched = false;
        status = true;

        homePage = new HomePage(this);
        currentPage = homePage;
        likedContentPage = new LikedContentPage(this);

        topEpisodes = new HashMap<>();
        topSongs = new HashMap<>();
        topAlbums = new HashMap<>();
        topArtists = new HashMap<>();
        topGenres = new HashMap<>();
        lastSearchedContentCreator = null;
        boughtMerch = new ArrayList<>();
        notifications = new ArrayList<>();
        premium = false;
        premiumListenedSongs = new HashMap<>();
        nonPremiumListenedSongs = new HashMap<>();
        adMoney = -1.0;
        prevPages = new Stack<>();
        nextPages = new Stack<>();
        invoker = new Invoker();
        context = new Context();
        lastSongRecommendation = null;
        lastPlaylistRecommendation = null;
    }

    @Override
    public String userType() {
        return "user";
    }

    /**
     * Search array list.
     *
     * @param filters the filters
     * @param type    the type
     * @return the array list
     */
    public ArrayList<String> search(final Filters filters, final String type) {
        searchBar.clearSelection();
        player.stop();

        lastSearched = true;
        ArrayList<String> results = new ArrayList<>();

        if (type.equals("artist") || type.equals("host")) {
            List<ContentCreator> contentCreatorsEntries =
            searchBar.searchContentCreator(filters, type);

            for (ContentCreator contentCreator : contentCreatorsEntries) {
                results.add(contentCreator.getUsername());
            }
        } else {
            List<LibraryEntry> libraryEntries = searchBar.search(filters, type);

            for (LibraryEntry libraryEntry : libraryEntries) {
                results.add(libraryEntry.getName());
            }
        }
        return results;
    }

    /**
     * Select string.
     *
     * @param itemNumber the item number
     * @return the string
     */
    public String select(final int itemNumber) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (!lastSearched) {
            return "Please conduct a search before making a selection.";
        }

        lastSearched = false;

        if (searchBar.getLastSearchType().equals("artist")
            || searchBar.getLastSearchType().equals("host")) {
            ContentCreator selected = searchBar.selectContentCreator(itemNumber);

            if (selected == null) {
                return "The selected ID is too high.";
            }
            lastSearchedContentCreator = selected;
            currentPage = selected.getPage();
            return "Successfully selected %s's page.".formatted(selected.getUsername());
        } else {
            LibraryEntry selected = searchBar.select(itemNumber);

            if (selected == null) {
                return "The selected ID is too high.";
            }

            return "Successfully selected %s.".formatted(selected.getName());
        }
    }

    /**
     * Load string.
     *
     * @return the string
     */
    public String load() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (searchBar.getLastSelected() == null) {
            return "Please select a source before attempting to load.";
        }

        if (!searchBar.getLastSearchType().equals("song")
            && ((AudioCollection) searchBar.getLastSelected()).getNumberOfTracks() == 0) {
            return "You can't load an empty audio collection!";
        }
        if (adMoney > 0) {
            adMoney = -1.0;
        }
        player.setSource(searchBar.getLastSelected(), searchBar.getLastSearchType());
        searchBar.clearSelection();

        addToStats(player.getSource());

        player.pause();

        return "Playback loaded successfully.";
    }

    /**
     * Play pause string.
     *
     * @return the string
     */
    public String playPause() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to pause or resume playback.";
        }

        player.pause();

        if (player.getPaused()) {
            return "Playback paused successfully.";
        } else {
            return "Playback resumed successfully.";
        }
    }

    /**
     * Repeat string.
     *
     * @return the string
     */
    public String repeat() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before setting the repeat status.";
        }

        Enums.RepeatMode repeatMode = player.repeat();
        String repeatStatus = "";

        switch (repeatMode) {
            case NO_REPEAT -> {
                repeatStatus = "no repeat";
            }
            case REPEAT_ONCE -> {
                repeatStatus = "repeat once";
            }
            case REPEAT_ALL -> {
                repeatStatus = "repeat all";
            }
            case REPEAT_INFINITE -> {
                repeatStatus = "repeat infinite";
            }
            case REPEAT_CURRENT_SONG -> {
                repeatStatus = "repeat current song";
            }
            default -> {
                repeatStatus = "";
            }
        }

        return "Repeat mode changed to %s.".formatted(repeatStatus);
    }

    /**
     * Shuffle string.
     *
     * @param seed the seed
     * @return the string
     */
    public String shuffle(final Integer seed) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before using the shuffle function.";
        }

        if (!player.getType().equals("playlist")
            && !player.getType().equals("album")) {
            return "The loaded source is not a playlist or an album.";
        }

        player.shuffle(seed);

        if (player.getShuffle()) {
            return "Shuffle function activated successfully.";
        }
        return "Shuffle function deactivated successfully.";
    }

    /**
     * Forward string.
     *
     * @return the string
     */
    public String forward() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to forward.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipNext();

        if (player.getCurrentAudioFile().getDuration().equals(player.getSource().getDuration())) {
            addToStats(player.getSource());
        }
        return "Skipped forward successfully.";
    }

    /**
     * Backward string.
     *
     * @return the string
     */
    public String backward() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please select a source before rewinding.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipPrev();

        return "Rewound successfully.";
    }

    /**
     * Like string.
     *
     * @return the string
     */
    public String like() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before liking or unliking.";
        }

        if (!player.getType().equals("song") && !player.getType().equals("playlist")
            && !player.getType().equals("album")) {
            return "Loaded source is not a song.";
        }

        Song song = (Song) player.getCurrentAudioFile();

        if (likedSongs.contains(song)) {
            likedSongs.remove(song);
            song.dislike();

            return "Unlike registered successfully.";
        }

        likedSongs.add(song);
        song.like();
        return "Like registered successfully.";
    }

    /**
     * Next string.
     *
     * @return the string
     */
    public String next() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        player.next();

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        addToStats(player.getSource());

        return "Skipped to next track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Prev string.
     *
     * @return the string
     */
    public String prev() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before returning to the previous track.";
        }

        player.prev();

        addToStats(player.getSource());

        return "Returned to previous track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Create playlist string.
     *
     * @param name      the name
     * @param timestamp the timestamp
     * @return the string
     */
    public String createPlaylist(final String name, final int timestamp) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (playlists.stream().anyMatch(playlist -> playlist.getName().equals(name))) {
            return "A playlist with the same name already exists.";
        }

        playlists.add(new Playlist(name, getUsername(), timestamp));

        return "Playlist created successfully.";
    }

    /**
     * Add remove in playlist string.
     *
     * @param id the id
     * @return the string
     */
    public String addRemoveInPlaylist(final int id) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before adding to or removing from the playlist.";
        }

        if (player.getType().equals("podcast")) {
            return "The loaded source is not a song.";
        }

        if (id > playlists.size()) {
            return "The specified playlist does not exist.";
        }

        Playlist playlist = playlists.get(id - 1);

        if (playlist.containsSong((Song) player.getCurrentAudioFile())) {
            playlist.removeSong((Song) player.getCurrentAudioFile());
            return "Successfully removed from playlist.";
        }

        playlist.addSong((Song) player.getCurrentAudioFile());
        return "Successfully added to playlist.";
    }

    /**
     * Switch playlist visibility string.
     *
     * @param playlistId the playlist id
     * @return the string
     */
    public String switchPlaylistVisibility(final Integer playlistId) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (playlistId > playlists.size()) {
            return "The specified playlist ID is too high.";
        }

        Playlist playlist = playlists.get(playlistId - 1);
        playlist.switchVisibility();

        if (playlist.getVisibility() == Enums.Visibility.PUBLIC) {
            return "Visibility status updated successfully to public.";
        }

        return "Visibility status updated successfully to private.";
    }

    /**
     * Show playlists array list.
     *
     * @return the array list
     */
    public ArrayList<PlaylistOutput> showPlaylists() {
        ArrayList<PlaylistOutput> playlistOutputs = new ArrayList<>();
        for (Playlist playlist : playlists) {
            playlistOutputs.add(new PlaylistOutput(playlist));
        }

        return playlistOutputs;
    }

    /**
     * Follow string.
     *
     * @return the string
     */
    public String follow() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        LibraryEntry selection = searchBar.getLastSelected();
        String type = searchBar.getLastSearchType();

        if (selection == null) {
            return "Please select a source before following or unfollowing.";
        }

        if (!type.equals("playlist")) {
            return "The selected source is not a playlist.";
        }

        Playlist playlist = (Playlist) selection;

        if (playlist.getOwner().equals(getUsername())) {
            return "You cannot follow or unfollow your own playlist.";
        }

        if (followedPlaylists.contains(playlist)) {
            followedPlaylists.remove(playlist);
            playlist.decreaseFollowers();

            return "Playlist unfollowed successfully.";
        }

        followedPlaylists.add(playlist);
        playlist.increaseFollowers();


        return "Playlist followed successfully.";
    }

    /**
     * Gets player stats.
     *
     * @return the player stats
     */
    public PlayerStats getPlayerStats() {
        return player.getStats();
    }

    /**
     * Show preferred songs array list.
     *
     * @return the array list
     */
    public ArrayList<String> showPreferredSongs() {
        ArrayList<String> results = new ArrayList<>();
        for (AudioFile audioFile : likedSongs) {
            results.add(audioFile.getName());
        }

        return results;
    }

    /**
     * Gets preferred genre.
     *
     * @return the preferred genre
     */
    public String getPreferredGenre() {
        String[] genres = {"pop", "rock", "rap"};
        int[] counts = new int[genres.length];
        int mostLikedIndex = -1;
        int mostLikedCount = 0;

        for (Song song : likedSongs) {
            for (int i = 0; i < genres.length; i++) {
                if (song.getGenre().equals(genres[i])) {
                    counts[i]++;
                    if (counts[i] > mostLikedCount) {
                        mostLikedCount = counts[i];
                        mostLikedIndex = i;
                    }
                    break;
                }
            }
        }

        String preferredGenre = mostLikedIndex != -1 ? genres[mostLikedIndex] : "unknown";
        return "This user's preferred genre is %s.".formatted(preferredGenre);
    }

    /**
     * Switch status.
     */
    public void switchStatus() {
        status = !status;
    }

    /**
     * Simulate time.
     *
     * @param time the time
     */
    public void simulateTime(final int time) {
        if (!status) {
            return;
        }

        player.simulatePlayer(time, this);
    }

    /**
     * Add to HashMap.
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
     * Add to user stats.
     *
     * @param source the source
     */
    public void addToStats(final PlayerSource source) {
        if (source.getType().equals(Enums.PlayerSourceType.PODCAST)) {
            Episode episode = (Episode) source.getAudioFile();
            Podcast podcast = (Podcast) source.getAudioCollection();
            String name = episode.getName() + "_" + podcast.getOwner();
            addToHashMap(topEpisodes, name);

            Host host = Admin.getInstance().getHost(podcast.getOwner());
            if (host != null) {
                addToHashMap(host.getTopEpisodes(), episode.getName());
                addToHashMap(host.getTopFans(), this.getUsername());
            }
        } else {
            Song song = (Song) source.getAudioFile();
            String songName = song.getName() + "_" + song.getArtist();
            addToHashMap(topSongs, songName);
            addToHashMap(topAlbums, song.getAlbum());
            addToHashMap(topGenres, song.getGenre());
            addToHashMap(topArtists, song.getArtist());
            if (premium) {
                addToHashMap(premiumListenedSongs, songName);
            } else {
                addToHashMap(nonPremiumListenedSongs, songName);
            }

            Artist artist = Admin.getInstance().getArtist(song.getArtist());
            addToHashMap(artist.getTopSongs(), song.getName());
            addToHashMap(artist.getTopAlbums(), song.getAlbum());
            addToHashMap(artist.getTopFans(), this.getUsername());
        }
    }

    /**
     * User wrapped
     *
     * @param visitor visitor for the design pattern
     * @return the wrapped
     */
    @Override
    public LinkedHashMap<String, Object> wrapped(final Visitor visitor) {
        return visitor.visit(this);
    }

    /**
     * Check if user wrapped has no stats
     *
     * @return the message
     */
    @Override
    public String emptyWrapped() {
        if (topEpisodes.isEmpty() && topSongs.isEmpty()) {
            return "No data to show for user "
                    + getUsername() + ".";
        }
        return "Has data.";
    }

    /**
     * Add merch.
     *
     * @param name the merch name
     */
    public void addMerch(final String name) {
        boughtMerch.add(name);
    }

    /**
     * Notify subscribers
     *
     * @param notification the notification
     */
    @Override
    public void notify(final HashMap<String, String> notification) {
        notifications.add(notification);
    }

    /**
     * Number of songs.
     *
     * @param hashMap the hashmap
     * @return the number
     */
    public int numberOfSongs(final HashMap<String, Integer> hashMap) {
        int number = 0;
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            number = number + entry.getValue();
        }
        return number;
    }

    public void adBreak(final double price) {
        if (!isPremium()) {
            adMoney = price;
        }
    }

    /**
     * Clear next pages stack if the page was changed with Change page
     *
     * @param page the old page
     */
    public void changedPage(final Page page) {
        prevPages.add(page);
        nextPages.clear();
    }

    /**
     * Previous page.
     */
    public void previousPage() {
        Command command = new ConcreteCommand(this);
        invoker.edit(command);
    }

    /**
     * Next page.
     */
    public void nextPage() {
        invoker.undo();
    }

    /**
     * Update recommendations.
     *
     * @param recommendationType the recommendation type
     * @return the message
     */
    public String updateRecommendations(final String recommendationType) {
        if (recommendationType.equals("random_song")) {
            Strategy songOption = new SongStrategy();
            context.setStrategy(songOption);
            Song randomSong = (Song) context.generateRecommendation(this);
            if (randomSong == null) {
                return "No new recommendations were found";
            }
            songRecommendations.add(randomSong);
            lastSongRecommendation = randomSong;
            lastPlaylistRecommendation = null;
            return "The recommendations for user "
                    + getUsername() + " have been updated successfully.";
        }
        if (recommendationType.equals("random_playlist")) {
            Strategy playlistOption = new PlaylistStrategy();
            context.setStrategy(playlistOption);
            Playlist randomPlaylist = (Playlist) context.generateRecommendation(this);
            if (randomPlaylist == null) {
                return "No new recommendations were found";
            }
            playlistRecommendations.add(randomPlaylist);
            lastPlaylistRecommendation = randomPlaylist;
            lastSongRecommendation = null;
            return "The recommendations for user " + getUsername()
                    + " have been updated successfully.";
        }
        Strategy fansPlaylistOption = new FansPlaylistStrategy();
        context.setStrategy(fansPlaylistOption);
        Playlist fansPlaylist = (Playlist) context.generateRecommendation(this);
        if (fansPlaylist == null) {
            return "No new recommendations were found";
        }
        playlistRecommendations.add(fansPlaylist);
        lastPlaylistRecommendation = fansPlaylist;
        lastSongRecommendation = null;
        return "The recommendations for user " + getUsername()
                + " have been updated successfully.";
    }

    /**
     * Get songs by genre.
     *
     * @param genre the genre
     * @return the songs
     */
    public ArrayList<Song> getSongsByGenre(final String genre) {
        ArrayList<Song> genreSongs = new ArrayList<>();
        for (Song song : Admin.getInstance().getSongs()) {
            if (song.getGenre().equals(genre)) {
                genreSongs.add(song);
            }
        }
        return genreSongs;
    }

    /**
     * Load string.
     *
     * @return the string
     */
    public String loadRecommendations() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (lastSongRecommendation == null
        && lastPlaylistRecommendation == null) {
            return "No recommendations available.";
        }
        if (lastPlaylistRecommendation == null) {
            player.setSource(lastSongRecommendation, "song");

            addToStats(player.getSource());

            player.pause();

            return "Playback loaded successfully.";
        }
        if (adMoney > 0) {
            adMoney = -1.0;
        }
        player.setSource(lastPlaylistRecommendation, "playlist");

        addToStats(player.getSource());

        player.pause();

        return "Playback loaded successfully.";
    }
    /**
     * Method to monetize artists when user is nonpremium.
     */
    public void monetizeNonPremium() {
        double songValue = adMoney / numberOfSongs(getNonPremiumListenedSongs());
        for (Map.Entry<String, Integer> entry : nonPremiumListenedSongs.entrySet()) {
            String name = entry.getKey();
            String[] parts = name.split("_");
            Artist artist = Admin.getInstance().getArtist(parts[1]);
            double value = songValue * entry.getValue();
            artist.addToEachSongRevenue(parts[0], value);
        }
        getNonPremiumListenedSongs().clear();
        adMoney = -1.0;
    }
}
