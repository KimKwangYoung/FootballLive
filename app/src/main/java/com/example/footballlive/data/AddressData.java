package com.example.footballlive.data;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddressData {
    @SerializedName("addresses")
    public List<AddData> addresses;



    public class AddData {
        @SerializedName("roadAddress") String roadAddress;
        @SerializedName("jibunAddress") String jibunAddress;
        @SerializedName("x") String x;
        @SerializedName("y") String y;

        public String getRoadAddress() {
            return roadAddress;
        }

        public void setRoadAddress(String roadAddress) {
            this.roadAddress = roadAddress;
        }

        public String getJibunAddress() {
            return jibunAddress;
        }

        public void setJibunAddress(String jibunAddress) {
            this.jibunAddress = jibunAddress;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }
    }






    @Override
    public String toString() {
        Log.e("CheckList", addresses + "");
        return addresses.get(0).roadAddress+" "+addresses.get(0).jibunAddress;
    }

    public String getLattitude(int position){
        return addresses.get(position).getY();
    }


    public String getLogitude(int position){
        return addresses.get(position).getX();
    }

    public String getJibunAddress(int position) {return addresses.get(position).getJibunAddress();}

    public String getDoroAddress(int position) {return addresses.get(position).getRoadAddress();}

    public List<AddData> getAddresses() {
        return addresses;
    }
}
