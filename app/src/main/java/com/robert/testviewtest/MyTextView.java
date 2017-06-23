package com.robert.testviewtest;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.TintContextWrapper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;


/**
 * Created by Robert on 2017/6/19.
 */

public class MyTextView extends AppCompatTextView {

    private int dragDirection = 0;
    private static final int TOP = 0x15;
    private static final int LEFT = 0x16;
    private static final int BOTTOM = 0x17;
    private static final int RIGHT = 0x18;
    private static final int LEFT_TOP = 0x11;
    private static final int RIGHT_TOP = 0x12;
    private static final int LEFT_BOTTOM = 0x13;
    private static final int RIGHT_BOTTOM = 0x14;
    private static final int CENTER = 0x19;

    private int touchAreaLength = 60;

    private int lastX;
    private int lastY;

    protected int screenWidth;
    protected int screenHeight;

    private int oriLeft;
    private int oriRight;
    private int oriTop;
    private int oriBottom;


    private static final String TAG = "MyTextView";

    public MyTextView(Context context) {
        this(context, null);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(TintContextWrapper.wrap(context), attrs, defStyleAttr);
        screenHeight = getResources().getDisplayMetrics().heightPixels - 40;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        int action = event.getAction();
        switch (action) {   
            case MotionEvent.ACTION_DOWN:
               // Log.d(TAG, "onTouchEvent: down");
                //do thing
                oriLeft = getLeft();
                oriRight = getRight();
                oriTop = getTop();
                oriBottom = getBottom();

                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                dragDirection = getDirection((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
                //do thing
             //   Log.d(TAG, "onTouchEvent: up");
                break;
            case MotionEvent.ACTION_MOVE:
              //  Log.d(TAG, "onTouchEvent: move");
                int tempRawX = (int)event.getRawX();
                int tempRawY = (int)event.getRawY();

                int dx = tempRawX - lastX;
                int dy = tempRawY - lastY;
                lastX = tempRawX;
                lastY = tempRawY;

                switch (dragDirection) {
                    case LEFT: // 左边缘
                        left( dx);
                        break;
                    case RIGHT: // 右边缘
                        right( dx);
                        break;
                    case BOTTOM: // 下边缘
                        bottom(dy);
                        break;
                    case TOP: // 上边缘
                        top( dy);
                        break;
                    case CENTER: // 点击中心-->>移动
                        center( dx, dy);
                        break;
                    case LEFT_BOTTOM: // 左下
                        left( dx);
                        bottom( dy);
                        break;
                    case LEFT_TOP: // 左上
                        left( dx);
                        top(dy);
                        break;
                    case RIGHT_BOTTOM: // 右下
                        right( dx);
                        bottom( dy);
                        break;
                    case RIGHT_TOP: // 右上
                        right( dx);
                        top( dy);
                        break;
                }

                //new pos l t r b is set into oriLeft, oriTop, oriRight, oriBottom
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(oriRight - oriLeft, oriBottom - oriTop);
                lp.setMargins(oriLeft,oriTop,0,0);
                setLayoutParams(lp);
                Log.d(TAG, "onTouchEvent: set layout");

                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 触摸点为中心->>移动
     */
    private void center(int dx, int dy) {
        int left = getLeft() + dx;
        int top = getTop() + dy;
        int right = getRight() + dx;
        int bottom = getBottom() + dy;


        if (left < 0) {
            left = 0;
            right = left + getWidth();
        }
        if (right > screenWidth ) {
            right = screenWidth ;
            left = right - getWidth();
        }
        if (top < 0) {
            top = 0;
            bottom = top + getHeight();
        }
        if (bottom > screenHeight ) {
            bottom = screenHeight ;
            top = bottom - getHeight();
        }

     //   lastdo = 0;
      //  v.layout(left, top, right, bottom);
        oriLeft = left;
        oriTop = top;
        oriRight = right;
        oriBottom = bottom;

    }

    /**
     * 触摸点为上边缘
     */
    private void top(int dy) {
        oriTop += dy;
        if (oriTop < 0) {
            oriTop = 0;
        }
        if (oriBottom - oriTop  < 200) {
            oriTop = oriBottom  - 200;
        }
    }

    /**
     * 触摸点为下边缘
     */
    private void bottom(int dy) {

        oriBottom += dy;
        if (oriBottom > screenHeight ) {
            oriBottom = screenHeight ;
        }
        if (oriBottom - oriTop  < 200) {
            oriBottom = 200 + oriTop;
        }
    }

    /**
     * 触摸点为右边缘
     */
    private void right(int dx) {
        oriRight += dx;
        if (oriRight > screenWidth ) {
            oriRight = screenWidth ;
        }
        if (oriRight - oriLeft  < 200) {
            oriRight = oriLeft  + 200;
        }
    }

    /**
     * 触摸点为左边缘
     */
    private void left(int dx) {
        oriLeft += dx;
        if (oriLeft < 0) {
            oriLeft = 0;
        }
        if (oriRight - oriLeft  < 200) {
            oriLeft = oriRight - 200;
        }
    }




    protected int getDirection( int x, int y) {
        int left = getLeft();
        int right = getRight();
        int bottom = getBottom();
        int top = getTop();
        if (x < touchAreaLength && y < touchAreaLength) {
            return LEFT_TOP;
        }
        if (y < touchAreaLength && right - left - x < touchAreaLength) {
            return RIGHT_TOP;
        }
        if (x < touchAreaLength && bottom - top - y < touchAreaLength) {
            return LEFT_BOTTOM;
        }
        if (right - left - x < touchAreaLength && bottom - top - y < touchAreaLength) {
            return RIGHT_BOTTOM;
        }
        if (x < touchAreaLength) {
            return LEFT;
        }
        if (y < touchAreaLength) {
            return TOP;
        }
        if (right - left - x < touchAreaLength) {
            return RIGHT;
        }
        if (bottom - top - y < touchAreaLength) {
            return BOTTOM;
        }
        return CENTER;
    }


    //    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//
//        Log.e(TAG, "#####################################onMeasure: ");
//        int width =0;
//        int height=0;
//
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        if (widthMode == MeasureSpec.EXACTLY) {
//            // Parent has told us how big to be. So be it.
//            width = widthSize;
//            Log.d(TAG, "onMeasure: width="+width);
//        } else {
//            Log.d(TAG, "onMeasure: width=not match");
//        }
//
//        if (heightMode == MeasureSpec.EXACTLY) {
//            // Parent has told us how big to be. So be it.
//            height = heightSize;
//            Log.d(TAG, "onMeasure: height="+ height);
//        } else {
//            Log.d(TAG, "onMeasure: height not set=");
//        }
//        setMeasuredDimension(width, height);
//        Log.d(TAG, "onMeasure: Dimension w="+ width+" h="+ height);
//    }
}
