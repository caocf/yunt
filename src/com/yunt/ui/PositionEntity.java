/**  
 * Project Name:Android_Car_Example  
 * File Name:PositionEntity.java  
 * Package Name:com.amap.api.car.example  
 * Date:2015��4��3������9:50:28  
 *  
 */

package com.yunt.ui;

import com.liucanwen.citylist.R.string;

/**
 * ClassName:PositionEntity <br/>
 * Function: ��װ�Ĺ���λ�õ�ʵ�� <br/>
 * Date: 2015��4��3�� ����9:50:28 <br/>
 * 
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
public class PositionEntity {

	public double latitue;
	public double longitude;
	public String address;
	public String city;
	public String province;
	public String cityCode;

	public PositionEntity() {
	}

	public PositionEntity(double latitude, double longtitude, String address, String province, String city,
			String cityCode) {
		this.latitue = latitude;
		this.longitude = longtitude;
		this.address = address;
		this.province = province;
		this.cityCode = cityCode;
	}

}
