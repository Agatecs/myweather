package com.agate.testcity.model;

/**
 * Created by Agate on 2016/11/29.
 */

public class County {
    private String countyName;
    private String countyCode;
    private String cityCode;

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getCountyCode() {

        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }
}
