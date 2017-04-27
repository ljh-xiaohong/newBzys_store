package com.bapm.bzys.newBzys.network;

public class DadanUrl {
	public static final int USER_LOGIN_REQUEST_CODE    = 200;
	public static final int GET_CODE_REQUEST_CODE      = 201;
	public static final int STORE_RESIT_REQUEST_CODE   = 202;
	public static final int USER_PUTRESET_REQUEST_CODE = 203;
	public static final int VALIDATE_CODE_REQUEST_CODE = 204;
	public static final int USER_CHILD_LIST_URL_CODE   = 205;
	public static final int USER_CHILD_ADD_URL_CODE    = 206;
	public static final int USER_CHILD_DEL_URL_CODE = 207;
	public static final int GOODS_TYPE_LIST_URL_CODE = 208;
	public static final int GOODS_DIC_STATUS_URL_CODE = 217;
	public static final int GOODS_TYPE_ADD_OR_UPDATE_URL_CODE=209;
	public static final int GOODS_LIST_URL_CODE = 210;
	public static final int GOODS_ADD_OR_UPDATE_URL_CODE=211;
	public static final int GOODS_TYPE_DEL_URL_CODE=212;
	public static final int ADVER_LIST_URL_CODE=213;
	public static final int ADVER_DIC_URL_CODE=214;
	public static final int ADVER_ADD_OR_UPDATE_URL_CODE=215;
	public static final int ADVER_DEL_URL_CODE=216;
	public static final int STORE_REGIST_URL_CODE=217;
	public static final int VALIDATE_CODE_URL_CODE = 218;
	public static final int USER_RESET_PWD_URL_CODE = 219;
	public static final int USER_VALIDATE_URL_CODE = 220;
	public static final int QI_NIU_TOKEN_URL_CODE=221;
	public static final int GOODS_TYPE_SORT_CHANGE_URL_CODE=222;
	public static final int EXIT_URL_CODE=223;
	public static final int USER_CHILD_DETAIL_URL_CODE=224;
	public static final int GOODS_SORT_CHANGE_URL_CODE=225;
	public static final int GOODS_DELETE_URL_CODE = 226;
	public static final int GOODS_DETAIL_URL_CODE = 227;
//	public static final int STORE_DETAIL_URL_CODE=228;
    //1.6版本
    public static final int COMPANYFEEDBACK_CODE=229;
    public static final int STORE_DETAIL_COMMIT_CODE=230;
    public static final int GET_ADDRESS_CODE=231;
    public static final int GET_ADVERT_LIST_CODE=232;
    public static final int ENTERPRISE_DETAIL_URL_CODE=233;
    public static final int GET_ORDER_LIST_CODE=234;
    public static final int ORDER_STATUS_CHANGE_CODE=235;
    public static final int ORDER_RECEIPT_CODE=236;
    public static final int COMMODITY_STATUS_CHANGE_CODE=237;
    public static final int USER_LOGIN_AGAIN_REQUEST_CODE    = 238;

//        public static final String BASE_ONE_URL="http://mapi.bzys.cn/api/";
//    public static final String BASE_ONE_URL="https://dev.bzys.cn/gallery/api/";
    public static final String BASE_ONE_URL="http://192.168.3.105:8989/gallery/api/";
//public static final String BASE_ONE_URL="http://192.168.3.19:8989/CateringAPI/api/";
    //    public static final String BASE_ONE_URL="https://at.bzys.cn:8443/api/";
	 //七牛上传凭证
    public static final String OPEN_API_QI_NIU_TOKEN_URL 
    = BASE_ONE_URL+"BaseApi/GetUploadToken/";
	//登录URL
    public static final String OPEN_API_LOGIN_URL 
    = BASE_ONE_URL+"User/PostUserLogin/";
	//重新登录URL
    public static final String OPEN_API_LOGIN_AGAIN_URL
    = BASE_ONE_URL+"User/GetNewTicket/";
	//退出登录
    public static final String OPEN_API_EXIT_URL 
    = BASE_ONE_URL+"BaseApi/GetUserExit/";
//    //获取店铺详细信息
//    public static final String OPEN_API_STORE_DETAIL_URL
//    = BASE_ONE_URL+"BaseApi/GetGalleryInfo/";
    //获取验证码
    public static final String OPEN_API_GET_CODE_URL 
    = BASE_ONE_URL+"User/GetValidateCode/";
    //店铺注册
    public static final String OPEN_API_STORE_REGIST_URL
    = BASE_ONE_URL+"User/PostResist/";
    //找回用户密码获取验证码
    public static final String OPEN_API_USER_VALIDATE_URL
    = BASE_ONE_URL+"User/GetRetValidateCode/";
    //找回用户密码
    public static final String OPEN_API_USER_RESET_PWD_URL
    = BASE_ONE_URL+"User/PostResetPassword/";
    //验证验证码正确性
    public static final String OPEN_API_VALIDATE_CODE_URL 
    = BASE_ONE_URL+"User/GetIsValidateCode/";
    //子账户列表
    public static final String OPEN_API_USER_CHILD_LIST_URL 
    = BASE_ONE_URL+"SubUser/GetSubUserAll";
    //子账户添加或更新
    public static final String OPEN_API_USER_CHILD_ADD_URL 
    = BASE_ONE_URL+"SubUser/PostSubUser/";
    //子账户详细信息
    public static final String OPEN_API_USER_CHILD_DETAIL_URL 
    = BASE_ONE_URL+"SubUser/GetSubUserById/";
    //子账户删除
    public static final String OPEN_API_USER_CHILD_DEL_URL 
    = BASE_ONE_URL+"SubUser/PostSubUserById/";
    //商品大类列表
    public static final String OPEN_API_GOODS_TYPE_LIST_URL 
    = BASE_ONE_URL+"GoodsType/GetGoodsTypeAll/";
    //商品大类添加或修改
    public static final String OPEN_API_GOODS_TYPE_ADD_OR_UPDATE_URL 
    = BASE_ONE_URL+"GoodsType/PostGoodsType/";
    //商品大类详细信息
    public static final String OPEN_API_GOODS_TYPE_DETAIL_URL 
    = BASE_ONE_URL+"GoodsType/GetGoodsTypeById/";
    //商品大类删除
    public static final String OPEN_API_GOODS_TYPE_DEL_URL 
    = BASE_ONE_URL+"GoodsType/PostGoodsTypeById/";
    //商品大类调换排序
    public static final String OPEN_API_GOODS_TYPE_SORT_CHANGE_URL 
    = BASE_ONE_URL+"GoodsType/PostRepGoodsTypeLevel/";
    //商品大类排序最大值
    public static final String OPEN_API_GOODS_TYPE_SORT_MAX_URL 
    = BASE_ONE_URL+"GoodsType/GetMaxSort/";
    //广告列表数据
    public static final String OPEN_API_ADVER_LIST_URL 
    = BASE_ONE_URL+"Promotion/GetPromotionAll/";
    //广告字典数据
    public static final String OPEN_API_ADVER_DIC_URL 
    = BASE_ONE_URL+"Promotion/GetPromotionStatusList/";
    //广告添加或修改
    public static final String OPEN_API_ADVER_ADD_OR_UPDATE_URL 
    = BASE_ONE_URL+"Promotion/PostPromotion/";
    //广告详细信息
    public static final String OPEN_API_ADVER_DETAIL_URL 
    = BASE_ONE_URL+"Promotion/GetPromotionById/";
    //广告删除
    public static final String OPEN_API_ADVER_DEL_URL 
    = BASE_ONE_URL+"Promotion/PostPromotionById/";

    //商品列表数据
    public static final String OPEN_API_GOODS_LIST_URL 
    = BASE_ONE_URL+"Goods/GetGoodsAll/";
    //商品销售状态列表
    public static final String OPEN_API_GOODS_DIC_STATUS_URL 
    = BASE_ONE_URL+"Goods/GetGoodsStatusList/";
    //商品添加或修改
    public static final String OPEN_API_GOODS_ADD_OR_UPDATE_URL 
    = BASE_ONE_URL+"Goods/PostGoods/";
  //商品删除
    public static final String OPEN_API_GOODS_DELETE_URL 
    = BASE_ONE_URL+"Goods/PostGoodsById/";
    //商品详细信息
    public static final String OPEN_API_GOODS_DETAIL_URL 
    = BASE_ONE_URL+"Goods/GetGoodsById/";
    //商品排序最大值
    public static final String OPEN_API_GOODS_SORT_MAX_URL 
    = BASE_ONE_URL+"Goods/GetMaxSort/";
    //商品交换排序
    public static final String OPEN_API_GOODS_SORT_CHANGE_URL 
    = BASE_ONE_URL+"Goods/PostRepGoodsLevel/";
    //商品删除
    public static final String OPEN_API_GOODS_DEL_URL 
    = BASE_ONE_URL+"Goods/DeleteGoodsById/";
    
    //订单列表
    public static final String OPEN_API_ORDER_LIST_URL 
    = BASE_ONE_URL+"Order/GetOrderAll/";
    //订单新增
    public static final String OPEN_API_ORDER_ADD_URL 
    = BASE_ONE_URL+"Order/PostOrder/";


    //反馈与建议
    public static final String OPEN_API_COMPANYFEEDBACK_URL
            = BASE_ONE_URL+"CompanyFeedback/PostCompanyFeedback/";

    //获取企业详细信息
    public static final String OPEN_API_ENTERPRISE_DETAIL_URL
            = BASE_ONE_URL+"CompanyInfo/GetCompanyInfo/";

    //获取地区选择省市区
    public static final String GET_ADDRESS_URL
            = BASE_ONE_URL+"User/GetPCAGetByJson/";

    //提交企业资料
    public static final String STORE_DETAIL_COMMIT_URL
            = BASE_ONE_URL+"CompanyInfo/PostUpdateCompany/";

    //获取推广员列表
    public static final String GET_ADVERT_LIST_URL
            = BASE_ONE_URL+"Promotion/GetPromotionInfoByOrder/";
    //获取获取订单信息
    public static final String GET_ORDER_LIST_URL
            = BASE_ONE_URL+"CompanyOrder/GetOrderFormInfo/";
    //修改订单详情状态
    public static final String ORDER_STATUS_CHANGE_URL
            = BASE_ONE_URL+"CompanyOrder/PostUpdateOrderStatus/";
    //订单收款
    public static final String ORDER_RECEIPT_URL
            = BASE_ONE_URL+"CompanyOrder/PostPayOrder/";
    //修改商品销售状态
    public static final String COMMODITY_STATUS_CHANGE_URL
            = BASE_ONE_URL+"CompanyOrder/PostUpdateGoodsSaleStatus/";
}
