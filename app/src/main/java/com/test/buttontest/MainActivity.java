package com.test.buttontest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;


public class MainActivity extends AppCompatActivity {
    Button[] btn=new Button[6];
    ConstraintLayout constraintLayout;
    float[][] XY=new float[6][2];
    float xDown=0,yDown=0,x,y,lastX,lastY,beginX,beginY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //自定義ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Wendy");

        btn[0]=findViewById(R.id.button1);
        btn[1]=findViewById(R.id.button2);
        btn[2]=findViewById(R.id.button3);
        btn[3]=findViewById(R.id.button4);
        btn[4]=findViewById(R.id.button5);
        btn[5]=findViewById(R.id.button6);
        constraintLayout=findViewById(R.id.constraintLayout);

        //儲存每個按鈕的x,y
        constraintLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                for (int i =0;i<btn.length;i++){
                    XY[i][0]=btn[i].getX();
                    XY[i][1]=btn[i].getY();
                }
                constraintLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this::onGlobalLayout);
            }
        });

        //為每個按鈕監聽Touch行為
        for (Button button : btn) {
            button.setOnTouchListener(BtnListener);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private final View.OnTouchListener BtnListener=(view, motionEvent) -> {
        switch (motionEvent.getActionMasked()) {
            //剛開始把他的手放在按鈕上
            case MotionEvent.ACTION_DOWN:

                //設立觸碰點等於按鈕中心點
                motionEvent.setLocation((float) view.getWidth()/2,(float)view.getHeight()/2);
                //按鈕變透明
                view.setAlpha((float) 0.8);
                //把圖層移到最上方
                view.bringToFront();
                //儲存目前點擊按鈕的x,y
                x=view.getX();
                y=view.getY();

                //取得目前觸碰點位置(到手機螢幕邊界)
                lastX = motionEvent.getRawX();
                lastY = motionEvent.getRawY();
                //將位置寫為起始位置
                beginX = lastX;
                beginY = lastY;
                //觸碰點的位置(到按鈕邊界)
                xDown = motionEvent.getX();
                yDown = motionEvent.getY();

                break;

            //使用者拖曳按鈕
            case MotionEvent.ACTION_MOVE:
                float movedX, movedY;
                //觸碰點的位置
                movedX = motionEvent.getX();
                movedY = motionEvent.getY();
                //計算移動距離
                float distanceX = movedX - xDown;
                float distanceY = movedY - yDown;
                //移動到該位置
                view.setX(view.getX() + distanceX);
                view.setY(view.getY() + distanceY);
                //移動完觸碰點的最新位置(到螢幕邊界)
                lastY = motionEvent.getRawY();
                lastX = motionEvent.getRawX();
                break;

            //使用者將手移開按鈕
            case  MotionEvent.ACTION_UP:
                //恢復原本透明度
                view.setAlpha(1);

                //如果按鈕移動幅度太小視為點擊事件
                if (Math.abs(lastX - beginX)< 10 && Math.abs(lastY - beginY)<10){
                    view.setX(x);
                    view.setY(y);
                    if(view.getId()==btn[0].getId()){
                        Intent intent=new Intent(this, MainActivity3.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    }else{
                        //取得按鈕上的文字
                        String btnText = ((Button)view).getText().toString();
                        //取得按鈕顏色
                        int color;
                        Drawable background = view.getBackground();
                        color=((ColorDrawable)background).getColor();
                        //傳值＆跳頁
                        Intent intent=new Intent(this,MainActivity2.class);
                        intent.putExtra("number", btnText);
                        intent.putExtra("color", color);
                        startActivity(intent);
                    }
                }else{//判斷點擊點要交換位置還是返回原點
                    for(int i=0;i<btn.length;i++){
                        if(view.getId()!=btn[i].getId()){//如果按鈕不等於它自己
                            if(viewInBtn(btn[i], motionEvent.getRawX(),motionEvent.getRawY())){//觸碰點在按鈕中
                                view.setX(XY[i][0]);
                                view.setY(XY[i][1]);
                                btn[i].setX(x);
                                btn[i].setY(y);
                                break;
                            }else{//觸碰點不在任何按鈕中，返回原先按鈕位置
                                view.setX(x);
                                view.setY(y);
                            }
                        }
                    }
                    view.setPressed(false);
                }
        }
        return true;
    };

    // 判斷中心點是否進入目標按鈕
    private boolean viewInBtn(Button tarBtn, float x, float y){
        int[] location = new int[2];
        tarBtn.getLocationOnScreen(location);
        float left = location[0];
        float top = location[1];
        float right = left+tarBtn.getMeasuredWidth();
        float bottom=top+tarBtn.getMeasuredHeight();
        return  x>=left&&x<=right&&y>=top&&y<=bottom;
    }

    /*抓按鈕位置資料也可以寫在這裡面，onCreate裡抓不到(生命週期)。
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }
    */
}