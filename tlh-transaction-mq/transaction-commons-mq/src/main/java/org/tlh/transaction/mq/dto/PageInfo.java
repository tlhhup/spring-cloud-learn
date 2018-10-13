package org.tlh.transaction.mq.dto;

import lombok.Data;

import java.util.List;

/**
 * @author huping
 * @desc
 * @date 18/10/13
 */
@Data
public class PageInfo<T> {

    private long total;
    private long totalPage;
    private List<T> data;
    private int currentPage;

}
