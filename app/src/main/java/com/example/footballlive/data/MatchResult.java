package com.example.footballlive.data;

import androidx.annotation.Keep;

@Keep
public class MatchResult {
    String myTeam;
    String oppTeam;
    String myTeam_score;
    String oppTeam_score;
    String stadium;
    String matchDay;
    String matchKey;



    public MatchResult() {
    }

    public String getMatchKey() {
        return matchKey;
    }

    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
    }

    public String getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(String matchDay) {
        this.matchDay = matchDay;
    }

    public String getMyTeam() {
        return myTeam;
    }

    public void setMyTeam(String myTeam) {
        this.myTeam = myTeam;
    }

    public String getOppTeam() {
        return oppTeam;
    }

    public void setOppTeam(String oppTeam) {
        this.oppTeam = oppTeam;
    }

    public String getMyTeam_score() {
        return myTeam_score;
    }

    public void setMyTeam_score(String myTeam_score) {
        this.myTeam_score = myTeam_score;
    }

    public String getOppTeam_score() {
        return oppTeam_score;
    }

    public void setOppTeam_score(String oppTeam_score) {
        this.oppTeam_score = oppTeam_score;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }
}
