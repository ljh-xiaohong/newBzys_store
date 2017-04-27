package com.bapm.bzys.newBzys.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys.base.BaseActivity;
import com.bapm.bzys.newBzys.network.DadanUrl;
import com.bapm.bzys.newBzys.network.HttpUtil;
import com.bapm.bzys.newBzys.network.function.interf.Function;
import com.bapm.bzys.newBzys.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys.util.ActivityManager;
import com.bapm.bzys.newBzys.util.DadanPreference;
import com.bapm.bzys.newBzys.widget.DadanArcDialog;
import com.bapm.bzys.newBzys.widget.dialog.CallDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by fs-ljh on 2017/4/7.
 * 帮助与反馈
 */

public class ActivityBookInformation extends BaseActivity implements Function, View.OnClickListener {
    @InjectView(R.id.edt_book_information)
    EditText edtBookInformation;//编辑框
    @InjectView(R.id.layout_edt)
    RelativeLayout layoutEdt;
    @InjectView(R.id.imgBtn_call)
    ImageButton imgBtnCall;//电话按钮
    @InjectView(R.id.tv_cancel)
    TextView tvCancel;//取消按钮
    @InjectView(R.id.tv_commit)
    TextView tvCommit;//提交按钮
    @InjectView(R.id.tv_counts)
    TextView tvCounts;
    private FunctionManager manager;
    private DadanArcDialog loadDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_information);
        ButterKnife.inject(this);
        manager = this.init(this.getContext());
        manager.registFunClass(ActivityBookInformation.class);
        loadDialog = new DadanArcDialog(this);
        loadDialog.setCancelable(false);
        initLisenter();
    }

    private void initLisenter() {
        edtBookInformation.setOnClickListener(this);
        layoutEdt.setOnClickListener(this);
        imgBtnCall.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvCommit.setOnClickListener(this);
        edtBookInformation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCounts.setText(s.length()+"");
                if (s.length()>0){
                    tvCommit.setClickable(true);
                    tvCommit.setTextColor(getResources().getColor(R.color.white));
                }else{
                    tvCommit.setClickable(false);
                    tvCommit.setTextColor(getResources().getColor(R.color.commit_orange));
                }
            }
        });
    }

    @Override
    public FunctionManager init(Context context) {
        return new FunctionManager(context);
    }

    @Override
    public void onSuccess(int requstCode, JSONObject result) {
        switch (requstCode){
            case DadanUrl.USER_LOGIN_AGAIN_REQUEST_CODE:
                if (result.optString("LogionCode").equals("1")) {
                    DadanPreference.getInstance(this).setTicket(result.optString("Ticket"));
                }else if(result.optString("LogionCode").equals("-1")){
                    Intent intent=new Intent(this,LoginActivity.class);
                    intent.putExtra("LogionCode","-1");
                    startActivity(intent);
                    ActivityManager.getInstance().finishAllActivity();
                }
                break;
        }
        loadDialog.dismiss();
        Toast.makeText(this,"感谢您的宝贵意见与建议，我们会努力做到更好。",Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    public void onSuccess(int requstCode, JSONArray result) {
        Log.e("JSONArray",result+"");
    }

    @Override
    public void onFaile(int requestCode, int status, String msg) {
        loadDialog.dismiss();
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
        if(requestCode== HttpUtil.ST_ACCOUNT_OTHER_LOGIN_FAILE){
            Map<String, String> params = new HashMap<String, String>();
            params.put("DEVICE_ID", ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId());
            manager.loginAgain(params, this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edt_book_information:
            case R.id.layout_edt:
                edtBookInformation.setFocusable(true);
                edtBookInformation.requestFocus();
                InputMethodManager imm= (InputMethodManager) edtBookInformation.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                break;
            case R.id.imgBtn_call:
               new CallDialog.Builder(this).setTitle("拨打电话").create().show();
                break;
            case R.id.tv_cancel:
                this.finish();
                break;
            case R.id.tv_commit:
                loadDialog.show();
                JSONObject params = new JSONObject();
                try {
                    params.put("Feedback", edtBookInformation.getText().toString());
                    manager.commitCompanyFeedback(params,this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unregistFunctionClass(ActivityBookInformation.class);
    }
}
