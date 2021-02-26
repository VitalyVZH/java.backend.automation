package ru.vitalyvzh;

public enum Images {

    POSITIVE_URL("https://bit.ua/wp-content/uploads/2018/07/Upside-Down_Face_Emoji_4dbbbd80-eb60-4c91-9642-83368692e361_large.png"),
    POSITIVE("smile.jpg"),
    NEGATIVE("image.jpg"),
    BIG_SIZE("big.tif"),
    SMALL_SIZE("small.gif");

    public final String path;

    Images(String path) {
        this.path = path;
    }
}
