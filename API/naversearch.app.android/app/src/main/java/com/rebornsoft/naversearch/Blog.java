package com.rebornsoft.naversearch;

public class Blog {
    public String tv_title;
    public String tv_description;
    public String tv_bloggername;
    public String tv_postdate;

    public Blog(String tv_title, String tv_description, String tv_bloggername, String tv_postdate) {
        this.tv_title = tv_title;
        this.tv_description = tv_description;
        this.tv_bloggername = tv_bloggername;
        this.tv_postdate = tv_postdate;

    }


    public String getTv_title() {
        return tv_title;
    }

    public void setTv_title(String tv_title) {
        this.tv_title = tv_title;
    }

    public String getTv_description() {
        return tv_description;
    }

    public void setTv_description(String tv_description) {
        this.tv_description = tv_description;
    }

    public String getTv_bloggername() {
        return tv_bloggername;
    }

    public void setTv_bloggername(String tv_bloggername) {
        this.tv_bloggername = tv_bloggername;
    }

    public String getTv_postdate() {
        return tv_postdate;
    }

    public void setTv_postdate(String tv_postdate) {
        this.tv_postdate = tv_postdate;
    }
}
