package com.example.footballlive.data;

import androidx.annotation.Keep;

import java.util.LinkedHashMap;
import java.util.Map;

@Keep
public class Team {
    public Team() {
    }

    String team_key;
    String team_name;
    String team_stadium_address;
    String team_stadium_latitude;
    String team_stadium_longitude;
    String team_leader;
    String teamPhoneNumber;

    // 신청한 리스트
    LinkedHashMap<String, ShortPlay> my_team_application_list = new LinkedHashMap<>();
    LinkedHashMap<String, String> ready_member = new LinkedHashMap<>();
    LinkedHashMap<String, ReadyMatch> ready_play = new LinkedHashMap<>();
    LinkedHashMap<String, ShortPlay> post = new LinkedHashMap<>();
    LinkedHashMap<String, User> team_member = new LinkedHashMap<>();
    LinkedHashMap<String, MatchResult> my_team_match_result = new LinkedHashMap<>();

    public String getTeamPhoneNumber() {
        return teamPhoneNumber;
    }

    public void setTeamPhoneNumber(String teamPhoneNumber) {
        this.teamPhoneNumber = teamPhoneNumber;
    }

    public LinkedHashMap<String, MatchResult> getMy_team_match_result() {
        return my_team_match_result;
    }

    public void setMy_team_match_result(Map<String, MatchResult> my_team_match_result) {
        this.my_team_match_result = new LinkedHashMap<>(my_team_match_result);
    }

    public LinkedHashMap<String, ShortPlay> getMy_team_application_list() {
        return my_team_application_list;
    }

    public void setMy_team_application_list(Map<String, ShortPlay> my_team_application_list) {
        this.my_team_application_list = new LinkedHashMap<>(my_team_application_list);
    }

    public LinkedHashMap<String, String> getReady_member() {
        return ready_member;
    }

    public void setReady_member(Map<String, String> ready_member) {
        this.ready_member = new LinkedHashMap<>(ready_member);
    }

    public LinkedHashMap<String, ReadyMatch> getReady_play() {
        return ready_play;
    }

    public void setReady_play(Map<String, ReadyMatch> ready_play) {
        this.ready_play = new LinkedHashMap<>(ready_play);
    }

    public LinkedHashMap<String, ShortPlay> getPost() {
        return post;
    }

    public void setPost(Map<String, ShortPlay> post) {
        this.post = new LinkedHashMap<>(post);
    }

    public LinkedHashMap<String, User> getTeam_member() {
        return team_member;
    }

    public void setTeam_member(Map<String, User> team_member) {
        this.team_member = new LinkedHashMap<>(team_member);
    }

    public String getTeam_key() {
        return team_key;
    }

    public void setTeam_key(String team_key) {
        this.team_key = team_key;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getTeam_leader() {
        return team_leader;
    }

    public void setTeam_leader(String team_leader) {
        this.team_leader = team_leader;
    }

    public String getTeam_stadium_address() {
        return team_stadium_address;
    }

    public void setTeam_stadium_address(String team_stadium_address) {
        this.team_stadium_address = team_stadium_address;
    }

    public String getTeam_stadium_latitude() {
        return team_stadium_latitude;
    }

    public void setTeam_stadium_latitude(String team_stadium_latitude) {
        this.team_stadium_latitude = team_stadium_latitude;
    }

    public String getTeam_stadium_longitude() {
        return team_stadium_longitude;
    }

    public void setTeam_stadium_longitude(String team_stadium_longitude) {
        this.team_stadium_longitude = team_stadium_longitude;
    }


}
