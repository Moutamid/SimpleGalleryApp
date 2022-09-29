package com.moutamid.simplegalleryapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TODO: Add a class header comment!
 */
public class ImagesModel implements Parcelable {

    private String name;
    private String type;
    private String size;
    private long date;
    private String resolution;
    private String path;
    private boolean check;

    public ImagesModel() {
    }

    protected ImagesModel(Parcel in) {
        name = in.readString();
        type = in.readString();
        size = in.readString();
        date = in.readLong();
        resolution = in.readString();
        path = in.readString();
        check = in.readByte() != 0;
    }

    public static final Creator<ImagesModel> CREATOR = new Creator<ImagesModel>() {
        @Override
        public ImagesModel createFromParcel(Parcel in) {
            return new ImagesModel(in);
        }

        @Override
        public ImagesModel[] newArray(int size) {
            return new ImagesModel[size];
        }
    };

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(type);
        parcel.writeString(size);
        parcel.writeLong(date);
        parcel.writeString(resolution);
        parcel.writeString(path);
        parcel.writeByte((byte) (check ? 1 : 0));
    }
}