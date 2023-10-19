package com.zkd.demo.excel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 针对主子表的列合并策略
 * see: https://github.com/alibaba/easyexcel/issues/778
 */
public class CustomLoopMergeStrategy extends AbstractMergeStrategy {

    /**
     * eachRow 举例 {2，3，4，5}
     * 表示按照主表的唯一键分组 子表的数量依次是2，3，4，5
     * 2->第一行和第二行合并
     * 3->第三行、第四行、第五行合并
     * 4->第6，7，8，9行合并
     * 5->第10，11，12，13，14，15行合并
     */
    private LinkedList<Integer> eachRow;
    /**
     * 当前需要合并的列号
     */
    private int columnIndex;
    /**
     * 需要合并的列号中最大的列号
     */
    private int maxColumn;
    private LinkedList<Integer> totalRows;

    public CustomLoopMergeStrategy(List<Integer> eachRow, int columnIndex, int maxColumn) {
        if (eachRow.stream().anyMatch(row -> row < 1)) {
            throw new IllegalArgumentException("EachRows must be greater than 1");
        }
        this.eachRow = new LinkedList<>(eachRow);
        this.columnIndex = columnIndex;
        this.maxColumn = maxColumn;
        final int[] acc = {0};
        this.totalRows = eachRow.stream().map(item -> {
            int result = item + acc[0], item1 = item + acc[0];
            acc[0] = item1;
            if (item == 1) {
                result = 0;
            }
            return result;
        }).filter(i -> i != 0).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {

        if (totalRows.isEmpty() || eachRow.isEmpty() || head.getColumnIndex() > maxColumn) {
            return;
        }

        if (head.getColumnIndex() == maxColumn && eachRow.getFirst() == 1) {
            eachRow.removeFirst();
            return;
        }

        if (relativeRowIndex >= totalRows.getFirst()) {
            totalRows.removeFirst();
            eachRow.removeFirst();
        }

        if (head.getColumnIndex() == columnIndex && !totalRows.isEmpty() && !eachRow.isEmpty()
                && relativeRowIndex == totalRows.getFirst() - eachRow.getFirst()) {

            CellRangeAddress cellRangeAddress = new CellRangeAddress(cell.getRowIndex(),
                    cell.getRowIndex() + eachRow.getFirst() - 1, cell.getColumnIndex(), cell.getColumnIndex());

            sheet.addMergedRegion(cellRangeAddress);

        }
    }
}
