package com.zkd.demo.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 注意：空白的单元格不会被读取
 * @Author zkd
 **/
@Slf4j
public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {

    /**
     * 所有列到字段名的映射
     */
    private Map<Integer, String> index2FieldMap;
    /**
     * 解析出的数据
     */
    private LinkedHashMap<Integer, Map<Integer, String>> row2DataMap = new LinkedHashMap<>();
    /**
     * 合并单元格
     */
    private List<CellExtra> extraMergeInfoList = new ArrayList<>();

    /**
     * 获取列数：列名的键值对 针对存在合并单元头的情况 取最后一次invokeHead获取到的值
     */
    private final Map<Integer, String> map = Maps.newHashMap();
    private List<List<String>> headList = Lists.newLinkedList();

    public NoModelDataListener() {
    }

    public NoModelDataListener(Map<Integer, String> index2FieldMap) {
        this.index2FieldMap = index2FieldMap;
    }

    @Override
    public void invoke(Map<Integer, String> rowData, AnalysisContext analysisContext) {
        //读取到的每行数据,其key是以0开始的索引
        row2DataMap.put(analysisContext.readRowHolder().getRowIndex(), rowData);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //所有行都解析完成
        this.explainMergeData(row2DataMap, extraMergeInfoList);
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        // k 0  v 列名
        log.info("解析到一条头数据:{}", JSON.toJSONString(headMap));

        //卡片适配
        List<String> columnHeadList = Lists.newArrayList();
        boolean addFlag = false;
        for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
            Integer k = entry.getKey();
            ReadCellData<?> v = entry.getValue();
            String stringValue = v.getStringValue();
            if (!stringValue.contains("填写条件")) {
                map.put(k, stringValue);
                columnHeadList.add(stringValue);
                addFlag = true;
            }
        }
        int size = map.size();
        map.put(size,"UNIQUE_KEY");
        if (addFlag) {
            headList.add(columnHeadList);
        }
    }

    /**
     * 某行的数据解析失败
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        System.err.println("解析失败，但是继续解析下一行: " + exception.getMessage());
        // 如果是某一个单元格的转换异常 能获取到具体行号
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
            System.err.println("第{}行，第{}列解析异常" + excelDataConvertException.getRowIndex() +
                    excelDataConvertException.getColumnIndex());
        }
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        switch (extra.getType()) {
            case MERGE: {
                extraMergeInfoList.add(extra);
                break;
            }
            case HYPERLINK: {
                break;
            }
            case COMMENT: {
            }
            default: {
            }
        }
    }

    /**
     * 处理合并单元格
     *
     * @param data               解析数据
     * @param extraMergeInfoList 合并单元格信息
     * @return 填充好的解析数据
     */
    private void explainMergeData(Map<Integer, Map<Integer, String>> data, List<CellExtra> extraMergeInfoList) {
        int count = 0;
        final Integer[] uniqueKey = {1};
        //循环所有合并单元格信息
        extraMergeInfoList.forEach(cellExtra -> {
            //合并的起始行
            int firstRowIndex = cellExtra.getFirstRowIndex();
            //合并的终止行
            int lastRowIndex = cellExtra.getLastRowIndex();
            //合并的起始列
            int firstColumnIndex = cellExtra.getFirstColumnIndex();
            //合并的终止列
            int lastColumnIndex = cellExtra.getLastColumnIndex();
            //rowIndex也是起始行 columnIndex也是起始列
            Map<Integer, String> rdata = data.get(cellExtra.getRowIndex());
            String val = null;
            if (rdata != null) {
                val = rdata.get(cellExtra.getColumnIndex());
            }
            //遍历每行
            Map<Integer, String> rowData = null;
            for (int i = firstRowIndex; i <= lastRowIndex; i++) {
                rowData = data.get(i);
                if (rowData == null) {
                    continue;
                }
                rowData.put(13, uniqueKey[0] +"");
                for (int c = firstColumnIndex; c <= lastColumnIndex; c++) {
                    rowData.put(c, val);
                }
            }
            if(rdata == null || rowData == null){
                return;
            }
            uniqueKey[0] = uniqueKey[0] +1;
        });
    }

//    public static void main(String[] args) throws FileNotFoundException {
//        //读取的表格名
//        String fileName = "C:\\Users\\dsf\\Desktop\\解析的\\2020年2季度电子渠道新业务开发需求-评估数据（数据导入模板）.xlsx";
//        String fileName2 = "C:\\Users\\dsf\\Desktop\\解析的\\非COSMIC评估工作量汇总模板.xlsx" ;
//        String fileName3 = "C:\\Users\\dsf\\Desktop\\解析的\\test.xlsx" ;
//        EasyExcel.read(fileName3,new NoModelDataListener())
//                .extraRead(CellExtraTypeEnum.MERGE)  // 需要读取合并单元格信息 默认不读取
//                //.registerConverter(new EmptyConverter()) //默认：DefaultConverterLoader#loadDefaultReadConverter()
//                .ignoreEmptyRow(true)
//                .autoTrim(true)
//                .headRowNumber(1)
//                .autoCloseStream(true)
//                //.sheet("2、功能点拆分表")
//                //.sheet("4、结果计算")
//                .sheet()
//                .doRead();
//
//    }
}