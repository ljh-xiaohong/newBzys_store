package com.bapm.bzys.newBzys.model;

import java.io.Serializable;

/**
 * Created by fs-ljh on 2017/4/14.
 */

public class AdvertList implements Serializable{


    /**
     * ID : 2
     * PromotionName : 一厅A3卫视你要不要紧是否常见问了一下说
     * PromotionNo : 12345763463
     * PromotionStatusId : 0
     * PicUrl : null
     * PromotionStatus : null
     * CofdLevels : 0
     */

    private int ID;
    private String PromotionName;
    private String PromotionNo;
    private int PromotionStatusId;
    private Object PicUrl;
    private Object PromotionStatus;
    private int CofdLevels;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getPromotionName() {
        return PromotionName;
    }

    public void setPromotionName(String PromotionName) {
        this.PromotionName = PromotionName;
    }

    public String getPromotionNo() {
        return PromotionNo;
    }

    public void setPromotionNo(String PromotionNo) {
        this.PromotionNo = PromotionNo;
    }

    public int getPromotionStatusId() {
        return PromotionStatusId;
    }

    public void setPromotionStatusId(int PromotionStatusId) {
        this.PromotionStatusId = PromotionStatusId;
    }

    public Object getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(Object PicUrl) {
        this.PicUrl = PicUrl;
    }

    public Object getPromotionStatus() {
        return PromotionStatus;
    }

    public void setPromotionStatus(Object PromotionStatus) {
        this.PromotionStatus = PromotionStatus;
    }

    public int getCofdLevels() {
        return CofdLevels;
    }

    public void setCofdLevels(int CofdLevels) {
        this.CofdLevels = CofdLevels;
    }
}
