package cn.fyupeng.utils;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 封装分页后的数据格式
 */
public class PagedResult implements Serializable {
	
	private int page;			// 当前页数
	private int total;			// 总页数	
	private long records;		// 总记录数
	private List<?> rows;		// 每行显示的内容
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public long getRecords() {
		return records;
	}
	public void setRecords(long records) {
		this.records = records;
	}
	public List<?> getRows() {
		return rows;
	}
	public void setRows(List<?> rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "PagedResult{" +
				"page=" + page +
				", total=" + total +
				", records=" + records +
				", rows=" + rows +
				'}';
	}
}
