package com.zkd.demo.datasource.controller;

import com.zkd.demo.basic.core.result.R;
import com.zkd.demo.datasource.entity.DTO.Link;
import com.zkd.demo.datasource.entity.VO.DataSourceDropVO;
import com.zkd.demo.datasource.entity.VO.DataSourceLinkVO;
import com.zkd.demo.datasource.entity.VO.DataSourceListVO;
import com.zkd.demo.datasource.entity.VO.DataSourceTypeVO;
import com.zkd.demo.datasource.entity.meta.Table;
import com.zkd.demo.datasource.entity.page.PageResult;
import com.zkd.demo.datasource.entity.request.DataSourceDeleteRequest;
import com.zkd.demo.datasource.entity.request.DataSourceLinkRequest;
import com.zkd.demo.datasource.entity.request.DataSourceRenameRequest;
import com.zkd.demo.datasource.entity.request.DataSourceSqlRequest;
import com.zkd.demo.datasource.service.DataSourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@RestController
@Api(tags = "数据源api")
@RequestMapping("/datasource")
public class InternalDataSourceController {

    @Autowired
    private DataSourceService dataSourceService;

    @ApiOperation(value = "条件分页查询数据源列表")
    @PostMapping(value = "/list")
    public R<PageResult<? extends DataSourceListVO>> queryList(HttpServletRequest request) {
        return R.ok(dataSourceService.queryList(request));
    }

    @ApiOperation(value = "获取所有数据源类型")
    @GetMapping(value = "/type-list")
    public R<List<DataSourceTypeVO>> queryTypeList() {
        return R.ok(dataSourceService.queryTypeList());
    }

    @ApiOperation(value = "根据数据源类型获取数据源连接模板")
    @GetMapping(value = "/template")
    public R<List<Link>> queryTemplateByType(@ApiParam(value = "数据源类型编码", required = true) @RequestParam String sourceTypeCode) {
        return R.ok(dataSourceService.queryTemplateByType(sourceTypeCode));
    }

    @ApiOperation(value = "获取数据源连接信息")
    @GetMapping(value = "/detail")
    public R<DataSourceLinkVO> queryLinkDetail(@ApiParam(value = "数据源编码", required = true) @RequestParam String sourceCode) {
        return R.ok(dataSourceService.queryLinkDetail(sourceCode));
    }

    @ApiOperation(value = "新增数据源")
    @PostMapping(value = "/insert")
    public R<String> insert(@Valid @RequestBody DataSourceLinkRequest request) {
        //返回新增后的sourceCode
        return R.ok(dataSourceService.insert(request));
    }

    @ApiOperation(value = "测试连接")
    @PostMapping(value = "/conn-test")
    public R<Boolean> connTest(@RequestBody DataSourceLinkRequest request) {
        return R.ok(dataSourceService.connTest(request));
    }

    @ApiOperation(value = "修改数据源")
    @PostMapping(value = "/update")
    public R update(@RequestBody DataSourceLinkRequest request) {
        dataSourceService.update(request);
        return R.ok();
    }

    @ApiOperation(value = "删除数据源")
    @PostMapping(value = "/delete")
    public R delete(@Valid @RequestBody DataSourceDeleteRequest request) {
        dataSourceService.delete(request);
        return R.ok();
    }

    @ApiOperation(value = "数据源重命名")
    @PostMapping(value = "/rename")
    public R rename(@Valid @RequestBody DataSourceRenameRequest request) {
        dataSourceService.rename(request);
        return R.ok();
    }

    @ApiOperation(value = "数据源下拉列表")
    @PostMapping(value = "/drop")
    public R<List<DataSourceDropVO>> queryDropList(HttpServletRequest request) {
        return R.ok(dataSourceService.queryDropList(request));
    }

    @ApiOperation(value = "获取数据表名")
    @GetMapping(value = "/tables")
    public R<List<String>> getTables(@ApiParam(value = "数据源编码", required = true) @RequestParam String sourceCode) {
        return R.ok(dataSourceService.getTables(sourceCode));
    }

    @ApiOperation(value = "获取表的元数据")
    @GetMapping(value = "/table-meta")
    public R<Table> getMetaData(@ApiParam(value = "数据源编码", required = true) @RequestParam String sourceCode,
                                @ApiParam(value = "数据表名称", required = true) @RequestParam String tableName) {
        return R.ok(dataSourceService.getMetaData(sourceCode, tableName));
    }

    @ApiOperation(value = "执行sql")
    @PostMapping(value = "/sql")
    public R<List<Map<String, Object>>> executeQuery(@RequestBody DataSourceSqlRequest request) {
        return R.ok(dataSourceService.executeQuery(request.getSql(), request.getSourceCode()));
    }


}