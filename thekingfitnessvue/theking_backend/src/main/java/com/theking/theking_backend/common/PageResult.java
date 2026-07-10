package com.theking.theking_backend.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private int code;
    private String message;
    private List<T> data;
    private long total;
    private int page;
    private int size;

    public static <T> PageResult<T> success(List<T> data, long total, int page, int size) {
        PageResult<T> r = new PageResult<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        r.setTotal(total);
        r.setPage(page);
        r.setSize(size);
        return r;
    }

    public static <T> PageResult<T> success(List<T> data, long total) {
        return success(data, total, 1, 10);
    }
}
