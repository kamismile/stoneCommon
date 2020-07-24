package io.github.kamismile.stone.commmon.base.pagination;

import java.io.Serializable;
import java.util.List;

public class PaginationData<T> implements Serializable {

    protected List<T> data;
    protected PaginationInfo pageInfo;
    protected boolean showHead = false;


    public PaginationData(final List<T> data, final int pageId,
                          final int pageSize, final long totalPages, final long totalCount) {
//		data = Collections.unmodifiableList(data);
        this.data = data;
        pageInfo = new PaginationInfo(pageId, pageSize, totalPages, totalCount);
    }

    public PaginationData(List<T> data, PaginationInfo pageInfo) {
        this.data = data;
        this.pageInfo = pageInfo;
    }

    public PaginationData(final List<T> data, final int pageId,
                          final int pageSize, final long totalCount) {
//		data = Collections.unmodifiableList(data);
        this.data = data;
        pageInfo = new PaginationInfo(pageId, pageSize, totalCount);
    }

    public boolean isShowHead() {
        return showHead;
    }

    public void setShowHead(boolean showHead) {
        this.showHead = showHead;
    }

    public final List<T> getData() {
        return data;
    }

    public final PaginationInfo getPageInfo() {
        return pageInfo;
    }

}

