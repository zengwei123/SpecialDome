package com.example.zengwei.specialdome;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zengwei on 2018/5/31.
 */

public class ZwCanvas extends View{
    ///***********************************************************
    private Paint linePaint;   //画笔
    private float Mywidth;     //屏幕宽度
    private float Myheight;    //屏幕高度

    private float x=0;  //按钮移动X
    private float y=0;   //按钮移动Y
    private float x_down,y_down; // 按下x位置y位置

    private int iAlpha=255;    //透明度
    private int index=0;       //执行的事件
    private RelativeLayout relativeLayout;    //所在布局
    private ZwCanvasListener zwCanvasListener;    //监听事件
    private boolean ispingfan=true;      //禁止频繁操作
    private boolean ishalo=false;
    private int haloR1=0;
    private int haloR2=50;
    private BitmapFactory.Options options;
    private  Bitmap bitmap;
    private RadialGradient radialGradient;
    public ZwCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //画笔
        linePaint=new Paint();
        linePaint.setStyle(Paint.Style.FILL );
        linePaint.setColor(Color.GREEN);
        linePaint.setStrokeWidth(8);
        //屏幕宽高
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Mywidth= wm.getDefaultDisplay().getWidth();
        Myheight= wm.getDefaultDisplay().getHeight();

        options = new BitmapFactory.Options();
        bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.start, options);  //可设置图片
        //可设置颜色
        radialGradient=new RadialGradient(Mywidth/2,Myheight-Myheight/5,200,Color.parseColor("#FFFFFF"),Color.parseColor("#00A4FF"), Shader.TileMode.MIRROR);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //画出图片
        /**
         * Mywidth/2+x-bitmap.getWidth()/2   X中心减去图片一半就是中心位置
         * Myheight-Myheight/5+y-bitmap.getHeight()/2   这个是Y所在位置
         */
       // butHalo(canvas);
        linePaint.setShader(radialGradient);
        linePaint.setAlpha(haloR2);
        linePaint.setStyle(Paint.Style.STROKE );
        linePaint.setStrokeWidth(20);  //可设置大小
        canvas.drawCircle(Mywidth/2,Myheight-Myheight/5,haloR1,linePaint);
        linePaint.setAlpha(255);
        canvas.drawBitmap(bitmap,Mywidth/2+x-bitmap.getWidth()/2,Myheight-Myheight/5+y-bitmap.getHeight()/2,linePaint);
    }


    //传入父布局
    public void setRelativeLayout(RelativeLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }
    //传入监听方法
    public void setZwCanvasListener(ZwCanvasListener zwCanvasListener) {
        this.zwCanvasListener = zwCanvasListener;
    }
    //X中心位置
    public float getMywidth() {
        return Mywidth/2;
    }
    //Y轴位置
    public float getMyheight() {
        return Myheight-Myheight/5;
    }

    public float getMyheights() {
        return Myheight/5;
    }


    /**
     *  手指抬起复位按钮
     */
    public void up(){
        x = 0;
        y = 0;
        invalidate();
    }

    /**
     *  按钮移动中所出发的事件
     */
    public void move(float xx, float yy){
        //移动的距离
        x=xx;y=yy;invalidate();
        //判断左右移动的距离 或者 向上移动的距离  是否为X Y 的获取距离
        if (Math.abs(xx)==getMywidth()||Math.abs(yy)==getMyheight()) {
            //回归原点
            x=0;y=0;
            relativeLayout.getBackground().setAlpha(0);   //同时背景的透明度也归0   让人看不见
            //判断是否执行过监听事件 true为没有
            if(ispingfan){
                iAlpha=0;   //画笔透明度为0
                linePaint.setAlpha(iAlpha); //设置透明赌
                //执行线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //为false说明执行了监听事件
                        ispingfan=false;
                        //透明度慢慢回归  按钮渐渐显示  这是一个按钮回归原点的过度动画
                        for(int is=0;is<51;is++){
                            iAlpha+=5;
                            linePaint.setAlpha(iAlpha);
                            postInvalidate();
                            try {
                                Thread.sleep(30);  //每次执行暂停30毫秒    可设置
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        //执行完毕后设置监听事件为可以执行
                        ispingfan=true;
                    }
                }).start();
            }else{
                //可以进行一个提示  提醒用户不用频繁操作
            }
        }
    }

    /**
     * 按下 移动 抬起  按钮的全部事件操作在这里
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果是按下
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            //判断按下的位置是否在按钮内
            if(event.getX()>(getMywidth()-80)&&
                    event.getX()<(getMywidth()+80)&&
                    event.getY()>(getMyheight()-80)&&
                    event.getY()<(getMyheight()+80)){
                //按下后背景图片显示
                relativeLayout.getBackground().setAlpha(255);
                //获取按下按钮的坐标点
                x_down=event.getX();
                y_down=event.getY();
                ishalo=true;
                zwCanvasListener.butdown();
                butHalo();
            }else{
                //如果没按上按钮，按钮无法移动
                x_down=0;
                y_down=0;
            }
        //移动事件
        }else if(event.getAction()==MotionEvent.ACTION_MOVE){
            //按下的位置不能为0
            if (x_down!=0&&y_down!=0){
                //左右移动的距离 大于上移的距离  那么认定为左右移动
                if(Math.abs(x_down-event.getX())>y_down-event.getY()){
                    haloR1=0;ishalo=false;
                    invalidate();
                    //如果认定为上移动事之后 如果 左右移动大于上移动 那么还是执行上移动操作
                    if(index==2){
                        move(0,event.getY()-y_down);
                    }else {
                        //初始为0   当认定为左右移动时 设置为1
                        index = 1;
                        //移动事件
                        move(event.getX() - x_down, 0);
                    }
                }
                //左右移动的距离 小于上移的距离  那么认定为上移动
                if(Math.abs(x_down-event.getX())<y_down-event.getY()){
                    haloR1=0;ishalo=false;
                    invalidate();
                    //如果认定为左右移动事之后 如果 左右移动小于上移动 那么还是执行左右移动操作
                    if(index==1){
                        move(event.getX()-x_down,0);
                    }else{
                        //初始为0   当认定为左右移动时 设置为1
                        index=2;
                        //移动事件
                        move(0,event.getY()-y_down);
                    }
                }

            }
            //抬起复位
        }else if(event.getAction()==MotionEvent.ACTION_UP){
            if(event.getX()<150){
                if(index==1){
                    if(ispingfan){
                        zwCanvasListener.left();
                    }
                    kuoHalo();
                    move(0-getMywidth(),0);
                }else {
                    relativeLayout.getBackground().setAlpha(0);
                    up();
                    index=0;
                }
            }else if(event.getX()>(getMywidth()*2-150)){
                if(index==1){
                    if(ispingfan){
                        zwCanvasListener.right();
                    }
                    kuoHalo();
                    move(getMywidth(),0);
                }else {
                    relativeLayout.getBackground().setAlpha(0);
                    up();
                    index=0;
                }
            }else if(y_down-event.getY()>getMyheights()*2){
                if(index==2){
                    if(ispingfan){
                        zwCanvasListener.up();
                    }
                    move(0,getMyheight());
                }else {
                    relativeLayout.getBackground().setAlpha(0);
                    up();
                    index=0;
                }
            }else{
                relativeLayout.getBackground().setAlpha(0);
                up();
                index=0;
            }
            ishalo=false;
            zwCanvasListener.butup();
        }
        return true;
    }

    //光弧阔收
    public void butHalo(){
            new Thread(new Runnable(){
                @Override
                public void run() {
                    haloR1=bitmap.getWidth()/2;
                    boolean thishalos=true;
                    while (ishalo){
                        if(thishalos){
                            if(haloR1>=bitmap.getWidth()/2+50){
                              //  haloR1=bitmap.getWidth()/2-10;
                                thishalos=false;
                            }else {
                                haloR1++;
                            }
                        }else{
                            if(haloR1<=bitmap.getWidth()/2-10){
                                haloR1=bitmap.getWidth()/2-10;
                                thishalos=true;
                            }else {
                                haloR1--;
                            }
                        }

                        postInvalidate();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    haloR1=0;
                    postInvalidate();
                }
            }).start();
    }
    //光弧扩散
    public void kuoHalo(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                while (true){
                    haloR1+=5;
                    if(haloR1>3000){
                        break;
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    //设置Bitmap透明度
    public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number){
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

                .getWidth(), sourceImg.getHeight());// 获得图片的ARGB值

        number = number * 255 / 100;

        for (int i = 0; i < argb.length; i++) {

            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);

        }

        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg

                .getHeight(), Bitmap.Config.ARGB_8888);

        return sourceImg;
    }
    //设置Bitmap适应屏幕
    public Bitmap resizeBitmap(Bitmap bitmap,float w,float h) {
        if(bitmap!=null)
        {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float newWidth = w;
            float newHeight = h;
            float scaleWight = newWidth/width;
            float scaleHeight = newHeight/height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWight, scaleHeight);
            Bitmap res = Bitmap.createBitmap(bitmap, 0,0,width, height, matrix, true);
            return res;
        }
        else{
            return null;
        }
    }
}
