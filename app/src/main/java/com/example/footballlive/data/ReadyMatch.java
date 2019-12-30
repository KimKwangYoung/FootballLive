package com.example.footballlive.data;

import androidx.annotation.Keep;

import java.util.LinkedHashMap;
import java.util.Map;

@Keep
public class ReadyMatch implements Comparable<ReadyMatch>{
    String oppentTeamName;
    String oppentTeamKey;
    String stadium;
    String matchDay;
    String startTime;
    String endTime;
    String matchKey;
    LinkedHashMap<String, User> cham = new LinkedHashMap<>();
    LinkedHashMap<String, User> bulcham = new LinkedHashMap<>();
    LinkedHashMap<String, User> allMember = new LinkedHashMap<>();

    public ReadyMatch() {
    }

    public String getMatchKey() {
        return matchKey;
    }

    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
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

    public String getOppentTeamName() {
        return oppentTeamName;
    }

    public void setOppentTeamName(String oppentTeamName) {
        this.oppentTeamName = oppentTeamName;
    }

    public String getOppentTeamKey() {
        return oppentTeamKey;
    }

    public void setOppentTeamKey(String oppentTeamKey) {
        this.oppentTeamKey = oppentTeamKey;
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

    public LinkedHashMap<String, User> getCham() {
        return cham;
    }

    public void setCham(Map<String, User> cham) {
        this.cham = new LinkedHashMap<>(cham);
    }

    public LinkedHashMap<String, User> getBulcham() {
        return bulcham;
    }

    public void setBulcham(Map<String, User> bulcham) {
        this.bulcham = new LinkedHashMap<>(bulcham);
    }

    public LinkedHashMap<String, User> getAllMember() {
        return allMember;
    }

    public void setAllMember(Map<String, User> allMember) {
        this.allMember = new LinkedHashMap<>(allMember);
    }

    @Override
    public int compareTo(ReadyMatch o) {
        return matchDay.compareTo(o.getMatchDay());
    }
}
