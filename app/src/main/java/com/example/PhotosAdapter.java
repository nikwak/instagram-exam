package com.example;

/**
 * Created by ni on 2017-03-09.
 * com.example.instagram-exam
 */

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotosAdapter extends ArrayAdapter<Photo> {
    public PhotosAdapter(Context context, List<Photo> photos) {
        super(context, android.R.layout.simple_list_item_1, photos);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Photo photo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }

        ImageView imgPhoto = (ImageView) convertView.findViewById(R.id.imgPhoto);

        // 높이 설정
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        imgPhoto.getLayoutParams().height = displayMetrics.widthPixels;

        imgPhoto.setImageResource(0);

        // 이미지 URL을 기반으로 이미지 뷰에 추가할 사진 요청
        // 비트맵을 이미지 뷰에 삽입
        Picasso.with(getContext()).load(photo.imageUrl).placeholder(R.drawable.instagram_glyph_on_white).into(imgPhoto);
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

}
