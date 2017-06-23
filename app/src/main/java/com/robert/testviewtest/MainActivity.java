package com.robert.testviewtest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private Button btn2;
    private Button btn3;

    private DragView mDragView;
    private Context mContext;


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;


        btn = (Button)findViewById(R.id.button);
        btn2 = (Button)findViewById(R.id.button2);
        btn3 = (Button)findViewById(R.id.button3);


        mDragView = (DragView)findViewById(R.id.dragview);
        mDragView.addDragView(R.layout.my_self_view, 0,400,380,760, true, false);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDragView.addDragView(R.layout.my_self_view, 0,400,380,760, true,false);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                mDragView.addDragView(R.layout.my_self_view, 0,400,380,760, false,false);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");

                TextView tt = new TextView(mContext);
                tt.setText("今天大雨，出门记得带伞哦！");
                tt.setTextColor(Color.BLACK);
                mDragView.addDragView(tt,50,50, 700,100,true, true);
            }
        });

    }
}
