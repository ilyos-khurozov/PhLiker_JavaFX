package model;

import javafx.scene.image.Image;

//CachedPhoto will be saved in cache with image & image's title

public class CachedPhoto {
    private Image image;
    private String title;

    public CachedPhoto(Image image, String title) {
        this.image = image;
        this.title = title;
    }

    public Image getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }
}
