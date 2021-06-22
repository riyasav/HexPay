package io.thoughtbox.hamdan.views.branches;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationResponseData implements Parcelable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("isdeleted")
    @Expose
    private String isdeleted;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("contact")
    @Expose
    private String contact;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(String isdeleted) {
        this.isdeleted = isdeleted;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeString(this.isdeleted);
        dest.writeString(this.address);
        dest.writeString(this.contact);
    }

    public LocationResponseData() {
    }

    protected LocationResponseData(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.isdeleted = in.readString();
        this.address = in.readString();
        this.contact = in.readString();
    }

    public static final Parcelable.Creator<LocationResponseData> CREATOR = new Parcelable.Creator<LocationResponseData>() {
        @Override
        public LocationResponseData createFromParcel(Parcel source) {
            return new LocationResponseData(source);
        }

        @Override
        public LocationResponseData[] newArray(int size) {
            return new LocationResponseData[size];
        }
    };
}