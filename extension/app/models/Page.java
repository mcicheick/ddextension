package models;

import java.util.List;

/**
 * La pagination générique.<br>
 * Pratique pour des commentaires et autres!!
 * 
 * @author Cheick Mahady SISSOKO
 * @date 30 oct. 2014 12:26:06
 * @param <T>
 * 
 * @since bribe v1
 */
public class Page<T> {

	public final int pageSize;
	public final long totalRowCount;
	public final int pageIndex;
	public final long count;
	public final List<T> data;

	/**
	 * 
	 * @param data
	 * @param total
	 * @param page
	 * @param pageSize
	 */
	public Page(List<T> data, long total, int page, int pageSize) {
		this.data = data;
		this.totalRowCount = total;
		this.pageIndex = page;
		this.pageSize = pageSize;
		if(data !=null)
			this.count = data.size();
		else 
			this.count = 0;
	}

	public boolean hasPrev() {
		return pageIndex > 1;
	}

	public boolean hasNext() {
		return (totalRowCount / pageSize) >= pageIndex;
	}

	public String getDisplayXtoYofZ() {
		int start = ((pageIndex - 1) * pageSize + 1);
		int end = start + Math.min(pageSize, data.size()) - 1;
		return start + " to " + end + " of " + totalRowCount;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("pageSize : ").append(pageSize).append("\n")
				.append("totalRowCount : ").append(totalRowCount).append("\n")
				.append("pageIndex : ").append(pageIndex).append("\n")
				.append("list : ").append(data).append("\n");
		return sb.toString();
	}

}