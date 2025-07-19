package com.example.demo.common;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用删除请求
 */
public class DeleteRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
   private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
