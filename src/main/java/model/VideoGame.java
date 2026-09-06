package model;

/**
 * Represents a video game product in the inventory.
 * Extends the base Product class with game-specific attributes.
 */
public class VideoGame extends Product {

    private String platform;
    private String genre;
    private String ageRating;

    /**
     * Constructs a new VideoGame with the specified details.
     *
     * @param identifier The unique identifier of the video game.
     * @param title The title of the video game.
     * @param price The price of the video game.
     * @param availableQuantity The stock quantity available.
     * @param platform The platform the game is played on (e.g., PC, PlayStation).
     * @param genre The genre of the video game (e.g., Action, RPG).
     * @param ageRating The age rating of the video game (e.g., E, T, M).
     */
    public VideoGame(String identifier, String title, double price, int availableQuantity,
                     String platform, String genre, String ageRating) {
        super(identifier, title, price, availableQuantity);
        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }

    /**
     * Gets the platform of the video game.
     * 
     * @return The video game platform.
     */
    public String getPlatform() { return platform; }

    /**
     * Gets the genre of the video game.
     * 
     * @return The video game genre.
     */
    public String getGenre() { return genre; }

    /**
     * Gets the age rating of the video game.
     * 
     * @return The video game age rating.
     */
    public String getAgeRating() { return ageRating; }

    /**
     * Generates a formatted string containing the video game's specific details.
     * 
     * @return A string representation of the video game description.
     */
    @Override
    public String getDescription() {
        return String.format("%s [VideoGame] - Platform: %s | Genre: %s | Rating: %s | Price: $%.2f | Stock: %d",
                getTitle(), platform, genre, ageRating, getPrice(), getAvailableQuantity());
    }
}