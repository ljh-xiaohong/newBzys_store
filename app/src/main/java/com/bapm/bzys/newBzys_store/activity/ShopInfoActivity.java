package com.bapm.bzys.newBzys_store.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.base.BaseActivity;
import com.bapm.bzys.newBzys_store.model.Address;
import com.bapm.bzys.newBzys_store.network.DadanUrl;
import com.bapm.bzys.newBzys_store.network.HttpUtil;
import com.bapm.bzys.newBzys_store.network.function.interf.Function;
import com.bapm.bzys.newBzys_store.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys_store.util.ActivityManager;
import com.bapm.bzys.newBzys_store.util.CommonUtil;
import com.bapm.bzys.newBzys_store.util.CustomToast;
import com.bapm.bzys.newBzys_store.util.DadanPreference;
import com.bapm.bzys.newBzys_store.widget.DadanArcDialog;
import com.bapm.bzys.newBzys_store.widget.wheel.OnWheelChangedListener;
import com.bapm.bzys.newBzys_store.widget.wheel.WheelView;
import com.bapm.bzys.newBzys_store.widget.wheel.adapters.ArrayWheelAdapter;
import com.google.gson.Gson;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 店铺资料编辑与填写
 * Created by fs-ljh on 2017/4/11.
 */

public class ShopInfoActivity extends BaseActivity implements Function, OnWheelChangedListener, View.OnClickListener {
    @BindView(R.id.tv_store_name)
    TextView tvStoreName;
    @BindView(R.id.edt_shop_name)
    EditText edtShopName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.edt_address)
    EditText edtAddress;
    @BindView(R.id.iv_goods_type_arrow_down)
    ImageView ivGoodsTypeArrowDown;
    @BindView(R.id.layout_address)
    AutoRelativeLayout layoutAddress;
    @BindView(R.id.edt_long_address)
    EditText edtLongAddress;
    @BindView(R.id.tv_pwd)
    TextView tvPwd;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.layout_oper)
    AutoLinearLayout layoutOper;
    @BindView(R.id.layout_form)
    AutoLinearLayout layoutForm;
    @BindView(R.id.imgBtn_back)
    ImageButton imgBtnBack;
    @BindView(R.id.shop_info_title)
    TextView shopInfoTitle;
    @BindView(R.id.layout_title_bar)
    AutoRelativeLayout layoutTitleBar;
    @BindView(R.id.choose_layout)
    AutoRelativeLayout chooseLayout;
    private FunctionManager manager;
    private DadanArcDialog loadDialog;
    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    private String[] mProvinceDatas;
    private int[] mProvinceIDs;
    private String mCurrentProviceName;
    private String mCurrentCityName;
    private String mCurrentDistrictName;
    private int mCurrentProviceID;
    private int mCurrentCityID;
    private int mCurrentDistrictID;
    private int mChangeProviceID;
    private int mChangeCityID;
    private int mChangeDistrictID;
    private int tagProviceID = 0;

    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    protected Map<String, int[]> mCityIDs = new HashMap<String, int[]>();
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
    protected Map<String, int[]> mDistrictIDs = new HashMap<String, int[]>();
    private View popupView;
    private PopupWindow window;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
        ButterKnife.bind(this);
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
        if (CommonUtil.isNull(edtShopName.getText().toString())) {
            CustomToast.showToast(this, "名称不能为空");
        } else if (CommonUtil.isNull(edtAddress.getText().toString())) {
            CustomToast.showToast(this, "请选择地址");
        } else if (CommonUtil.isNull(edtPhone.getText().toString())) {
            CustomToast.showToast(this, "电话不能为空");
        } else if (edtPhone.getText().toString().length() == 11 && !CommonUtil.checkPhoneNum(edtPhone.getText().toString())) {
            CustomToast.showToast(this, "请输入正确的号码");
        } else {
            loadDialog.show();
            JSONObject params = new JSONObject();
            try {
                params.put("CompanyName", edtShopName.getText().toString());
                params.put("CompanyAddress", edtLongAddress.getText().toString());
                params.put("Telephone", edtPhone.getText().toString());
                if (tagProviceID != mCurrentProviceID) {
                    params.put("ProvinceID", mChangeProviceID);
                    params.put("CityID", mChangeCityID);
                    params.put("AreaID", mChangeDistrictID);
                } else {
                    params.put("ProvinceID", mCurrentProviceID);
                    params.put("CityID", mCurrentCityID);
                    params.put("AreaID", mCurrentDistrictID);
                }
                manager.commitStoreDetail(params, this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void login(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        ActivityManager.getInstance().finishAllActivity();
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
                } else if (result.optString("LogionCode").equals("-1")) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("LogionCode", "-1");
                    startActivity(intent);
                    ActivityManager.getInstance().finishAllActivity();
                }
                break;
            case DadanUrl.STORE_DETAIL_COMMIT_CODE:
                btnLogin.setEnabled(true);
                if (result.optInt("Code") == 1) {
                    if (getIntent().getStringExtra("wherefrom").equals("RegistActivity")) {
                        Intent intent = new Intent(this, MainActivity.class);
                        DadanPreference.getInstance(this).setString("CompanyName", edtShopName.getText().toString());
                        startActivity(intent);
                    }
                    this.finish();
                }
                CustomToast.showToast(ShopInfoActivity.this, result.optString("Message"));

                break;
            case DadanUrl.ENTERPRISE_DETAIL_URL_CODE:
                try {
                    edtShopName.setText(result.getString("CompanyName"));
                    if (result.getString("ProvinceName").equals("钓鱼岛")) {
                        edtAddress.setText(result.getString("ProvinceName"));
                        mCurrentProviceID = result.optInt("ProvinceID");
                        tagProviceID = mCurrentProviceID;
                        mCurrentCityID = 0;
                        mCurrentDistrictID = 0;
                    } else {
                        edtAddress.setText(result.getString("ProvinceName") + "省" + result.getString("CityName") + "市" + result.getString("AreaName") + "区");
                        mCurrentProviceID = result.optInt("ProvinceID");
                        tagProviceID = mCurrentProviceID;
                        mCurrentCityID = result.optInt("CityID");
                        mCurrentDistrictID = result.optInt("AreaID");
                    }

                    edtLongAddress.setText(result.getString("CompanyAddress"));
                    edtPhone.setText(result.getString("TelePhone"));
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
        if (requestCode == HttpUtil.ST_ACCOUNT_OTHER_LOGIN_FAILE || requestCode == 233) {
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
            updateDistrict(newValue);
        }
    }

    private void updateDistrict(int newValue) {
        mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
        mChangeDistrictID = mDistrictIDs.get(mCurrentCityName)[newValue];
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_address:
            case R.id.edt_address:
            case R.id.iv_goods_type_arrow_down:
                layoutAddress.setEnabled(false);
                edtAddress.setEnabled(false);
                ivGoodsTypeArrowDown.setEnabled(false);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ShopInfoActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                initPopwindow();
                break;
            case R.id.btn_confirm:
                if (mCurrentProviceName.equals("钓鱼岛")) {
                    edtAddress.setText(mCurrentProviceName);
                } else {
                    edtAddress.setText(mCurrentProviceName + "省" + mCurrentCityName + "市"
                            + mCurrentDistrictName + "区");
                }

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

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;

    private void initPopwindow() {
//        Map<String, String> params = new HashMap<String, String>();
//        loadDialog.show();
//        manager.getAddress(params, this);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);
        } else {
            getData();
        }


        // 创建PopupWindow对象，指定宽度和高度
        window = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight() / 3);

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
        Button btnConfirm = (Button) popupView.findViewById(R.id.btn_confirm);
        Button btnCencel = (Button) popupView.findViewById(R.id.btn_cencel);
        btnConfirm.setOnClickListener(this);
        btnCencel.setOnClickListener(this);
        layoutAddress.setEnabled(true);
        edtAddress.setEnabled(true);
        ivGoodsTypeArrowDown.setEnabled(true);
    }

    public void getData() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuilder builder = null;
        try {
            isr = new InputStreamReader(getAssets().open("data.json"), "UTF-8");
            br = new BufferedReader(isr);
            String line;
            builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        Gson gson = new Gson();
        Address student = gson.fromJson(builder.toString(), Address.class);
        mProvinceDatas = new String[student.getJsonData().size()];
        mProvinceIDs = new int[student.getJsonData().size()];
        for (int i = 0; i < student.getJsonData().size(); i++) {
            mProvinceDatas[i] = student.getJsonData().get(i).getProvinceName();
            mProvinceIDs[i] = student.getJsonData().get(i).getProvinceId();
            List<Address.JsonDataBean.CitysBean> cityList = student.getJsonData().get(i).getCitys();
            String[] cityNames = new String[cityList.size()];
            int[] cityIDs = new int[cityList.size()];
            for (int j = 0; j < cityList.size(); j++) {
                // 遍历省下面的所有市的数据
                cityNames[j] = cityList.get(j).getCityName();
                cityIDs[j] = cityList.get(j).getCityId();
                List<Address.JsonDataBean.CitysBean.AreasBean> districtList = cityList.get(j).getAreas();
                String[] distrinctNameArray = new String[districtList.size()];
                int[] distrinctIDArray = new int[districtList.size()];
                Address.JsonDataBean.CitysBean.AreasBean[] distrinctArray = new Address.JsonDataBean.CitysBean.AreasBean[districtList.size()];
                for (int k = 0; k < districtList.size(); k++) {
                    // 遍历市下面所有区/县的数据
                    distrinctArray[k] = districtList.get(k);
                    distrinctNameArray[k] = districtList.get(k).getAreaName();
                    distrinctIDArray[k] = districtList.get(k).getAreaId();
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
            mChangeProviceID = student.getJsonData().get(0).getProvinceId();
            tagProviceID = mChangeProviceID;
            List<Address.JsonDataBean.CitysBean> cityList = student.getJsonData().get(0).getCitys();
            if (cityList != null && !cityList.isEmpty()) {
                mCurrentCityName = cityList.get(0).getCityName();
                mChangeCityID = cityList.get(0).getCityId();
                List<Address.JsonDataBean.CitysBean.AreasBean> districtList = cityList.get(0).getAreas();
                if (districtList != null && !districtList.isEmpty()) {
                    mCurrentDistrictName = districtList.get(0).getAreaName();
                    mChangeDistrictID = districtList.get(0).getAreaId();
                } else {
                    mCurrentDistrictName = "";
                    mChangeDistrictID = 0;
                }
            } else {
                mCurrentCityName = "";
                mChangeCityID = 0;
            }
        }
        ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<String>(ShopInfoActivity.this, mProvinceDatas);
        arrayWheelAdapter.setTextColor(getResources().getColor(R.color.province_line_border));
        mViewProvince.setViewAdapter(arrayWheelAdapter);
        if (edtAddress.getText().toString().equals("")) {
            mViewProvince.setCurrentItem(0);
        } else {
            for (int i = 0; i < mProvinceDatas.length; i++) {
                if (edtAddress.getText().toString().equals("钓鱼岛")) {
                    mViewProvince.setCurrentItem(34);
                    return;
                } else {
                    if (mProvinceDatas[i].equals(edtAddress.getText().toString().substring(0, edtAddress.getText().toString().indexOf("省")))) {
                        mViewProvince.setCurrentItem(i);
                    }
                }

            }
        }
        currentCities();
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        mChangeCityID = mCityIDs.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);
        if (areas.length == 0) {
            areas = new String[]{""};
        }
        ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<String>(ShopInfoActivity.this, areas);
        arrayWheelAdapter.setTextColor(getResources().getColor(R.color.province_line_border));
        mViewDistrict.setViewAdapter(arrayWheelAdapter);
        mViewDistrict.setCurrentItem(0);
        mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
        mChangeDistrictID = mDistrictIDs.get(mCurrentCityName)[0];

    }

    private void currentAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        mChangeCityID = mCityIDs.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);
        if (areas.length == 0) {
            areas = new String[]{""};
        }
        ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<String>(ShopInfoActivity.this, areas);
        arrayWheelAdapter.setTextColor(getResources().getColor(R.color.province_line_border));
        mViewDistrict.setViewAdapter(arrayWheelAdapter);
        if (edtAddress.getText().toString().equals("")) {
            mViewDistrict.setCurrentItem(0);
        } else {
            for (int i = 0; i < areas.length; i++) {
                if (areas[i].equals(edtAddress.getText().toString().substring(edtAddress.getText().toString().indexOf("市") + 1, edtAddress.getText().toString().indexOf("区")))) {
                    mViewDistrict.setCurrentItem(i);
                }
            }
        }
        mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
        mChangeDistrictID = mDistrictIDs.get(mCurrentCityName)[0];
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        mChangeProviceID = mProvinceIDs[pCurrent];
        tagProviceID = mChangeProviceID;
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities.length == 0) {
            cities = new String[]{""};
        }
        ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<String>(ShopInfoActivity.this, cities);
        arrayWheelAdapter.setTextColor(getResources().getColor(R.color.province_line_border));
        mViewCity.setViewAdapter(arrayWheelAdapter);
        mViewCity.setCurrentItem(0);
        if (!cities[0].equals("")) {
            updateAreas();
        } else {
            ArrayWheelAdapter<String> arrayWheelAdapters = new ArrayWheelAdapter<String>(ShopInfoActivity.this, cities);
            arrayWheelAdapters.setTextColor(getResources().getColor(R.color.province_line_border));
            mViewDistrict.setViewAdapter(arrayWheelAdapters);
        }

    }

    private void currentCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        mChangeProviceID = mProvinceIDs[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities.length == 0) {
            cities = new String[]{""};
        }
        ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<String>(ShopInfoActivity.this, cities);
        arrayWheelAdapter.setTextColor(getResources().getColor(R.color.province_line_border));
//        arrayWheelAdapter.setItemResource(getResources().getColor(R.color.province_line_border));
        mViewCity.setViewAdapter(arrayWheelAdapter);
        if (edtAddress.getText().toString().equals("")) {
            mViewCity.setCurrentItem(0);
        } else {
            for (int i = 0; i < cities.length; i++) {
                if (cities[i].equals(edtAddress.getText().toString().substring(edtAddress.getText().toString().indexOf("省") + 1, edtAddress.getText().toString().indexOf("市")))) {
                    mViewCity.setCurrentItem(i);
                }
            }
        }

        currentAreas();
    }


}
