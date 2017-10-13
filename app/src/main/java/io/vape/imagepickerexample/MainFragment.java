package io.vape.imagepickerexample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.vape.imagepicker.ImageLoader;
import io.vape.imagepicker.ImagePicker;
import io.vape.imagepicker.PhotosBottomSheetDialogFragment;

import static io.vape.imagepicker.PhotosBottomSheetDialogFragment.IMAGE_TARGET;
import static io.vape.imagepicker.PhotosBottomSheetDialogFragment.SELECTED_IMAGES;

public class MainFragment extends Fragment {

    private static final int MAX_WIDTH = 512;
    private static final int MAX_HEIGHT = 384;
    static int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Button pickImage = (Button) view.findViewById(R.id.pickImage);
        ImagePicker.setImageLoader(new ImageLoader() {
            @Override
            public void load(Uri uri, ImageView into) {
                Picasso.with(into.getContext())
                        .load(uri)
                        .transform(new ImageSizeTransform(MAX_WIDTH, MAX_HEIGHT))
                        .resize(size, size)
                        .centerInside()
                        .into(into);
            }
        });
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotosBottomSheetDialogFragment.show(MainFragment.this, new ArrayList<String>());
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_TARGET:
                if (resultCode == Activity.RESULT_OK) {
                    List<String> images = data.getStringArrayListExtra(SELECTED_IMAGES);
                    images.size();
                }
                break;
        }
    }
}
