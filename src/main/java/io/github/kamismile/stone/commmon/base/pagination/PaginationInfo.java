package io.github.kamismile.stone.commmon.base.pagination;

import java.io.Serializable;

public class PaginationInfo implements Serializable {
	public final static int DEFAULT_PAGE_SIZE = 10;

	private int currentPage;
	private int pageSize;
	private long totalCount;
	private long totalPage;

	public PaginationInfo(final int currentPage, final int pageSize,
			final long totalPage, final long totalCount) {
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.totalPage = totalPage;
	}

	public PaginationInfo(final int currentPage, final int pageSize,
			final long totalCount) {
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.totalPage = (int) Math.ceil(totalCount * 1.0 / pageSize);
	}

	public int getPageSize() {
		return pageSize;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public long getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

}
