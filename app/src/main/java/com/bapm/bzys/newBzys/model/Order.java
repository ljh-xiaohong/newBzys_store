package com.bapm.bzys.newBzys.model;

import java.util.List;

/**
 * Created by fs-ljh on 2017/4/14.
 */

public class Order {

    /**
     * ID : 订单id
     * NumericalOrder : 序号
     * OrderNumber : 订单编号
     * ConsumerNickName : 买家昵称
     * CreateTime : 下单时间
     * OrderFormStatus : 订单状态id
     * Numbers : 总件数
     * Prices : 总金额
     * cofds : [{"ID":"订单详情id","GoodsName":"商品名称","GoodsNo":"商品编号","Number":"商品数量","Price":"商品价格","Need":"需求","OrderFormDetailsStatus":"订单详情状态id","GoodsSalesFlag":"商品销售状态id","GoodsCountedBy":"计算单位","PicThum":"商品图片路径"},{"ID":"订单详情id","GoodsName":"商品名称","GoodsNo":"商品编号","Number":"商品数量","Price":"商品价格","Need":"需求","OrderFormDetailsStatus":"订单详情状态id","GoodsSalesFlag":"商品销售状态id","GoodsCountedBy":"计算单位","PicThum":"商品图片路径"}]
     */

    private String ID;
    private String NumericalOrder;
    private String OrderNumber;
    private String ConsumerNickName;
    private String CreateTime;
    private String OrderFormStatus;
    private String Numbers;
    private String Prices;
    private List<CofdsBean> cofds;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNumericalOrder() {
        return NumericalOrder;
    }

    public void setNumericalOrder(String NumericalOrder) {
        this.NumericalOrder = NumericalOrder;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String OrderNumber) {
        this.OrderNumber = OrderNumber;
    }

    public String getConsumerNickName() {
        return ConsumerNickName;
    }

    public void setConsumerNickName(String ConsumerNickName) {
        this.ConsumerNickName = ConsumerNickName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public String getOrderFormStatus() {
        return OrderFormStatus;
    }

    public void setOrderFormStatus(String OrderFormStatus) {
        this.OrderFormStatus = OrderFormStatus;
    }

    public String getNumbers() {
        return Numbers;
    }

    public void setNumbers(String Numbers) {
        this.Numbers = Numbers;
    }

    public String getPrices() {
        return Prices;
    }

    public void setPrices(String Prices) {
        this.Prices = Prices;
    }

    public List<CofdsBean> getCofds() {
        return cofds;
    }

    public void setCofds(List<CofdsBean> cofds) {
        this.cofds = cofds;
    }

    public static class CofdsBean {
        /**
         * ID : 订单详情id
         * GoodsName : 商品名称
         * GoodsNo : 商品编号
         * Number : 商品数量
         * Price : 商品价格
         * Need : 需求
         * OrderFormDetailsStatus : 订单详情状态id
         * GoodsSalesFlag : 商品销售状态id
         * GoodsCountedBy : 计算单位
         * PicThum : 商品图片路径
         */

        private String ID;
        private String GoodsName;
        private String GoodsNo;
        private String Number;
        private String Price;
        private String Need;
        private String OrderFormDetailsStatus;
        private String GoodsSalesFlag;
        private String GoodsCountedBy;
        private String PicThum;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getGoodsName() {
            return GoodsName;
        }

        public void setGoodsName(String GoodsName) {
            this.GoodsName = GoodsName;
        }

        public String getGoodsNo() {
            return GoodsNo;
        }

        public void setGoodsNo(String GoodsNo) {
            this.GoodsNo = GoodsNo;
        }

        public String getNumber() {
            return Number;
        }

        public void setNumber(String Number) {
            this.Number = Number;
        }

        public String getPrice() {
            return Price;
        }

        public void setPrice(String Price) {
            this.Price = Price;
        }

        public String getNeed() {
            return Need;
        }

        public void setNeed(String Need) {
            this.Need = Need;
        }

        public String getOrderFormDetailsStatus() {
            return OrderFormDetailsStatus;
        }

        public void setOrderFormDetailsStatus(String OrderFormDetailsStatus) {
            this.OrderFormDetailsStatus = OrderFormDetailsStatus;
        }

        public String getGoodsSalesFlag() {
            return GoodsSalesFlag;
        }

        public void setGoodsSalesFlag(String GoodsSalesFlag) {
            this.GoodsSalesFlag = GoodsSalesFlag;
        }

        public String getGoodsCountedBy() {
            return GoodsCountedBy;
        }

        public void setGoodsCountedBy(String GoodsCountedBy) {
            this.GoodsCountedBy = GoodsCountedBy;
        }

        public String getPicThum() {
            return PicThum;
        }

        public void setPicThum(String PicThum) {
            this.PicThum = PicThum;
        }
    }
}
