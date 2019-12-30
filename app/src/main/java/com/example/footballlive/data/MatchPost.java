package com.example.footballlive.data;

import androidx.annotation.Keep;

import java.util.LinkedHashMap;
import java.util.Map;

@Keep
public class MatchPost implements Comparable<MatchPost>{

    public MatchPost() {
    }

    String play_key;
    String title;
    String content;
    String home_team_name;
    String away_team_name;
    String home_team;
    String away_team;
    String creationTime;
    String stadium_latitude;
    String stadium_longitude;
    public Long updateCnt;

    LinkedHashMap<String, User> home_team_entry = new LinkedHashMap<>();
    LinkedHashMap<String, User> away_team_entry = new LinkedHashMap<>();
    LinkedHashMap<String, String> applicationTeam = new LinkedHashMap<>();

    String stadium;
    String start_date_time;
    String end_date_time;
    String matchday;


    public String getStadium_latitude() {
        return stadium_latitude;
    }

    public void setStadium_latitude(String stadium_latitude) {
        this.stadium_latitude = stadium_latitude;
    }

    public String getStadium_longitude() {
        return stadium_longitude;
    }

    public void setStadium_longitude(String stadium_longitude) {
        this.stadium_longitude = stadium_longitude;
    }

    public Long getUpdateCnt() {
        return updateCnt;
    }

    public void setUpdateCnt(Long updateCnt) {
        this.updateCnt = updateCnt;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public LinkedHashMap<String, User> getHome_team_entry() {
        return home_team_entry;
    }

    public void setHome_team_entry(Map<String, User> home_team_entry) {
        this.home_team_entry = new LinkedHashMap<>(home_team_entry);
    }

    public LinkedHashMap<String, User> getAway_team_entry() {
        return away_team_entry;
    }

    public void setAway_team_entry(Map<String, User> away_team_entry) {
        this.away_team_entry = new LinkedHashMap<>(away_team_entry);
    }

    public LinkedHashMap<String, String> getApplicationTeam() {
        return applicationTeam;
    }

    public void setApplicationTeam(Map<String, String> applicationTeam) {
        this.applicationTeam = new LinkedHashMap<>(applicationTeam);
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

    public String getPlay_key() {
        return play_key;
    }

    public void setPlay_key(String play_key) {
        this.play_key = play_key;
    }

    public String getHome_team() {
        return home_team;
    }

    public void setHome_team(String home_team) {
        this.home_team = home_team;
    }

    public String getAway_team() {
        return away_team;
    }

    public void setAway_team(String away_team) {
        this.away_team = away_team;
    }


//    public ArrayList<User> getHome_team_score() {
//        return home_team_score;
//    }
//
//    public void setHome_team_score(ArrayList<User> home_team_score) {
//        this.home_team_score = home_team_score;
//    }
//
//    public ArrayList<User> getAway_team_score() {
//        return away_team_score;
//    }
//
//    public void setAway_team_score(ArrayList<User> away_team_score) {
//        this.away_team_score = away_team_score;
//    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public String getStart_date_time() {
        return start_date_time;
    }

    public void setStart_date_time(String start_date_time) {
        this.start_date_time = start_date_time;
    }

    public String getEnd_date_time() {
        return end_date_time;
    }

    public void setEnd_date_time(String end_date_time) {
        this.end_date_time = end_date_time;
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


    public String getMatchday() {
        return matchday;
    }

    public void setMatchday(String matchday) {
        this.matchday = matchday;
    }

    @Override
    public int compareTo(MatchPost o) {
        return -(creationTime.compareTo(o.getCreationTime()));
    }
}
