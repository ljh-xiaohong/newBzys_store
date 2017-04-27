package com.bapm.bzys.newBzys.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fs-ljh on 2017/4/13.
 */

public class Address implements Serializable{

    private List<JsonDataBean> jsonData;

    public List<JsonDataBean> getJsonData() {
        return jsonData;
    }

    public void setJsonData(List<JsonDataBean> jsonData) {
        this.jsonData = jsonData;
    }

    public static class JsonDataBean {
        /**
         * ProvinceId : 110000
         * ProvinceName : 北京
         * Citys : [{"CityId":110100,"CityName":"北京","Areas":[{"AreaId":110101,"AreaName":"东城"},{"AreaId":110102,"AreaName":"西城"}]}]
         */

        private int ProvinceId;
        private String ProvinceName;
        private List<CitysBean> Citys;

        public int getProvinceId() {
            return ProvinceId;
        }

        public void setProvinceId(int ProvinceId) {
            this.ProvinceId = ProvinceId;
        }

        public String getProvinceName() {
            return ProvinceName;
        }

        public void setProvinceName(String ProvinceName) {
            this.ProvinceName = ProvinceName;
        }

        public List<CitysBean> getCitys() {
            return Citys;
        }

        public void setCitys(List<CitysBean> Citys) {
            this.Citys = Citys;
        }

        public static class CitysBean {
            /**
             * CityId : 110100
             * CityName : 北京
             * Areas : [{"AreaId":110101,"AreaName":"东城"},{"AreaId":110102,"AreaName":"西城"}]
             */

            private int CityId;
            private String CityName;
            private List<AreasBean> Areas;

            public int getCityId() {
                return CityId;
            }

            public void setCityId(int CityId) {
                this.CityId = CityId;
            }

            public String getCityName() {
                return CityName;
            }

            public void setCityName(String CityName) {
                this.CityName = CityName;
            }

            public List<AreasBean> getAreas() {
                return Areas;
            }

            public void setAreas(List<AreasBean> Areas) {
                this.Areas = Areas;
            }

            public static class AreasBean {
                /**
                 * AreaId : 110101
                 * AreaName : 东城
                 */

                private int AreaId;
                private String AreaName;

                public int getAreaId() {
                    return AreaId;
                }

                public void setAreaId(int AreaId) {
                    this.AreaId = AreaId;
                }

                public String getAreaName() {
                    return AreaName;
                }

                public void setAreaName(String AreaName) {
                    this.AreaName = AreaName;
                }
            }
        }
    }
}
