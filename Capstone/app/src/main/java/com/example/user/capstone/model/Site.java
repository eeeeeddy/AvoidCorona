package com.example.user.capstone.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Site {
    @SerializedName("records")
    public List<Data> records;

    public Site(List<Data> records) {
        this.records = records;
    }

    public static class Data {
        @SerializedName("확진일")
        private String date;
        @SerializedName("장소유형")
        private String placeType;
        @SerializedName("상호명")
        private String placeName;
        @SerializedName("위치도로명주소")
        private String placeRoadAddress;
        @SerializedName("위치지번주소")
        private String placeAddress;
        @SerializedName("위도")
        private String latitude;
        @SerializedName("경도")
        private String longitude;

        public Data(String date, String placeType, String placeName, String placeRoadAddress, String placeAddress, String latitude, String longitude) {
            this.date = date;
            this.placeType = placeType;
            this.placeName = placeName;
            this.placeRoadAddress = placeRoadAddress;
            this.placeAddress = placeAddress;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getDate() {
            return date;
        }

        public String getPlaceType() {
            return placeType;
        }

        public String getPlaceName() {
            return placeName;
        }

        public String getPlaceRoadAddress() {
            return placeRoadAddress;
        }

        public String getPlaceAddress() {
            return placeAddress;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        @Override
        public String toString() {
            return "Site{" +
                    "date='" + date + '\'' +
                    ", placeType='" + placeType + '\'' +
                    ", placeName='" + placeName + '\'' +
                    ", placeRoadAddress='" + placeRoadAddress + '\'' +
                    ", placeAddress='" + placeAddress + '\'' +
                    ", latitude='" + latitude + '\'' +
                    ", longitude='" + longitude + '\'' +
                    '}';
        }
    }

}
