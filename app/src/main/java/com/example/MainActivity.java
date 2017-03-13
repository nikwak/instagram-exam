package com.example;

/**
 * Created by ni on 2017-03-09.
 * com.example.instagram-exam
 */

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    private ArrayList<Photo> photos;
    private PhotosAdapter aPhotos;
    private SwipeRefreshLayout swipeContainer;
    private EditText searchText;
    private Button searchBtn;
    private String searchParam;
    private ListView lvPhotos;
    private String searchTextFinal;
    private Photo photo;
    private boolean lockListView;
    private String photoID;
    private String more_available;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        searchText  = (EditText) findViewById(R.id.searchText);
        searchBtn  = (Button) findViewById(R.id.searchBtn);
        photos = new ArrayList<Photo>(); // 이미지 리스트
        // 리스트 바인딩
        aPhotos = new PhotosAdapter(this, photos);

        // 이미지 리스트뷰
        lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        lvPhotos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lvPhotos.setOnScrollListener(new EndlessScrollListener());

        lvPhotos.setAdapter(aPhotos);

        // 새로고침 리스너
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchParam = searchText.getText().toString();
                if("true".equals(more_available))
                    fetchPopularPhotos(searchParam, "");
            }
        });

        // 리프레시 config color 설정
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // 검색 버튼
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchParam = searchText.getText().toString();
                fetchPopularPhotos(searchParam ,"");
            }
        });


    }

    private void fetchPopularPhotos(String searchTxt, String maxId) {
        if("".equals(searchTxt)) {
            searchTxt = "design";
        }


        if("".equals(maxId)){
            maxId = "0";
        }
        aPhotos.setSearchText(searchTxt);

        searchTextFinal = searchTxt;
        // 이미지 가져올 url
        String popularUrl = "https://www.instagram.com/"+searchTxt+"/media/?max_id="+maxId;
        // 비동기 네트워크
        AsyncHttpClient client = new AsyncHttpClient();

        // 요청
        client.get(popularUrl, new JsonHttpResponseHandler() {
            // 요청 성공 시
            // json 데이터로 전달
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                parseJson(response);

                swipeContainer.setRefreshing(false);

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
                photos.add(photo);
            }
            more_available = response.getString("more_available");
            // 리스트 변경사항
            aPhotos.notifyDataSetChanged();


        } catch (JSONException e ) {
            // json 파싱 에러
            e.printStackTrace();
        }
    }


    // Endless Scroll
    public class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;
        private int currentPos = 0;

        public EndlessScrollListener() {
        }
        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
            lockListView = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤
            //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
            if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lockListView) {
                if("true".equals(more_available))
                    fetchPopularPhotos(searchTextFinal, photo.id);
            }
        }
    }
}
