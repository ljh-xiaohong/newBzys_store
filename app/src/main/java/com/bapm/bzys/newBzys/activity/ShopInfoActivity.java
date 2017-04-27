package com.bapm.bzys.newBzys.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys.base.BaseActivity;
import com.bapm.bzys.newBzys.model.Address;
import com.bapm.bzys.newBzys.network.DadanUrl;
import com.bapm.bzys.newBzys.network.HttpUtil;
import com.bapm.bzys.newBzys.network.function.interf.Function;
import com.bapm.bzys.newBzys.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys.util.ActivityManager;
import com.bapm.bzys.newBzys.util.DadanPreference;
import com.bapm.bzys.newBzys.widget.DadanArcDialog;
import com.bapm.bzys.newBzys.widget.wheel.OnWheelChangedListener;
import com.bapm.bzys.newBzys.widget.wheel.WheelView;
import com.bapm.bzys.newBzys.widget.wheel.adapters.ArrayWheelAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 店铺资料编辑与填写
 * Created by fs-ljh on 2017/4/11.
 */

public class ShopInfoActivity extends BaseActivity implements Function, OnWheelChangedListener, View.OnClickListener {
    @InjectView(R.id.shop_info_title)
    TextView shopInfoTitle;
    @InjectView(R.id.layout_title_bar)
    RelativeLayout layoutTitleBar;
    @InjectView(R.id.edt_shop_name)
    EditText edtShopName;
    @InjectView(R.id.edt_address)
    EditText edtAddress;
    @InjectView(R.id.layout_address)
    RelativeLayout layoutAddress;
    @InjectView(R.id.edt_long_address)
    EditText edtLongAddress;
    @InjectView(R.id.edt_phone)
    EditText edtPhone;
    @InjectView(R.id.btn_login)
    Button btnLogin;
    @InjectView(R.id.layout_oper)
    LinearLayout layoutOper;
    @InjectView(R.id.layout_form)
    LinearLayout layoutForm;
    Button btnConfirm;
    Button btnCencel;
    @InjectView(R.id.imgBtn_back)
    ImageButton imgBtnBack;
    @InjectView(R.id.tv_pwd)
    TextView tvPwd;
    @InjectView(R.id.choose_layout)
    RelativeLayout chooseLayout;
    @InjectView(R.id.iv_goods_type_arrow_down)
    ImageView ivGoodsTypeArrowDown;
    private FunctionManager manager;
    private DadanArcDialog loadDialog;
    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    private String[] mProvinceDatas;
    private String[] mProvinceIDs;
    private String mCurrentProviceName;
    private String mCurrentCityName;
    private String mCurrentDistrictName;
    private String mCurrentProviceID;
    private String mCurrentCityID;
    private String mCurrentDistrictID;
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    protected Map<String, String[]> mCityIDs = new HashMap<String, String[]>();
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
    protected Map<String, String[]> mDistrictIDs = new HashMap<String, String[]>();
    private View popupView;
    private PopupWindow window;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
        ButterKnife.inject(this);
        manager = this.init(this.getContext());
        manager.registFunClass(ShopInfoActivity.class);
        initView();
        initLisenter();
        initData();
    }

    private void initData() {
        loadDialog = new DadanArcDialog(this);
        loadDialog.setCancelable(false);
        Map<String, String> params = new HashMap<String, String>();
        manager.getAddress(params, this);
        if (!getIntent().getStringExtra("wherefrom").equals("RegistActivity")) {
            loadDialog.show();
            manager.getEnterpriseDetail(params, this);
        }
    }

    private void initView() {
        popupView = ShopInfoActivity.this.getLayoutInflater().inflate(R.layout.layout_popwindow, null);
        mViewProvince = (WheelView) popupView.findViewById(R.id.id_province);
        mViewCity = (WheelView) popupView.findViewById(R.id.id_city);
        mViewDistrict = (WheelView) popupView.findViewById(R.id.id_district);
        if (getIntent().getStringExtra("wherefrom").equals("RegistActivity")) {
            shopInfoTitle.setText("店铺资料填写");
            btnLogin.setText("进入店铺");
        } else {
            shopInfoTitle.setText("店铺资料编辑");
            btnLogin.setText("保存");
            imgBtnBack.setVisibility(View.VISIBLE);
        }
    }

    private void initLisenter() {
        layoutAddress.setOnClickListener(this);
        edtAddress.setOnClickListener(this);
        imgBtnBack.setOnClickListener(this);
        ivGoodsTypeArrowDown.setOnClickListener(this);
        edtAddress.setFocusable(false);
        mViewProvince.addChangingListener(this);
        // 添加change事件
        mViewCity.addChangingListener(this);
        // 添加change事件
        mViewDistrict.addChangingListener(this);
    }

    public void regist(View v) {
        loadDialog.show();
        JSONObject params = new JSONObject();
        try {
            params.put("CompanyName", edtShopName.getText().toString());
            params.put("CompanyAddress", edtLongAddress.getText().toString());
            params.put("Telephone", edtPhone.getText().toString());
            params.put("ProvinceID", mCurrentProviceID);
            params.put("CityID", mCurrentCityID);
            params.put("AreaID", mCurrentDistrictID);
            manager.commitStoreDetail(params, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void login(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public FunctionManager init(Context context) {
        return new FunctionManager(context);
    }

    @Override
    public void onSuccess(int requstCode, JSONObject result) {
        switch (requstCode) {
            case DadanUrl.USER_LOGIN_AGAIN_REQUEST_CODE:
                if (result.optString("LogionCode").equals("1")) {
                    DadanPreference.getInstance(this).setTicket(result.optString("Ticket"));
                    initData();
                }else if(result.optString("LogionCode").equals("-1")){
                    Intent intent=new Intent(this,LoginActivity.class);
                    intent.putExtra("LogionCode","-1");
                    startActivity(intent);
                    ActivityManager.getInstance().finishAllActivity();
                }
                break;
            case DadanUrl.STORE_DETAIL_COMMIT_CODE:
                try {
                    Toast.makeText(ShopInfoActivity.this, result.getString("Message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (getIntent().getStringExtra("wherefrom").equals("RegistActivity")) {
                    Intent intent = new Intent(this, MainActivity.class);
                    DadanPreference.getInstance(this).setString("CompanyName", edtShopName.getText().toString());
                    startActivity(intent);
                }
                this.finish();
                break;
            case DadanUrl.ENTERPRISE_DETAIL_URL_CODE:
                try {
                    edtShopName.setText(result.getString("CompanyName"));
                    edtAddress.setText(result.getString("ProvinceName") + "省" + result.getString("CityName") + "市" + result.getString("AreaName") + "区");
                    edtLongAddress.setText(result.getString("CompanyAddress"));
                    edtPhone.setText(result.getString("TelePhone"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case DadanUrl.GET_ADDRESS_CODE:
                Log.e("result", result.toString());
                try {
                    String object = result.getString("Value");
                    Gson gson = new Gson();
                    Address student = gson.fromJson(object, Address.class);
                    mProvinceDatas = new String[student.getJsonData().size()];
                    mProvinceIDs = new String[student.getJsonData().size()];
                    for (int i = 0; i < student.getJsonData().size(); i++) {
                        mProvinceDatas[i] = student.getJsonData().get(i).getProvinceName();
                        mProvinceIDs[i] = student.getJsonData().get(i).getProvinceId() + "";
                        List<Address.JsonDataBean.CitysBean> cityList = student.getJsonData().get(i).getCitys();
                        String[] cityNames = new String[cityList.size()];
                        String[] cityIDs = new String[cityList.size()];
                        for (int j = 0; j < cityList.size(); j++) {
                            // 遍历省下面的所有市的数据
                            cityNames[j] = cityList.get(j).getCityName();
                            cityIDs[j] = cityList.get(j).getCityId() + "";
                            List<Address.JsonDataBean.CitysBean.AreasBean> districtList = cityList.get(j).getAreas();
                            String[] distrinctNameArray = new String[districtList.size()];
                            String[] distrinctIDArray = new String[districtList.size()];
                            Address.JsonDataBean.CitysBean.AreasBean[] distrinctArray = new Address.JsonDataBean.CitysBean.AreasBean[districtList.size()];
                            for (int k = 0; k < districtList.size(); k++) {
                                // 遍历市下面所有区/县的数据
                                distrinctArray[k] = districtList.get(k);
                                distrinctNameArray[k] = districtList.get(k).getAreaName();
                                distrinctIDArray[k] = districtList.get(k).getAreaId() + "";
                            }
                            // 市-区/县的数据，保存到mDistrictDatasMap
                            mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                            mDistrictIDs.put(cityNames[j], distrinctIDArray);
                        }
                        // 省-市的数据，保存到mCitisDatasMap
                        mCitisDatasMap.put(student.getJsonData().get(i).getProvinceName(), cityNames);
                        mCityIDs.put(student.getJsonData().get(i).getProvinceName(), cityIDs);
                    }
                    if (student.getJsonData().size() > 0) {
                        mCurrentProviceName = student.getJsonData().get(0).getProvinceName();
                        mCurrentProviceID = student.getJsonData().get(0).getProvinceId() + "";
                        List<Address.JsonDataBean.CitysBean> cityList = student.getJsonData().get(0).getCitys();
                        if (cityList != null && !cityList.isEmpty()) {
                            mCurrentCityName = cityList.get(0).getCityName();
                            List<Address.JsonDataBean.CitysBean.AreasBean> districtList = cityList.get(0).getAreas();
                            if (districtList != null && !districtList.isEmpty()) {
                                mCurrentDistrictName = districtList.get(0).getAreaName();
                            }
                        }
                    }
                    ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<String>(ShopInfoActivity.this, mProvinceDatas);
                    arrayWheelAdapter.setTextColor(getResources().getColor(R.color.province_line_border));
                    mViewProvince.setViewAdapter(arrayWheelAdapter);
                    updateCities();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        loadDialog.dismiss();
    }

    @Override
    public void onSuccess(int requstCode, JSONArray result) {

    }

    @Override
    public void onFaile(int requestCode, int status, String msg) {
        loadDialog.dismiss();
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        if(requestCode== HttpUtil.ST_ACCOUNT_OTHER_LOGIN_FAILE){
            Map<String, String> params = new HashMap<String, String>();
            params.put("DEVICE_ID", ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId());
            manager.loginAgain(params, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unregistFunctionClass(ShopInfoActivity.class);
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentDistrictID = mDistrictIDs.get(mCurrentCityName)[newValue];
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_address:
            case R.id.edt_address:
            case R.id.iv_goods_type_arrow_down:
                initPopwindow();
                break;
            case R.id.btn_confirm:
                edtAddress.setText(mCurrentProviceName + "省" + mCurrentCityName + "市"
                        + mCurrentDistrictName + "区");
                window.dismiss();
                break;
            case R.id.btn_cencel:
                window.dismiss();
                break;
            case R.id.imgBtn_back:
               this.finish();
                break;
            default:
                break;
        }
    }

    private void initPopwindow() {
        // 创建PopupWindow对象，指定宽度和高度
        window = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight()/3);

        //设置动画
        window.setAnimationStyle(R.style.popup_window_anim);
        // 设置背景颜色
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
        //设置可以获取焦点
        window.setFocusable(true);
        //设置可以触摸弹出框以外的区域
        window.setOutsideTouchable(true);
        // 更新popupwindow的状态
        window.update();
        // 以下拉的方式显示，并且可以设置显示的位置
        window.showAtLocation(findViewById(R.id.choose_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        btnConfirm = (Button) popupView.findViewById(R.id.btn_confirm);
        btnCencel = (Button) popupView.findViewById(R.id.btn_cencel);
        btnConfirm.setOnClickListener(this);
        btnCencel.setOnClickListener(this);
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        mCurrentCityID = mCityIDs.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);
        if (areas == null) {
            areas = new String[]{""};
        }
        ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<String>(ShopInfoActivity.this, areas);
        arrayWheelAdapter.setTextColor(getResources().getColor(R.color.province_line_border));
        mViewDistrict.setViewAdapter(arrayWheelAdapter);
        mViewDistrict.setCurrentItem(0);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        mCurrentProviceID = mProvinceIDs[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<String>(ShopInfoActivity.this, cities);
        arrayWheelAdapter.setTextColor(getResources().getColor(R.color.province_line_border));
//        arrayWheelAdapter.setItemResource(getResources().getColor(R.color.province_line_border));
        mViewCity.setViewAdapter(arrayWheelAdapter);
        mViewCity.setCurrentItem(0);
        updateAreas();
    }


}
