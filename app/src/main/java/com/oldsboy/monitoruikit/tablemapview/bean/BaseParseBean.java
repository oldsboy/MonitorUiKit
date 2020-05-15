package com.oldsboy.monitoruikit.tablemapview.bean;

/**
 * @ProjectName: MonitorUiKit
 * @Package: com.oldsboy.monitoruikit.tablemapview.bean
 * @ClassName: BaseParseBean
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/5/9 11:19
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/9 11:19
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class BaseParseBean {
    private String columnName;
    private String value;

    public BaseParseBean(String columnName, String value) {
        this.columnName = columnName;
        this.value = value;
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
