package com.bapm.bzys.newBzys_store.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.base.BaseActivity;
import com.bapm.bzys.newBzys_store.network.DadanUrl;
import com.bapm.bzys.newBzys_store.network.function.interf.Function;
import com.bapm.bzys.newBzys_store.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys_store.util.ActivityManager;
import com.bapm.bzys.newBzys_store.util.CommonUtil;
import com.bapm.bzys.newBzys_store.util.Constants;
import com.bapm.bzys.newBzys_store.util.CustomToast;
import com.bapm.bzys.newBzys_store.util.DadanPreference;
import com.bapm.bzys.newBzys_store.widget.DadanArcDialog;
import com.bapm.bzys.newBzys_store.widget.dialog.MyDialog;
import com.bapm.bzys.newBzys_store.widget.dialog.MyDialogListener;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements Function {
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.ed_phone)
    EditText edPhone;
    @BindView(R.id.name_remove)
    ImageView nameRemove;
    @BindView(R.id.layout_phone)
    AutoRelativeLayout layoutPhone;
    @BindView(R.id.tv_pwd)
    TextView tvPwd;
    @BindView(R.id.ed_pwd)
    EditText edPwd;
    @BindView(R.id.psw_remove)
    ImageView pswRemove;
    @BindView(R.id.display_psw)
    ImageView displayPsw;
    @BindView(R.id.high_psw)
    ImageView highPsw;
    @BindView(R.id.layout_pwd)
    AutoRelativeLayout layoutPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_forget)
    Button tvForget;
    @BindView(R.id.tv_regist)
    Button tvRegist;
    @BindView(R.id.layout_next)
    AutoRelativeLayout layoutNext;
    @BindView(R.id.layout_oper)
    AutoLinearLayout layoutOper;
    @BindView(R.id.layout_form)
    AutoLinearLayout layoutForm;
    private FunctionManager manager;
    private EditText ed_phone;
    private EditText ed_pwd;
    private DadanArcDialog loadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra("LogionCode") != null && getIntent().getStringExtra("LogionCode").equals("-1")) {
            new MyDialog(this).callback(MyDialog.TYPE_INFO, "该账号已在其他设备登录，如果不是本人操作，请您及时修改密码。", new MyDialogListener() {
                @Override
                public void callback(String[] array) {

                }
            }, "确定", null, null);
        }
        manager = this.init(this.getContext());
        manager.registFunClass(LoginActivity.class);
        ed_phone = (EditText) findViewById(R.id.ed_phone);
        ed_pwd = (EditText) findViewById(R.id.ed_pwd);
        loadDialog = new DadanArcDialog(this);
        loadDialog.setCancelable(false);

    }

    public void login(View v) {
        if (CommonUtil.isNull(ed_phone.getText().toString())) {
            CustomToast.showToast(this, "账号不能为空");
        } else if (CommonUtil.isNull(ed_pwd.getText().toString())) {
            CustomToast.showToast(this, "密码不能为空");
        } else {
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
//         CustomToast.showToast(this, msg);
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
                ActivityManager.getInstance().finishAllActivity();
            } else {
                CustomToast.showToast(this, message);
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
