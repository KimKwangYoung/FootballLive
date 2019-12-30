package com.example.footballlive.data;

import androidx.annotation.Keep;

import java.util.ArrayList;

@Keep
public class Recruiting implements Comparable<Recruiting>{

    public Recruiting() {
    }

    String title;
    String content;
    String uniform;
    String dues;
    String recruiting_key;
    String stadium;
    ArrayList<User> application = new ArrayList<>();
    String members;
    String teamKey;
    String teamName;
    String creationTime;

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamKey() {
        return teamKey;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public String getRecruiting_key() {
        return recruiting_key;
    }

    public void setRecruiting_key(String recruiting_key) {
        this.recruiting_key = recruiting_key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUniform() {
        return uniform;
    }

    public void setUniform(String uniform) {
        this.uniform = uniform;
    }

    public String getDues() {
        return dues;
    }

    public void setDues(String dues) {
        this.dues = dues;
    }

    public ArrayList<User> getApplication() {
        return application;
    }

    public void setApplication(ArrayList<User> application) {
        this.application = application;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    @Override
    public int compareTo(Recruiting o) {
        return -(creationTime.compareTo(o.getCreationTime()));
    }
}
