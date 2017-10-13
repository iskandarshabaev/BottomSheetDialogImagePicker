package io.vape.imagepicker;

public class ImagePicker {

    public static ImageLoader imageLoader = new DefaultImageLoader();

    public static void setImageLoader(ImageLoader loader) {
        imageLoader = loader;
    }

}
