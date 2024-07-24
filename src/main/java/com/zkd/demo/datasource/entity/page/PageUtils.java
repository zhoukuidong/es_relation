package com.zkd.demo.datasource.entity.page;

import com.baomidou.mybatisplus.core.metadata.IPage;

public class PageUtils {
    /**
     * 获取分页查询结果
     *
     * @param pageInfo 分页信息
     * @return 分页结果
     */
    @SuppressWarnings("unchecked")
    public static <T> PageResult<T> getPageResult(IPage<T> pageInfo) {
        PageResult pageResult = new PageResult();
        pageResult.setPageNum(Math.toIntExact(pageInfo.getCurrent()));
        pageResult.setPageSize(Math.toIntExact(pageInfo.getSize()));
        pageResult.setTotalPages(Math.toIntExact(pageInfo.getPages()));
        pageResult.setTotalSize(pageInfo.getTotal());
        pageResult.setDataList(pageInfo.getRecords());
        return pageResult;
    }
}