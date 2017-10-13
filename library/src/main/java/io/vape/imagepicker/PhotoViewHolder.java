package io.vape.imagepicker;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

public class PhotoViewHolder extends RecyclerView.ViewHolder {

    View rootView;
    ImageView photo;
    RadioButton isSelected;

    public PhotoViewHolder(View view) {
        super(view);
        this.rootView = view;
        this.photo = (ImageView) view.findViewById(R.id.photo);
        this.isSelected = (RadioButton) view.findViewById(R.id.is_selected);
    }

    public void bind(ImageItem item) {
        ImagePicker.imageLoader.load(item.uri, photo);
        isSelected.setChecked(item.isSelected);
        if (item.isSelected) {
            photo.setColorFilter(ContextCompat.getColor(photo.getContext(), R.color.colorHighlight));
        } else {
            photo.setColorFilter(ContextCompat.getColor(photo.getContext(), R.color.transparent));
        }
    }

}
