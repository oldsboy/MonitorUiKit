package com.oldsboy.monitoruikit.tablemapview;

import java.util.LinkedHashMap;

/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views
 * @ClassName: ColumnValueBean
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/16 17:38
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/16 17:38
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ColumnValueBean {
    private int position;
    private String columnName;
    private String value;

    public ColumnValueBean(int position, String columnName, String value) {
        this.position = position;
        this.columnName = columnName;
        this.value = value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
