package io.vape.imagepicker;

import android.net.Uri;
import android.widget.ImageView;

public interface ImageLoader {

    void load(Uri uri, ImageView into);

}
