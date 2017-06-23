package com.robert.testviewtest;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Robert on 2017/6/20.
 */

public class MoveLayout extends RelativeLayout {

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



    private int lastX;
    private int lastY;

    private int screenWidth;
    private int screenHeight;

    private int oriLeft;
    private int oriRight;
    private int oriTop;
    private int oriBottom;

    /**
     * 标示此类的每个实例的id
     */
    private int identity = 0;


    /**
     *触控区域设定
     */
    private int touchAreaLength = 60;

    private int minHeight = 120;
    private int minWidth= 180;
    private static final String TAG = "MoveLinearLayout";


    public MoveLayout(Context context) {
        super(context);
        init();
    }

    public MoveLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoveLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        screenHeight = 500;//getResources().getDisplayMetrics().heightPixels - 40;
        screenWidth = 500;// getResources().getDisplayMetrics().widthPixels;
    }

    public void setViewWidthHeight(int width , int height) {
        screenWidth = width;
        screenHeight = height;
    }

    public void setMinHeight(int height) {
        minHeight = height;
        if(minHeight < touchAreaLength*2) {
            minHeight = touchAreaLength*2;
        }
    }

    public void setMinWidth(int width) {
        minWidth = width;
        if (minWidth < touchAreaLength*3) {
            minWidth = touchAreaLength*3;
        }
    }

    private boolean mFixedSize = false;

    public void setFixedSize(boolean b) {
        mFixedSize = b;
    }

    private int mDeleteHeight = 0;
    private int mDeleteWidth = 0;
    private boolean isInDeleteArea = false;
    public void setDeleteWidthHeight(int width, int height) {
        mDeleteWidth = screenWidth - width;
        mDeleteHeight = height;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
              //   Log.d(TAG, "onTouchEvent: down height="+ getHeight());
                oriLeft = getLeft();
                oriRight = getRight();
                oriTop = getTop();
                oriBottom = getBottom();

                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                dragDirection = getDirection((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
             //      Log.d(TAG, "onTouchEvent: up");
                spotL = false;
                spotT = false;
                spotR = false;
                spotB = false;
                requestLayout();
                mDeleteView.setVisibility(View.INVISIBLE);
               // invalidate();
                break;
//            case MotionEvent.ACTION_CANCEL:
//                Log.d(TAG, "onTouchEvent: cancel");
//                spotL = false;
//                spotT = false;
//                spotR = false;
//                spotB = false;
//                invalidate();
//                break;
            case MotionEvent.ACTION_MOVE:
                 // Log.d(TAG, "onTouchEvent: move");
                int tempRawX = (int)event.getRawX();
                int tempRawY = (int)event.getRawY();

                int dx = tempRawX - lastX;
                int dy = tempRawY - lastY;
                lastX = tempRawX;
                lastY = tempRawY;

                switch (dragDirection) {
                    case LEFT:
                        left( dx);
                        break;
                    case RIGHT:
                        right( dx);
                        break;
                    case BOTTOM:
                        bottom(dy);
                        break;
                    case TOP:
                        top( dy);
                        break;
                    case CENTER:
                        center( dx, dy);
                        break;
//                    case LEFT_BOTTOM:
//                        left( dx);
//                        bottom( dy);
//                        break;
//                    case LEFT_TOP:
//                        left( dx);
//                        top(dy);
//                        break;
//                    case RIGHT_BOTTOM:
//                        right( dx);
//                        bottom( dy);
//                        break;
//                    case RIGHT_TOP:
//                        right( dx);
//                        top( dy);
//                        break;
                }

                //new pos l t r b is set into oriLeft, oriTop, oriRight, oriBottom
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(oriRight - oriLeft, oriBottom - oriTop);
                lp.setMargins(oriLeft,oriTop,0,0);
                setLayoutParams(lp);
             //   Log.d(TAG, "onTouchEvent: set layout width="+(oriRight - oriLeft)+" height="+(oriBottom - oriTop));
             //   Log.d(TAG, "onTouchEvent: marginLeft="+oriLeft+"  marginTop"+oriTop);
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

        oriLeft = left;
        oriTop = top;
        oriRight = right;
        oriBottom = bottom;

        //todo: show delete icon
        mDeleteView.setVisibility(View.VISIBLE);
        //do delete
        if(isInDeleteArea == false && oriRight > mDeleteWidth && oriTop < mDeleteHeight) {//delete
            Log.e(TAG, "center: oriRight"+oriRight+  "  mDeleteWidth"+mDeleteWidth  +"  oriTop"+ oriTop+ "  mDeleteHeightv"+ mDeleteHeight);
            if(mListener != null) {
                mListener.onDeleteMoveLayout(identity);
                mDeleteView.setVisibility(View.INVISIBLE);
                isInDeleteArea = true;//this object is deleted ,only one-time-using
            }
        }

    }

    /**
     * 触摸点为上边缘
     */
    private void top(int dy) {
        oriTop += dy;
        if (oriTop < 0) {
            oriTop = 0;
        }
        if (oriBottom - oriTop  < minHeight) {
            oriTop = oriBottom  - minHeight;
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
        if (oriBottom - oriTop  < minHeight) {
            oriBottom = minHeight + oriTop;
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
        if (oriRight - oriLeft  < minWidth) {
            oriRight = oriLeft  + minWidth;
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
        if (oriRight - oriLeft  < minWidth) {
            oriLeft = oriRight - minWidth;
        }
    }

    private int getDirection( int x, int y) {
        int left = getLeft();
        int right = getRight();
        int bottom = getBottom();
        int top = getTop();

//        if (x < touchAreaLength && y < touchAreaLength) {
//            return LEFT_TOP;
//        }
//        if (y < touchAreaLength && right - left - x < touchAreaLength) {
//            return RIGHT_TOP;
//        }
//        if (x < touchAreaLength && bottom - top - y < touchAreaLength) {
//            return LEFT_BOTTOM;
//        }
//        if (right - left - x < touchAreaLength && bottom - top - y < touchAreaLength) {
//            return RIGHT_BOTTOM;
//        }
        if (mFixedSize == true) {
            return CENTER;
        }

        if (x < touchAreaLength) {
            spotL = true;
            requestLayout();
            return LEFT;
        }
        if (y < touchAreaLength) {
            spotT = true;
            requestLayout();
            return TOP;
        }
        if (right - left - x < touchAreaLength) {
            spotR = true;
            requestLayout();
            return RIGHT;
        }
        if (bottom - top - y < touchAreaLength) {
            spotB = true;
            requestLayout();
            return BOTTOM;
        }
        return CENTER;
    }

    private boolean spotL = false;
    private boolean spotT = false;
    private boolean spotR = false;
    private boolean spotB = false;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);


        RelativeLayout rlt = (RelativeLayout) getChildAt(0);
        int count = rlt.getChildCount();

        for (int a = 0; a < count; a ++) {
            if(a == 1) {        //l
                if(spotL)
                    rlt.getChildAt(a).setVisibility(View.VISIBLE);
                else
                    rlt.getChildAt(a).setVisibility(View.INVISIBLE);
            } else if(a == 2) { //t
                if(spotT)
                    rlt.getChildAt(a).setVisibility(View.VISIBLE);
                else
                    rlt.getChildAt(a).setVisibility(View.INVISIBLE);
            } else if(a == 3) { //r
                if(spotR)
                    rlt.getChildAt(a).setVisibility(View.VISIBLE);
                else
                    rlt.getChildAt(a).setVisibility(View.INVISIBLE);
            } else if(a == 4) { //b
                if(spotB)
                    rlt.getChildAt(a).setVisibility(View.VISIBLE);
                else
                    rlt.getChildAt(a).setVisibility(View.INVISIBLE);
            }
           // Log.d(TAG, "onLayout: "+rlt.getChildAt(a).getClass().toString());
        }

    }



    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    //set the main delete area object (to change visibility)
    private View mDeleteView = null;
    public void setDeleteView(View v) {
        mDeleteView = v;
    }

    //delete listener
    private DeleteMoveLayout mListener = null;
    public interface DeleteMoveLayout {
        void onDeleteMoveLayout(int identity);
    }
    public void setOnDeleteMoveLayout(DeleteMoveLayout l) {
        mListener = l;
    }

}
