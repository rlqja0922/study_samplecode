package com.rebornsoft.naversearch;

public class Movie {
    String title;
    String image;
    String director;
    String actor;


    public Movie(String title, String image, String director, String actor) {
        this.title = title;
        this.image = image;
        this.director = director;
        this.actor = actor;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
}
