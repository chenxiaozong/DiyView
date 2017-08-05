package com.example.a01_scrollview.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * Created by chen on 2017/8/5.
 * 自定义ViewGroup
 *
 */

public class MyScrollView extends ScrollView {


    private View childview;//scrollview布局文件中的子视图

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
    }


    /**
     * 重新加载子视图
     */

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if(getChildCount()>0) {

            childview = getChildAt(0);

        }

    }



    private Rect normal = new Rect() ; //用于记录临界状态的上下左右的坐标

    private  int lastY; //记录上一次y轴方向坐标的位置
    /**
     * 触摸事件
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if(childview==null) {
        return super.onTouchEvent(ev);
        }

        int eventY = (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastY = eventY;
                break;

            case MotionEvent.ACTION_MOVE:
                int dy = eventY-lastY;//计算微小的移动量


                if(isNeedMove()) {//如果滑动超出边界，执行自定义scrollview的操作
                    if(normal.isEmpty()) {
                        //记录临界状态的左上右下
                    normal.set(childview.getLeft(),childview.getTop(),childview.getRight(),childview.getBottom());
                    }

                    childview.layout(childview.getLeft(),childview.getTop()+dy/2,childview.getRight(),childview.getBottom()+dy/2);

                }
                lastY = eventY;
                break;

            case MotionEvent.ACTION_UP://还原scrollview
                //1. 使用平移动画
                int translateY = childview.getBottom() - normal.bottom;
                TranslateAnimation translate = new TranslateAnimation(0,0,0,-translateY);

                translate.setDuration(1000);
                //translate.setFillAfter(true);

                //设置动画监听，当动画完成时，将scrollv恢复到边界布局
                translate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        childview.clearAnimation();
                        childview.layout(normal.left,normal.top,normal.right,normal.bottom);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


                childview.startAnimation(translate);


                break;
        }

        return  super.onTouchEvent(ev);
    }


    /**
     * 根据坐标，判断scrollview是否超出边界
     * @return
     */
    private boolean isNeedMove() {
        int scrollY = this.getScrollY();//        //1. 获取scroll偏移量


        int childHight = childview.getMeasuredHeight();// 获取子视图高度

        int dy =childHight-this.getMeasuredHeight();//dy =  childHight-scrollHeight


        if(scrollY<=0||scrollY>=dy) {//1. 向上滑动超出边界 2. 向下滑动超出边界
            return true;
        }
        return  false;//处在临界范围内的 返回false 按照默认scrollview方式处理 滑动


    }


}






