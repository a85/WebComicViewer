package com.rickreation.webcomicviewer.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Strip implements Parcelable {
	String comic;
	
	String img;
	String title;
	String alt;
	int num;
	
	String date;	
	int month;
	int day;
	int year;
	
	String transcript;
	String url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Strip() {
		
	}
	
	public Strip(Parcel in) {
		comic = in.readString();
		img = in.readString();
		title = in.readString();
		alt = in.readString();		
		num = in.readInt();
		date = in.readString();
		month = in.readInt();
		day = in.readInt();
		year = in.readInt();		
		transcript = in.readString();
		url = in.readString();
	}

	public String getComic() {
		return comic;
	}

	public void setComic(String comic) {
		this.comic = comic;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getTranscript() {
		return transcript;
	}

	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {		
		dest.writeString(comic);
		dest.writeString(img);
		dest.writeString(title);
		dest.writeString(alt);
		dest.writeInt(num);
		dest.writeString(date);
		dest.writeInt(month);
		dest.writeInt(day);
		dest.writeInt(year);
		dest.writeString(transcript);
		dest.writeString(url);
	}		
	
	public static final Parcelable.Creator<Strip> CREATOR =
        new Parcelable.Creator<Strip>() {
        public Strip createFromParcel(Parcel in) {
            return new Strip(in);
        }

        public Strip[] newArray(int size) {
            return new Strip[size];
        }
    };
}
