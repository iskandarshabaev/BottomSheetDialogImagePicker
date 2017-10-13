package io.vape.imagepicker;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;


public class PhotosBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static final int IMAGE_TARGET = 45;
    public static final String SELECTED_IMAGES = "selected_images";
    private View mView;
    private RecyclerView mPhotoList;
    private Button mAttachButton;
    private boolean isAttach;
    private List<String> selectedImages;
    private boolean contentChanged;
    private PhotoListAdapter adapter;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float height = (float) size.y;
            mAttachButton.setY(height - mAttachButton.getHeight() - mAttachButton.getPaddingTop() -
                    mAttachButton.getPaddingBottom() - bottomSheet.getY());
        }
    };

    public static void show(Fragment targetFragment) {
        show(targetFragment, new ArrayList<String>());
    }

    public static void show(Fragment targetFragment, ArrayList<String> paths) {
        PhotosBottomSheetDialogFragment fragment = newInstance(targetFragment, paths);
        fragment.show(targetFragment.getChildFragmentManager(), fragment.getTag());
    }

    public static PhotosBottomSheetDialogFragment newInstance(Fragment targetFragment, ArrayList<String> paths) {
        Bundle args = new Bundle();
        args.putStringArrayList(PhotosBottomSheetDialogFragment.SELECTED_IMAGES, paths);
        PhotosBottomSheetDialogFragment fragment = new PhotosBottomSheetDialogFragment();
        fragment.setArguments(args);
        fragment.setTargetFragment(targetFragment, PhotosBottomSheetDialogFragment.IMAGE_TARGET);
        return fragment;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.fragment_photos_bottom_sheet, null);
        dialog.setContentView(contentView);
        mView = contentView;
        findViews(contentView);
        Bundle args = getArguments();
        selectedImages = args.getStringArrayList(SELECTED_IMAGES);
        initViews();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    private void findViews(View view) {
        mPhotoList = (RecyclerView) view.findViewById(R.id.photo_list);
        mAttachButton = (Button) view.findViewById(R.id.attach);
    }

    private void initViews() {
        adapter = new PhotoListAdapter(
                new ArrayList<ImageItem>(), new ArrayList<ImageItem>());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        mPhotoList.setAdapter(adapter);
        mPhotoList.setLayoutManager(layoutManager);
        mPhotoList.setItemAnimator(animator);
        GridSpacingItemDecoration gridSpacingItemDecoration = new GridSpacingItemDecoration(3,
                getResources().getDimensionPixelSize(R.dimen.margin_medium), true, 0);
        mPhotoList.addItemDecoration(gridSpacingItemDecoration);
        initImagesList();
        mAttachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAttach || contentChanged) {
                    List<ImageItem> items = adapter.getSelectedItems();
                    ArrayList<String> result = new ArrayList<>();
                    for (ImageItem intent : items) {
                        result.add(intent.uri.toString());
                    }
                    Intent intent = getActivity().getIntent();
                    intent.putStringArrayListExtra(SELECTED_IMAGES, result);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                }
                dismiss();
            }
        });
        mAttachButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.windowBackground), PorterDuff.Mode.MULTIPLY);
        mAttachButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryText));
        adapter.setPhotoAdapterDelegate(new PhotoListAdapter.PhotoAdapterDelegate() {
            @SuppressLint("NewApi")
            @Override
            public void onImageClick(ImageItem item) {
                contentChanged = true;
                if (adapter.getSelectedItems().size() == 0) {
                    mAttachButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.windowBackground), PorterDuff.Mode.MULTIPLY);
                    mAttachButton.setText(getString(R.string.cancel));
                    mAttachButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryText));
                    isAttach = false;
                } else {
                    mAttachButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                    mAttachButton.setText(getString(R.string.attach));
                    mAttachButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryTextInverted));
                    isAttach = true;
                    Display display = getActivity().getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    float height = (float) size.y;
                    mAttachButton.setY(height - mAttachButton.getHeight() - mAttachButton.getPaddingTop() -
                            mAttachButton.getPaddingBottom() - ((FrameLayout) mView.getParent()).getY());
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initImagesList() {
        if (!PermissionUtils.checkPermissions(getActivity())) {
            PermissionUtils.requestPermisions(getActivity(), new String[]{READ_EXTERNAL_STORAGE}, 9);
        } else {
            List<Uri> uris = getImages(getContext());
            List<ImageItem> images = new ArrayList<>(uris.size());
            for (Uri uri : uris) {
                images.add(new ImageItem(uri));
            }
            adapter.changeDataSet(images);
            adapter.setSelectedImages(selectedImages);
        }
    }

    public List<Uri> getImages(Context context) {
        List<Uri> paths = new ArrayList<>();
        Cursor cc = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " DESC");
        if (cc != null) {
            cc.moveToFirst();
            for (int i = 0; i < cc.getCount(); i++) {
                cc.moveToPosition(i);
                Uri uri = Uri.fromFile(new File(cc.getString(1)));
                paths.add(uri);
            }
            cc.close();
        }
        return paths;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        int grants = 0;
        if (requestCode == 9) {
            for (int grantResult : grantResults) {
                grants += grantResult;
            }
        }
        if (grants == 0) {
            initImagesList();
        } else {
            for (int index = 0; index < grantResults.length; index++) {
                if (grantResults[index] != PERMISSION_GRANTED) {
                    if (grantResults[index] == PERMISSION_DENIED &&
                            !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[index])) {
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((FrameLayout) mView.getParent()).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                ((FrameLayout) mView.getParent()).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                float height = (float) size.y;
                mAttachButton.setY(height - mAttachButton.getHeight() - mAttachButton.getPaddingTop() -
                        mAttachButton.getPaddingBottom() - ((FrameLayout) mView.getParent()).getY());
            }
        });
    }
}
