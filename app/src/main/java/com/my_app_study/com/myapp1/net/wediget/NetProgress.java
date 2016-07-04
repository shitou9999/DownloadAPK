package com.my_app_study.com.myapp1.net.wediget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.my_app_study.com.myapp1.net.IProgressCallBack;

/**
 * Created by MT3020 on 2015/11/5.
 */
public abstract class NetProgress extends FrameLayout {
    private static LayoutParams layoutParams = new LayoutParams(-1,-1);

    ProInfo info;
    IProgressCallBack callBack;

    public NetProgress(Context context) {
        super(context);
    }

    public NetProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float mProgress;

    public void setProgress(float progress){
        this.mProgress = progress;
        invalidate();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        info = new ProInfo(getContext());
        info.setLayoutParams(layoutParams);
        addView(info, 0);
        info.setVisibility(GONE);
    }




//    @Override
//    public void successful(Object data) {
//        info.setVisibility(GONE);
//    }
//
//    @Override
//    public void finishProgress(float progress) {
//        if(info.getVisibility()!=VISIBLE){
//            info.bringToFront();
//            info.setVisibility(VISIBLE);
//        }
//        info.setProgress(progress);
//
//    }

    private class ProInfo extends View {
        private float mCircle;
        private float mTextSize;

        private Paint mArcPaint;
        private Paint mTextPaint;
        private Paint mBackPaint;

        float mCenterX;
        float mCenterY;

        ValueAnimator valueAnimator;


        Matrix matrix = new Matrix();
        Bitmap bitmap;


        public ProInfo(Context context) {
            super(context);
            init();
        }

        private void init(){


           // bitmap =((BitmapDrawable) getContext().getResources().getDrawable(R.drawable.xyz)).getBitmap();

            mBackPaint = new Paint();
            mBackPaint.setColor(0x8a363434);
        }




//        void checkAnimation(){
//            if(valueAnimator ==null || !valueAnimator.isRunning()){
//                valueAnimator = ValueAnimator.ofFloat(0,1f);
//                valueAnimator.setDuration(5000);
//                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        int rotate = (int) (System.currentTimeMillis() / 3 % 360);
//                        matrix.reset();
//
//                        matrix.postScale(mCircle / bitmap.getHeight(), mCircle / bitmap.getHeight());
//
//                        matrix.postRotate(rotate, mCircle / 2, mCircle / 2);
//
//                        matrix.postTranslate(mCenterX - mCircle / 2, mCenterY - mCircle / 2);
//
//                        invalidate();
//                    }
//                });
//                valueAnimator.start();
//            }
//
//        }

        void setProgress(float progress){
            mProgress = progress;
            //checkAnimation();
            invalidate();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mCenterX=w/2;
            mCenterY=h/2;


            mCircle = Math.min(w,h)/1.2f;

            mTextSize = mCircle*0.144f;


            mArcPaint = new Paint();
            mArcPaint.setStyle(Paint.Style.STROKE);
            mArcPaint.setColor(0x8a363434);
            mArcPaint.setAntiAlias(true);

            mTextPaint = new Paint();
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.setColor(Color.GRAY);

        }



        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBackPaint);

            canvas.drawBitmap(bitmap, matrix, mArcPaint);
            String text = (int)(mProgress*100)+"%";
            canvas.drawText(text, mCenterX - mTextPaint.measureText(text) / 2, mCenterY + 0.368f * mTextSize, mTextPaint);
        }






    }

}

































































