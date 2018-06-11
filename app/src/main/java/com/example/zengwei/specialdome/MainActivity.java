package com.example.zengwei.specialdome;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private ZwCanvas zwCanvas;
    private float x,y; // 按下位置
    private int i=0;

    private RelativeLayout two_layout,abcdd;
    private AnimationDrawable animationDrawable;
    Resources resources ;
    Drawable btnDrawable;

    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button=findViewById(R.id.button);
        two_layout = findViewById(R.id.two_layout);
        abcdd = findViewById(R.id.abcdd);
        resources = getApplicationContext().getResources();
        btnDrawable = resources.getDrawable(R.mipmap.tu);
        two_layout.setBackgroundDrawable(btnDrawable);
        two_layout.getBackground().setAlpha(0);

        zwCanvas = findViewById(R.id.zw);
        zwCanvas.setRelativeLayout(two_layout);
        ViewOutlineProvider viewOutlineProvider=new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                //x，y轴表示位置，后两个参数表示长，宽
                outline.setOval(0,100,abcdd.getWidth(),abcdd.getHeight());
                outline.setAlpha(200);
            }
        };
        abcdd.setOutlineProvider(viewOutlineProvider);
        zwCanvas.setZwCanvasListener(new ZwCanvasListener() {
            @Override
            public void up() {
                Toast.makeText(MainActivity.this, "上滑动", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void left() {
                Toast.makeText(MainActivity.this, "左滑动", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void right() {
                Toast.makeText(MainActivity.this, "右滑动", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void butdown() {
                rotateyAnimRun(abcdd);
            }

            @Override
            public void butup() {
                rotateyAnimRun1(abcdd);
            }
        });
    }
    public void rotateyAnimRun(View view) {

        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.90f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotationX", 0.0F, 38.0F);
        ObjectAnimator translate = ObjectAnimator.ofFloat(view, "translationY", 0F, 730F);
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(rotation,scaleY,scaleX,translate);
        animatorSet.start();
    }
    public void rotateyAnimRun1(View view) {

        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.90f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.1f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotationX", 38.0F, 0F);
        ObjectAnimator translate = ObjectAnimator.ofFloat(view, "translationY", 730F, 0F);
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(rotation,scaleY,scaleX,translate);
        animatorSet.start();
    }
}
