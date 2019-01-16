package com.blg.rtu.frmFunction.bean;

import com.google.gson.Gson;
import java.io.Serializable;

public class WeatherVo implements Serializable {
    private String humidity;
    private String temp;
    private String weather;
    private String week;

    public String getHumidity()
    {
        return this.humidity;
    }

    public String getTemp()
    {
        return this.temp;
    }

    public String getWeather()
    {
        return this.weather;
    }

    public String getWeek()
    {
        return this.week;
    }

    public void setHumidity(String paramString)
    {
        this.humidity = paramString;
    }

    public void setTemp(String paramString)
    {
        this.temp = paramString;
    }

    public void setWeather(String paramString)
    {
        this.weather = paramString;
    }

    public void setWeek(String paramString)
    {
        this.week = paramString;
    }

    public String toString()
    {
        return new Gson().toJson(this);
    }
}
