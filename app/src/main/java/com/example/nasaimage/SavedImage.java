package com.example.nasaimage;

import java.io.Serializable;

public class SavedImage implements Serializable {
    private String date;
    private String url;
    private String hdurl;
    private String fileName;

    public SavedImage(String date, String url, String hdurl, String fileName) {
        this.date = date;
        this.url = url;
        this.hdurl = hdurl;
        this.fileName = fileName;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public String getHdurl() {
        return hdurl;
    }

    public String getFileName() {
        return fileName;
    }
}

