package com.example.login_register;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    //대량의 문자열 데이터를 저장할 Arraylist 객체 생성

    ArrayList<String> mDatas= new ArrayList<String>();
    ListView listview; //ListView 참조변수

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //문자열 데이터 ArrayList에 추가
        mDatas.add("KOREA");
        mDatas.add("CANADA");
        mDatas.add("FRANCE");
        mDatas.add("MEXICO");
        mDatas.add("POLAND");
        mDatas.add("SAUDI ARABIA");
        //ListView가 보여줄 뷰를 만들어내는 Adapter 객체 생성
        //ArrayAdapter : 문자열 데이터들을 적절한 iew로 1:1로 만들어서 List형태로 ListView에 제공하는 객체
        //첫번째 파라미터 : Context객체 ->MainActivity가 Context를 상속했기 때문에 this로 제공 가능
        //두번째 파라미터 : 문자열 데이터를 보여줄 뷰. ListView에 나열되는 하나의 아이템 단위의 뷰 모양
        //세번째 파라미터 : adapter가 뷰로 만들어줄 대량의 데이터들
        //본 예제에서는 문자열만 하나씩 보여주면 되기 때문에 두번째 파라미터의 뷰 모먕은 Android 시스템에서 제공하는
        //기본 Layout xml 파일을 사용함.

        ArrayAdapter adapter= new ArrayAdapter(this, android.R.layout.simple_list_item_1, mDatas);

        listview= (ListView)findViewById(R.id.listview);
        listview.setAdapter(adapter); //위에 만들어진 Adapter를 ListView에 설정 : xml에서 'entries'속성

        //ListView의 아이템 하나가 클릭되는 것을 감지하는 Listener객체 설정 (Button의 OnClickListener와 같은 역할)
        listview.setOnItemClickListener(listener);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //ListView의 아이템 중 하나가 오래 클릭될 때 호출되는 메소드
            //첫번째 파라미터 : 오래 눌러진 아이템을 보여주고 있는 AdapterView 객체(여기서는 ListView객체)
            //두번째 파라미터 : 오래 눌러진 아이템 뷰
            //세번째 파라미터 : 오래 눌러진 아이템의 위치(ListView이 첫번째 아이템(가장위쪽)부터 차례대로 0,1,2,3.....)
            //네번재 파리미터 : 오래 눌러진 아이템의 아이디(특별한 설정이 없다면 세번째 파라이터인 position과 같은 값)

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                //PopupMenu객체 생성.
                //생성자함수의 첫번재 파라미터 : Context
                //생성자함수의 두번째 파라미터 : Popup Menu를 붙일 anchor 뷰
                PopupMenu popup= new PopupMenu(MainActivity2.this, view);//view는 오래 눌러진 뷰를 의미

                //Popup Menu에 들어갈 MenuItem 추가.
                //이전 포스트의 컨텍스트 메뉴(Context menu)처럼 xml 메뉴 리소스 사용
                //첫번재 파라미터 : res폴더>>menu폴더>>menu_listview.xml파일 리소스
                //두번재 파라미터 : Menu 객체->Popup Menu 객체로 부터 Menu 객체 얻어오기
                getMenuInflater().inflate(R.menu.mainmenu, popup.getMenu());
                //Popup menu의 메뉴아이템을 눌렀을  때 보여질 ListView 항목의 위치
                //Listener 안에서 사용해야 하기에 final로 선언
                final int index= position;
                //Popup Menu의 MenuItem을 클릭하는 것을 감지하는 listener 설정
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // TODO Auto-generated method stub
                        //선택된 Popup Menu의  아이템아이디를 구별하여 원하는 작업 수행
                        //예제에서는 선택된 ListView의 항목(String 문자열) data와 해당 메뉴이름을 출력함
                        switch( item.getItemId() ){
                            case R.id.modify:
                                Toast.makeText(MainActivity2.this, mDatas.get(index)+" Modify", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.delete:
                                Toast.makeText(MainActivity2.this, mDatas.get(index)+" Delete", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.info:
                                Toast.makeText(MainActivity2.this, mDatas.get(index)+" Info", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
                popup.show();//Popup Menu 보이기
                return false;
            }
        });
        //ListView를 Context 메뉴로 등록
     //   registerForContextMenu(listview);
    }
    //ListView의 아이템 하나가 클릭되는 것을 감지하는 Listener객체 생성 (Button의 OnClickListener와 같은 역할)
    AdapterView.OnItemClickListener listener= new AdapterView.OnItemClickListener() {
        //ListView의 아이템 중 하나가 클릭될 때 호출되는 메소드
        //첫번째 파라미터 : 클릭된 아이템을 보여주고 있는 AdapterView 객체(여기서는 ListView객체)
        //두번째 파라미터 : 클릭된 아이템 뷰
        //세번째 파라미터 : 클릭된 아이템의 위치(ListView이 첫번째 아이템(가장위쪽)부터 차례대로 0,1,2,3.....)
        //네번재 파리미터 : 클릭된 아이템의 아이디(특별한 설정이 없다면 세번째 파라이터인 position과 같은 값)
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            //클릭된 아이템의 위치를 이용하여 데이터인 문자열을 Toast로 출력
            Toast.makeText(MainActivity2.this, mDatas.get(position), Toast.LENGTH_SHORT).show();
        }
    };
}