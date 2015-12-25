package com.yunt.ui;

public class PositionEntity {

	public double latitue;
	public double longitude;
	public String address;
	public String city;
	public String province;
	public String cityCode;
	public String address4Search;

	public PositionEntity() {
	}

	public PositionEntity(double latitude, double longtitude, String address, String address4Search,
			String province, String city, String cityCode) {
		this.latitue = latitude;
		this.longitude = longtitude;
		this.address = address;
		this.address4Search = address4Search;
		this.province = province;
		this.cityCode = cityCode;
	}

}
