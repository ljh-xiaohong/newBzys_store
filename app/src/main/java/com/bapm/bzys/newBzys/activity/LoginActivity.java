package com.bapm.bzys.newBzys.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys.base.BaseActivity;
import com.bapm.bzys.newBzys.network.DadanUrl;
import com.bapm.bzys.newBzys.network.HttpUtil;
import com.bapm.bzys.newBzys.network.function.interf.Function;
import com.bapm.bzys.newBzys.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys.util.Constants;
import com.bapm.bzys.newBzys.util.DadanPreference;
import com.bapm.bzys.newBzys.widget.DadanArcDialog;
import com.bapm.bzys.newBzys.widget.dialog.MyDialog;
import com.bapm.bzys.newBzys.widget.dialog.MyDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements Function {
    @InjectView(R.id.name_remove)
    ImageView nameRemove;
    @InjectView(R.id.psw_remove)
    ImageView pswRemove;
    @InjectView(R.id.display_psw)
    ImageView displayPsw;
    @InjectView(R.id.high_psw)
    ImageView highPsw;
    private FunctionManager manager;
    private EditText ed_phone;
    private EditText ed_pwd;
    private DadanArcDialog loadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        if (getIntent().getStringExtra("LogionCode")!=null&&getIntent().getStringExtra("LogionCode").equals("-1")){
            new MyDialog(this).callback(MyDialog.TYPE_INFO, "该账号已在其他设备登录，如果不是本人操作，请您及时修改密码。", new MyDialogListener() {
                @Override
                public void callback(String[] array) {

                }
            },"确定",null,null);
        }
        manager = this.init(this.getContext());
        manager.registFunClass(LoginActivity.class);
        ed_phone = (EditText) findViewById(R.id.ed_phone);
        ed_pwd = (EditText) findViewById(R.id.ed_pwd);
        loadDialog = new DadanArcDialog(this);
        loadDialog.setCancelable(false);

    }

    public void login(View v) {
        loadDialog.show();
        loadDialog.setTitle("正在登录中...");
        JSONObject params = new JSONObject();
        try {
            params.put("Phone", ed_phone.getText().toString());
            params.put("Password", ed_pwd.getText().toString());
            params.put("DEVICE_ID", ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId());
            manager.login(params, this);
        } catch (JSONException e) {
        }
    }

    public void back(View v) {
        this.finish();
    }

    @Override
    public FunctionManager init(Context context) {
        return new FunctionManager(context);
    }

    @Override
    public void onSuccess(int requstCode, JSONObject result) {
        loadDialog.dismiss();
        switch (requstCode) {
            case DadanUrl.USER_LOGIN_REQUEST_CODE: {
                handleLogin(result);
            }
            default:
                break;
        }
    }

    public void forget(View v) {
        Intent intent = new Intent(this, ActivityForgetPwd.class);
        startActivity(intent);
    }

    public void regist(View v) {
        Intent intent = new Intent(this, RegistActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFaile(int requestCode, int status, String msg) {
        loadDialog.dismiss();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.i(LoginActivity.class.toString(), msg);
    }

    public void handleLogin(JSONObject result) {
        try {
            int loginCode = result.optInt(Constants.LOGIONCODE_KEY);
            String message = result.getString(Constants.MESSAGE_KEY);
            if (loginCode == Constants.NETWORK_SUCCESS) {
                String ticket = result.optString(Constants.TICKET_KEY);
                DadanPreference.getInstance(this).setTicket(ticket);
                Intent mainIntent = new Intent(this.getContext(), MainActivity.class);
                startActivity(mainIntent);
                this.finish();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unregistFunctionClass(LoginActivity.class);
    }

    @Override
    public void onSuccess(int requstCode, JSONArray result) {
        loadDialog.dismiss();
    }

    @OnClick({R.id.name_remove, R.id.psw_remove, R.id.display_psw, R.id.high_psw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.name_remove:
                ed_phone.setText("");
                break;
            case R.id.psw_remove:
                ed_pwd.setText("");
                break;
            case R.id.display_psw:
                highPsw.setVisibility(View.VISIBLE);
                ed_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ed_pwd.setSelection(ed_pwd.getText().length());
                displayPsw.setVisibility(View.GONE);
                break;
            case R.id.high_psw:
                displayPsw.setVisibility(View.VISIBLE);
                ed_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ed_pwd.setSelection(ed_pwd.getText().length());
                highPsw.setVisibility(View.GONE);
                break;
        }
    }
}
