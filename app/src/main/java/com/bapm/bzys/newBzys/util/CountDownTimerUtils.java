package com.bapm.bzys.newBzys.util;


import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;

import com.bapm.bzys.newBzys.R;

public class CountDownTimerUtils extends CountDownTimer {
    private Button btn;

    /**
     * @param textView          The TextView
     *
     *
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receiver
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimerUtils(Button btn, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.btn = btn;
    }
    @Override
    public void onTick(long millisUntilFinished) {
        btn.setClickable(false); //设置不可点击
        btn.setText(millisUntilFinished / 1000 + "s后重新获取验证码");  //设置倒计时时间
        btn.setBackgroundResource(R.drawable.btn_timer_verify_code_press); //设置按钮为灰色，这时是不能点击的
        /**
         * 超链接 URLSpan
         * 文字背景颜色 BackgroundColorSpan
         * 文字颜色 ForegroundColorSpan
         * 字体大小 AbsoluteSizeSpan
         * 粗体、斜体 StyleSpan
         * 删除线 StrikethroughSpan
         * 下划线 UnderlineSpan
         * 图片 ImageSpan
         * http://blog.csdn.net/ah200614435/article/details/7914459
         */
        SpannableString spannableString = new SpannableString(btn.getText().toString());  //获取按钮上的文字
        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
        /**
         * public void setSpan(Object what, int start, int end, int flags) {
         * 主要是start跟end，start是起始位置,无论中英文，都算一个。
         * 从0开始计算起。end是结束位置，所以处理的文字，包含开始位置，但不包含结束位置。
         */
        spannableString.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将倒计时的时间设置为红色
        btn.setText(spannableString);
    }

    @Override
    public void onFinish() {
    	btn.setText("获取验证码");
    	btn.setClickable(true);//重新获得点击
    	btn.setBackgroundResource(R.drawable.btn_timer_verify_code_normal);  //还原背景色
    }
}