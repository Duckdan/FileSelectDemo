package com.study.fileselectlibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/11/8.
 */

public class FileItem implements Comparable<FileItem>, Parcelable {
    private String name;
    private boolean isFile;
    private String path;
    private long lastModifyTime;
    private boolean isRead;
    private long fileSize;
    private boolean isChecked;
    private String dataSize;
    private String dataDate;
    /**
     * 用于标记是否是从数据库中查询的数据
     */
    private boolean isData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getDataSize() {
        return dataSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate;
    }

    @Override
    public int compareTo(FileItem item) {
        if (isData) {
            return this.lastModifyTime - item.getLastModifyTime() > 0 ? -1 : 1;
        }
        return this.name.compareTo(item.getName());
    }

    public boolean isData() {
        return isData;
    }

    public void setData(boolean data) {
        isData = data;
    }

    @Override
    public String toString() {
        return "FileItem{" +
                "name='" + name + '\'' +
                ", isFile=" + isFile +
                ", path='" + path + '\'' +
                ", lastModifyTime=" + lastModifyTime +
                ", isRead=" + isRead +
                ", fileSize=" + fileSize +
                '}' + "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeByte(this.isFile ? (byte) 1 : (byte) 0);
        dest.writeString(this.path);
        dest.writeLong(this.lastModifyTime);
        dest.writeByte(this.isRead ? (byte) 1 : (byte) 0);
        dest.writeLong(this.fileSize);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeString(this.dataSize);
        dest.writeString(this.dataDate);
        dest.writeByte(this.isData ? (byte) 1 : (byte) 0);
    }

    public FileItem() {
    }

    protected FileItem(Parcel in) {
        this.name = in.readString();
        this.isFile = in.readByte() != 0;
        this.path = in.readString();
        this.lastModifyTime = in.readLong();
        this.isRead = in.readByte() != 0;
        this.fileSize = in.readLong();
        this.isChecked = in.readByte() != 0;
        this.dataSize = in.readString();
        this.dataDate = in.readString();
        this.isData = in.readByte() != 0;
    }

    public static final Creator<FileItem> CREATOR = new Creator<FileItem>() {
        @Override
        public FileItem createFromParcel(Parcel source) {
            return new FileItem(source);
        }

        @Override
        public FileItem[] newArray(int size) {
            return new FileItem[size];
        }
    };
}
