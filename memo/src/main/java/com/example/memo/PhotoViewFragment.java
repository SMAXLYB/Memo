package com.example.memo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.memo.databinding.DialogPhotoBinding;


public class PhotoViewFragment extends DialogFragment {
    private static final String ARG_PATH = "path";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DialogPhotoBinding photoBinding = DialogPhotoBinding.inflate(LayoutInflater.from(getActivity()));
        String path = getArguments().getString(ARG_PATH);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        photoBinding.crimeImage.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setTitle("查看大图")
                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setView(photoBinding.getRoot())
                .create();
    }

    public static PhotoViewFragment newIntent(String path) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PATH, path);
        PhotoViewFragment fragment = new PhotoViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
