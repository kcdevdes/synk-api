package com.kcdevdes.synk.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * content               // List<T>
 * page                  // int 현재 페이지
 * size                  // int 페이지 크기
 * totalElements         // long 전체 개수
 * totalPages            // int 전체 페이지
 * last                  // boolean 마지막 페이지 여부
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
