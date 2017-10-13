package io.vape.imagepicker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoViewHolder> implements View.OnClickListener {

    private List<ImageItem> mPaths;
    private PhotoAdapterDelegate mPhotoAdapterDelegate;
    private List<ImageItem> mSelectedItems;
    private boolean onBind;

    public PhotoListAdapter(List<ImageItem> paths, List<ImageItem> selectedItems) {
        mPaths = paths;
        mSelectedItems = selectedItems;
    }

    public void setSelectedImages(List<String> images) {
        for (ImageItem item : mPaths) {
            for (String i : images) {
                if (item.uri.getPath().equals(i.replace("file://", ""))) {
                    item.isSelected = true;
                    mSelectedItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setPhotoAdapterDelegate(PhotoAdapterDelegate photoAdapterDelegate) {
        mPhotoAdapterDelegate = photoAdapterDelegate;
    }

    public void changeDataSet(List<ImageItem> items) {
        mPaths.clear();
        mPaths.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        onBind = true;
        ImageItem item = mPaths.get(position);
        holder.bind(item);
        holder.rootView.setTag(item);
        holder.rootView.setOnClickListener(this);
        onBind = false;
    }

    @Override
    public void onClick(View v) {
        handleCheck(v);
    }

    private void handleCheck(View v) {
        if (mPhotoAdapterDelegate != null) {
            ImageItem item = (ImageItem) v.getTag();
            int index = mPaths.indexOf(item);
            item.isSelected = !item.isSelected;
            if (item.isSelected) {
                mSelectedItems.add(item);
            } else {
                mSelectedItems.remove(item);
            }
            mPhotoAdapterDelegate.onImageClick(item);
            notifyItemChanged(index);
        }
    }

    public List<ImageItem> getSelectedItems() {
        return mSelectedItems;
    }

    @Override
    public int getItemCount() {
        return mPaths.size();
    }

    public interface PhotoAdapterDelegate {

        void onImageClick(ImageItem item);

    }
}
