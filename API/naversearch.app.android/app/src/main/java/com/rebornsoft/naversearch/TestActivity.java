package com.rebornsoft.naversearch;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    BufferedReader br;
    StringBuilder searchResult;

    EditText et_movie;
    Button btn_search;
    RecyclerView recyclerView;
    public static MovieAdapters mAdapter;

//    ArrayList<Movie> movieList = new ArrayList<>();//컬렉션 프레임워크 객체 초기화 후에 사용가능
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        searchNaver("살인");

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);



        et_movie = findViewById(R.id.et_movie);
        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String movie_search = et_movie.getText().toString();
                    searchNaver(movie_search);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    public void searchNaver(final String searchObject) { // 검색어 = searchObject로 ;
        final String clientId = "DGr4VNoEFVmhuXfD5uBT";//애플리케이션 클라이언트 아이디값";
        final String clientSecret = "tNLwkJ1dks";//애플리케이션 클라이언트 시크릿값";
        final int display = 10; // 보여지는 검색결과의 수

        // 네트워크 연결은 Thread 생성 필요

        new Thread() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                try {
                    String text = URLEncoder.encode(searchObject, "UTF-8");
                    String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text + "&display=" + display + "&"; // 영화 정보 json 결과
//                                        String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text + "&display=" + display + "&"; // 블로그 정보 json 결과
                    // Json 형태로 결과값을 받아옴.
                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-Naver-Client-Id", clientId);
                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                    con.connect();

                    int responseCode = con.getResponseCode();


                    if(responseCode==200) { // 정상 호출
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    } else {  // 에러 발생
                        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }

                    searchResult = new StringBuilder();
                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        searchResult.append(inputLine + "\n");

                    }
                    br.close();
                    con.disconnect();


                    String data = searchResult.toString();
                    String[] array;
                    array = data.split("\"");
                    Log.d(TAG,"arrayData : "+array.toString());
                    String[] title = new String[display];
//                    String[] link = new String[display]; // URL
                    String[] image = new String[display];
//                    String[] subtitle = new String[display];
                    String[] director = new String[display];
                    String[] actor = new String[display];
//                    String[] pubDate = new String[display];

//                    String[] userRating = new String[display];


//                    String[] title = new String[display];
//                    String[] link = new String[display];
//                    String[] description = new String[display];
//                    String[] bloggername = new String[display];
//                    String[] postdate = new String[display];


//                    link: 블로그 글 URL 링크
//                    description: 블로그 글 내용
//                    bloggername: 블로그 주인장 닉네임
//                    postdate: 블로그 글 작성일

//                    List<Movie> movies = new ArrayList<>();

                    Log.d(TAG,"array i : "+ array.length);
                    int k = 0;

                    for (int i = 0; i < array.length; i++) {

                        if (array[i].equals("title"))
                            title[k] = array[i + 2];
                        if (array[i].equals("image"))
                            image[k] = array[i + 2];

                        if (array[i].equals("director"))
                            director[k] = array[i + 2];

                        if (array[i].equals("actor")) {
                            actor[k] = array[i + 2];
                            k++;
                        }

                    }

                    for (int i= 0; i < 10; i++){
                        String match = "[</b><b>]";
                        title[i] =title[i].replaceAll(match, "");
                    }



//                    title[0] = title[0].replaceAll("</b>", "");
//                    title[0] = title[0].replaceAll("<b>", "");

                    ArrayList<Movie> movieList = new ArrayList<>();

                    movieList.add(new Movie(title[0], image[0], director[0], actor[0]));
                    movieList.add(new Movie(title[1], image[1], director[1], actor[1]));
                    movieList.add(new Movie(title[2], image[2], director[2], actor[2]));
                    movieList.add(new Movie(title[3], image[3], director[3], actor[3]));
                    movieList.add(new Movie(title[4], image[4], director[4], actor[4]));
                    movieList.add(new Movie(title[5], image[5], director[5], actor[5]));
                    movieList.add(new Movie(title[6], image[6], director[6], actor[6]));
                    movieList.add(new Movie(title[7], image[7], director[7], actor[7]));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MovieAdapters movieAdapters = new MovieAdapters(movieList, TestActivity.this);
                            recyclerView.setAdapter(movieAdapters);

                            movieAdapters.notifyDataSetChanged();
                        }
                    });

//                    Movie item = new Movie(title[0], image[0], director[0], actor[0]);
//                    Movie item = new Movie(title[1], image[1], director[1], actor[1]);
//                    Movie item = new Movie(title[2], image[2], director[2], actor[2]);
//                    Movie item = new Movie(title[3], image[3], director[3], actor[3]);
//                    Movie item = new Movie(title[4], image[4], director[4], actor[4]);
//                    adapter.addItem(item);


                    Log.d(TAG,"movie : "+movieList.size());
//                    mAdapter = new MovieAdapters((ArrayList<Movie>) movies, TestActivity.this);
//                    MovieRecyclerView.setAdapter(mAdapter);

                    Log.d(TAG, "title 0 : " + title[0]);
                    Log.d(TAG, "title 1 : " + title[1]);
                    Log.d(TAG, "title 2 : " + title[2]);
                    Log.d(TAG, "title 3 : " + title[3]);
                    Log.d(TAG, "title 4 : " + title[4]);

                    Log.d(TAG, "director0 : " + director[0]);
                    Log.d(TAG, "director1 : " + director[1]);
                    Log.d(TAG, "director2 : " + director[2]);
                    Log.d(TAG, "image0 : " + image[0]);
                    Log.d(TAG, "image1 : " + image[1]);
                    Log.d(TAG, "image2 : " + image[2]);
                    Log.d(TAG, "actor0 : " + actor[0]);
                    Log.d(TAG, "actor1 : " + actor[1]);
                    Log.d(TAG, "actor2 : " + actor[2]);


//                    Log.d(TAG, "title잘나오니: " + title[0] + title[1] + title[2]);
                    // title[0], link[0], bloggername[0] 등 인덱스 값에 맞게 검색결과를 변수화하였다.

                } catch (Exception e) {
                    Log.d(TAG, "error : " + e);
                }

            }
        }.start();

    }


}