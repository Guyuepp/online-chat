package cn.edu.ncu.onlinechat.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private long total;
    private long pageNum;
    private long pageSize;
    private List<T> records;

    public static <T> PageResult<T> of(long total, long pageNum, long pageSize, List<T> records) {
        PageResult<T> p = new PageResult<>();
        p.setTotal(total);
        p.setPageNum(pageNum);
        p.setPageSize(pageSize);
        p.setRecords(records);
        return p;
    }
}
