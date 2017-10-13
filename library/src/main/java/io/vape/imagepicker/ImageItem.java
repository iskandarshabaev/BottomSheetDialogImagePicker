package io.vape.imagepicker;

import android.net.Uri;

public class ImageItem {

    Uri uri;
    boolean isSelected;

    public ImageItem() {
    }

    public ImageItem(Uri uri) {
        this.uri = uri;
    }
}
