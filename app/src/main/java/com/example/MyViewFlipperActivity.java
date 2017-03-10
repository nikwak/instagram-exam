package com.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;

/**
 * Created by ni on 2017-03-09.
 * com.example.instagram-exam
 */
public class MyViewFlipperActivity extends Activity implements View.OnTouchListener,
        CompoundButton.OnCheckedChangeListener {
    ViewFlipper flipper;

    // 터치 이벤트 발생 지점의 x좌표 저장
    float xAtDown;
    float xAtUp;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        flipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        flipper.setOnTouchListener(this);


        Intent intent = new Intent(this.getIntent());
        String s = intent.getStringExtra("photo_id");

        // ViewFlipper에 동적으로 child view 추가
        ImageView iv = new ImageView(this);
        flipper.addView(iv);
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

    // CompoundButton.OnCheckedChangeListener의 abstract method
    // 책크박스 책크 이벤트 리스너
    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {

        Log.w("checked", Boolean.toString(isChecked));

        if (isChecked == true) {
            // 왼쪽 에니메이션 설정
            flipper.setInAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.push_left_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.push_left_out));

            // 자동 Flipping 시작 (간격 3초)
            flipper.setFlipInterval(3000);
            flipper.startFlipping();
        } else {
            // 자동 Flipping 해지
            flipper.stopFlipping();
        }
    }
}