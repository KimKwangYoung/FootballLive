package com.example.footballlive.data;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Keep
public class ShortPlay implements Comparable<ShortPlay>{

    String post_title;
    String home_team_name;
    String away_team_name;
    String home_team_key;
    String away_team_key;
    String stadium;
    String matchDay;
    String startTime;
    String endTime;
    String postKey;
    String createTime;

    LinkedHashMap<String, String> applicationTeam = new LinkedHashMap<>();


    public ShortPlay() {
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getHome_team_name() {
        return home_team_name;
    }

    public void setHome_team_name(String home_team_name) {
        this.home_team_name = home_team_name;
    }

    public String getAway_team_name() {
        return away_team_name;
    }

    public void setAway_team_name(String away_team_name) {
        this.away_team_name = away_team_name;
    }

    public String getHome_team_key() {
        return home_team_key;
    }

    public void setHome_team_key(String home_team_key) {
        this.home_team_key = home_team_key;
    }

    public String getAway_team_key() {
        return away_team_key;
    }

    public void setAway_team_key(String away_team_key) {
        this.away_team_key = away_team_key;
    }

    public LinkedHashMap<String, String> getApplicationTeam() {
        return applicationTeam;
    }

    public void setApplicationTeam(Map<String, String> applicationTeam) {
        this.applicationTeam = new LinkedHashMap<>(applicationTeam);
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public String getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(String matchDay) {
        this.matchDay = matchDay;
    }

    @Override
    public int compareTo(ShortPlay o) {
        return (createTime.compareTo(o.getCreateTime())) * (-1);
    }
}
