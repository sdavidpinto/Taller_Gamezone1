package model;

public class VideoGame extends Product {

    private String platform;
    private String genre;
    private String ageRating;

    public VideoGame(String identifier, String title, double price, int availableQuantity,
                      String platform, String genre, String ageRating) {
        super(identifier, title, price, availableQuantity);
        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }

    public String getPlatform() { return platform; }
    public String getGenre() { return genre; }
    public String getAgeRating() { return ageRating; }

    @Override
    public String getDescription() {
        return String.format("%s [Video Game] - Platform: %s | Genre: %s | Age rating: %s | Price: $%.2f | Stock: %d",
                getTitle(), platform, genre, ageRating, getPrice(), getAvailableQuantity());
    }
}