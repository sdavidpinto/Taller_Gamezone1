package model;

/**
 * Representa un producto de tipo videojuego.
 * Extiende la clase Product para incluir atributos específicos de videojuegos.
 */
public class VideoGame extends Product {

    private String platform;
    private String genre;
    private String ageRating;

    /**
     * Construye una nueva instancia de VideoGame con los detalles especificados.
     *
     * @param identifier El identificador único del videojuego.
     * @param title El título del videojuego.
     * @param price El precio del videojuego.
     * @param availableQuantity La cantidad disponible en inventario.
     * @param platform La plataforma en la que se ejecuta el videojuego.
     * @param genre El género del videojuego.
     * @param ageRating La clasificación por edad del videojuego.
     */
    public VideoGame(String identifier, String title, double price, int availableQuantity,
                     String platform, String genre, String ageRating) {
        super(identifier, title, price, availableQuantity);
        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }

    /**
     * Obtiene la plataforma del videojuego.
     * 
     * @return La plataforma del videojuego.
     */
    public String getPlatform() { return platform; }

    /**
     * Obtiene el género del videojuego.
     * 
     * @return El género del videojuego.
     */
    public String getGenre() { return genre; }

    /**
     * Obtiene la clasificación por edad del videojuego.
     * 
     * @return La clasificación por edad.
     */
    public String getAgeRating() { return ageRating; }

    /**
     * Genera una descripción formateada con los detalles específicos del videojuego.
     * 
     * @return Una cadena de texto formateada con los datos del videojuego.
     */
    @Override
    public String getDescription() {
        return String.format("%s [VideoGame] - Platform: %s | Genre: %s | Rating: %s | Price: $%.2f | Stock: %d",
                getTitle(), platform, genre, ageRating, getPrice(), getAvailableQuantity());
    }
}