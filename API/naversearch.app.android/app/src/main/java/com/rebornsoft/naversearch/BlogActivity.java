package com.rebornsoft.naversearch;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
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

public class BlogActivity extends AppCompatActivity {
    BufferedReader br;
    StringBuilder searchResult;

    EditText et_blog;
    Button btn_search;
    RecyclerView recyclerView;
    public static MovieAdapters mAdapter;

    //    ArrayList<Movie> movieList = new ArrayList<>();//컬렉션 프레임워크 객체 초기화 후에 사용가능
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
//        searchNaver("살인");

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);



        et_blog = findViewById(R.id.et_blog);
        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https:blog.naver.com")));
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                        Uri uri = Uri.parse("https:blog.naver.com");
//                    intent.setData(uri);
//                    startActivity(intent);
                    String blog_search = et_blog.getText().toString();
                    searchNaver(blog_search);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    public void searchNaver(final String searchObject) { // 검색어 = searchObject로 ;
        final String clientId = "DGr4VNoEFVmhuXfD5uBT";//애플리케이션 클라이언트 아이디값";
        final String clientSecret = "tNLwkJ1dks";//애플리케이션 클라이언트 시크릿값";
        final int display = 15; // 보여지는 검색결과의 수

        // 네트워크 연결은 Thread 생성 필요

        new Thread() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                try {
                    String text = URLEncoder.encode(searchObject, "UTF-8");
//                    String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text + "&display=" + display + "&"; // 영화 정보 json 결과
                    String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text + "&display=" + display + "&"; // 블로그 정보 json 결과
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
                    String[] link = new String[display];
                    String[] description = new String[display];
                    String[] bloggername = new String[display];
                    String[] postdate = new String[display];
                    String[] bloggerlink = new String[display];


//                    link: 블로그 글 URL 링크
//                    description: 블로그 글 내용
//                    bloggername: 블로그 주인장 닉네임
//                    postdate: 블로그 글 작성일


                    Log.d(TAG,"array i : "+ array.length);
                    int k = 0;

                    for (int i = 0; i < array.length; i++) {

                        if (array[i].equals("title"))
                            title[k] = array[i + 2];
                        if (array[i].equals("link"))
                            link[k] = array[i + 2];
                        if (array[i].equals("description"))
                            description[k] = array[i + 2];
                        if (array[i].equals("bloggername"))
                            bloggername[k] = array[i + 2];
                        if (array[i].equals("postdate"))
                            postdate[k] = array[i + 2];
                        if (array[i].equals("bloggerlink")) {
                            bloggerlink[k] = array[i + 2];
                            k++;
                        }
                        if (bloggerlink[10] != null ) {
                            break;
                        }

                    }

                    for (int i= 0; i < 10; i++){
                        String match = "[</b><b>]";
                        title[i] =title[i].replaceAll(match, "");
                        description[i] =description[i].replaceAll(match, "");
                    }


                    // title, link, description, bloggerlink, postdate, bloggername


//                    title[0] = title[0].replaceAll("</b>", "");
//                    title[0] = title[0].replaceAll("<b>", "");

                    ArrayList<Blog> bloglist = new ArrayList<>();

                    bloglist.add(new Blog(title[0], description[0], bloggername[0], postdate[0]));
                    bloglist.add(new Blog(title[1], description[1], bloggername[1], postdate[1]));
                    bloglist.add(new Blog(title[2], description[2], bloggername[2], postdate[2]));
                    bloglist.add(new Blog(title[3], description[3], bloggername[3], postdate[3]));
                    bloglist.add(new Blog(title[4], description[4], bloggername[4], postdate[4]));
                    bloglist.add(new Blog(title[5], description[5], bloggername[5], postdate[5]));
                    bloglist.add(new Blog(title[6], description[6], bloggername[6], postdate[6]));
                    bloglist.add(new Blog(title[7], description[7], bloggername[7], postdate[7]));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BlogAdapter blogAdapter = new BlogAdapter(bloglist, BlogActivity.this);
                            recyclerView.setAdapter(blogAdapter);

                            blogAdapter.notifyDataSetChanged();
                        }
                    });

//                    Movie item = new Movie(title[0], image[0], director[0], actor[0]);
//                    Movie item = new Movie(title[1], image[1], director[1], actor[1]);
//                    Movie item = new Movie(title[2], image[2], director[2], actor[2]);
//                    Movie item = new Movie(title[3], image[3], director[3], actor[3]);
//                    Movie item = new Movie(title[4], image[4], director[4], actor[4]);
//                    adapter.addItem(item);


//                    Log.d(TAG,"movie : "+movieList.size());
//                    mAdapter = new MovieAdapters((ArrayList<Movie>) movies, TestActivity.this);
//                    MovieRecyclerView.setAdapter(mAdapter);

                    Log.d(TAG, "title 0 : " + title[0]);
                    Log.d(TAG, "title 1 : " + title[1]);
                    Log.d(TAG, "title 2 : " + title[2]);
                    Log.d(TAG, "title 3 : " + title[3]);
                    Log.d(TAG, "title 4 : " + title[4]);
                    Log.d(TAG, "title 0 : " + title[5]);
                    Log.d(TAG, "title 1 : " + title[6]);
                    Log.d(TAG, "title 2 : " + title[7]);
                    Log.d(TAG, "title 3 : " + title[8]);
                    Log.d(TAG, "title 4 : " + title[9]);
                    Log.d(TAG, "link : " + link[0]);
                    Log.d(TAG, "description : " + description[0]);
                    Log.d(TAG, "bloggername : " + bloggername[0]);
                    Log.d(TAG, "postdate : " + postdate[0]);
                    Log.d(TAG, "postdate : " + postdate[1]);
                    Log.d(TAG, "postdate : " + postdate[2]);
                    Log.d(TAG, "postdate : " + postdate[3]);
                    Log.d(TAG, "postdate : " + postdate[4]);
                    Log.d(TAG, "postdate : " + postdate[5]);
                    Log.d(TAG, "postdate : " + postdate[6]);

                    Log.d(TAG, "bloggerlink : " + bloggerlink[0]);

//                    Log.d(TAG, "title잘나오니: " + title[0] + title[1] + title[2]);
                    // title[0], link[0], bloggername[0] 등 인덱스 값에 맞게 검색결과를 변수화하였다.

                } catch (Exception e) {
                    Log.d(TAG, "error : " + e);
                }

            }
        }.start();

    }


}