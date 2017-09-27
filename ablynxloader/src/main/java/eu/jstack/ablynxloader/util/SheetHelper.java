package eu.jstack.ablynxloader.util;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;

public class SheetHelper {
    public static void getData(Sheet firstSheet, ArrayList<LinkedHashMap<String, Object>> values) {
        ArrayList<String> keys = new ArrayList<>();
        int rowCount = 0;

        for (Row nextRow : firstSheet) {
            if (rowCount != 0) {
                values.add(new LinkedHashMap<>());
            }

            int columnIndex = 0;
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                getCellData(cell, rowCount, values, keys, columnIndex, nextRow);
                columnIndex++;
            }

            if (values.size() != 0) {
                LinkedHashMap<String, Object> value = values.get(values.size()-1);
                value.put("hash", Objects.hash(value.toString()));
            }
            ++rowCount;
        }

    }

    public static void getCellData(Cell cell, int rowCount, ArrayList<LinkedHashMap<String, Object>> values, ArrayList<String> keys, int columnIndex, Row row) {
        Object value = null;

        if (rowCount == 0) {
            String key = cell.getStringCellValue();
            keys.add(key);
            return;
        }

        switch (cell.getCellTypeEnum()) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                value = cell.getNumericCellValue();
                break;
            default:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                }
        }
        values.get(values.size()-1).put(keys.get(columnIndex), value);
    }
}
