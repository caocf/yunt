package com.bepo.bean;

import java.util.List;

public class CommonBean<T> {

	public String total;
	public List<T> rows;

	public CommonBean(String total, List<T> rows) {
		super();
		this.total = total;
		this.rows = rows;
	}

	public CommonBean() {
		super();
	}

	/**
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}

	/**
	 * @param total
	 *            the total to set
	 */
	public void setTotal(String total) {
		this.total = total;
	}

	/**
	 * @return the rows
	 */
	public List<T> getRows() {
		return rows;
	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(List<T> rows) {
		this.rows = rows;
	}

}
