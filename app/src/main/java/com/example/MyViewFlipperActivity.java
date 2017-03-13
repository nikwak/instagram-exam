package com.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ni on 2017-03-09.
 * com.example.instagram-exam
 */
public class MyViewFlipperActivity extends Activity implements View.OnTouchListener {
    ViewFlipper flipper;
    private ArrayList<Photo> photos;
    private Photo photo;
    // ViewFlipper에 동적으로 child view 추가
    private ImageView iv;

    // 터치 이벤트 발생 지점의 x좌표 저장
    float xAtDown;
    float xAtUp;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        photos = new ArrayList<Photo>(); // 이미지 리스트

        flipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        flipper.setOnTouchListener(this);

        Intent intent = new Intent(this.getIntent());
        String s = intent.getStringExtra("photo_imageUrl");
        String photo_id = intent.getStringExtra("photo_id");
        String mParam = intent.getStringExtra("mParam");



        iv = (ImageView)findViewById(R.id.imageview1);

        if(iv.getParent() != null){
            ((ViewGroup)iv.getParent()).removeView(iv);
        }
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Picasso.with(this).load(s).placeholder(R.drawable.instagram_glyph_on_white).into(iv);
        flipper.addView(iv);

        // 이미지 가져올 url
        String popularUrl = "https://www.instagram.com/"+mParam+"/media/?max_id="+photo_id;

        System.out.println("popularUrl : "+popularUrl);
        // 비동기 네트워크
        AsyncHttpClient client = new AsyncHttpClient();
        // 요청
        client.get(popularUrl, new JsonHttpResponseHandler() {
            // 요청 성공 시
            // json 데이터로 전달
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                parseJson(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });


    }

    private void parseJson(JSONObject response){
        JSONArray photosJSON = null;
        JSONArray commentsJSON = null;
        try {
            photos.clear();
            // json 데이터 파싱
            photosJSON = response.getJSONArray("items");
            for (int i = 0; i < photosJSON.length(); i++) {
                JSONObject photoJSON = photosJSON.getJSONObject(i);
                photo = new Photo();
                photo.profileUrl = photoJSON.getJSONObject("user").getString("profile_picture");
                photo.username = photoJSON.getJSONObject("user").getString("username");

                if (photoJSON.has("caption") && !photoJSON.isNull("caption")) {
                    photo.caption = photoJSON.getJSONObject("caption").getString("text");
                }
                photo.createdTime = photoJSON.getString("created_time");
                photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");

                if (photoJSON.has("comments") && !photoJSON.isNull("comments")) {
                    photo.commentsCount = photoJSON.getJSONObject("comments").getInt("count");
                    commentsJSON = photoJSON.getJSONObject("comments").getJSONArray("data");
                    if (commentsJSON.length() > 0) {
                        photo.comment1 = commentsJSON.getJSONObject(commentsJSON.length() - 1).getString("text");
                        photo.user1 = commentsJSON.getJSONObject(commentsJSON.length() - 1).getJSONObject("from").getString("username");
                        if (commentsJSON.length() > 1) {
                            photo.comment2 = commentsJSON.getJSONObject(commentsJSON.length() - 2).getString("text");
                            photo.user2 = commentsJSON.getJSONObject(commentsJSON.length() - 2).getJSONObject("from").getString("username");
                        }
                    } else {
                        photo.commentsCount = 0;
                    }
                }
                photo.id = photoJSON.getString("id");

                iv = new ImageView(MyViewFlipperActivity.this);//ImageView)findViewById(R.id.imageview1);
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Picasso.with(MyViewFlipperActivity.this).load(photo.imageUrl).placeholder(R.drawable.instagram_glyph_on_white).into(iv);
                flipper.addView(iv);
            }


        } catch (JSONException e ) {
            // json 파싱 에러
            e.printStackTrace();
        }
    }

    // View.OnTouchListener의 abstract method
    // flipper 터지 이벤트 리스너
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 터치 이벤트가 일어난 뷰가 ViewFlipper가 아니면 return
        if (v != flipper)
            return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            xAtDown = event.getX(); // 터치 시작지점 x좌표 저장
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            xAtUp = event.getX(); // 터치 끝난지점 x좌표 저장

            if (xAtUp < xAtDown) {
                // 왼쪽 방향 에니메이션 지정
                flipper.setInAnimation(AnimationUtils.loadAnimation(this,
                        R.anim.push_left_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                        R.anim.push_left_out));

                // 다음 view 보여줌
                flipper.showNext();
            } else if (xAtUp > xAtDown) {
                // 오른쪽 방향 에니메이션 지정
                flipper.setInAnimation(AnimationUtils.loadAnimation(this,
                        R.anim.push_right_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                        R.anim.push_right_out));
                // 전 view 보여줌
                flipper.showPrevious();
            }
        }
        return true;
    }
}