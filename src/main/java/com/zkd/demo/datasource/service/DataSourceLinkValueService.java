package com.zkd.demo.datasource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zkd.demo.datasource.entity.DO.TDataSourceLinkValueDO;
import com.zkd.demo.datasource.entity.DTO.Link;

import java.util.List;

public interface DataSourceLinkValueService extends IService<TDataSourceLinkValueDO> {
    List<Link> queryLinkListBySourceCode(String sourceCode);
}
