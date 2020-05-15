package com.oldsboy.monitoruikit.tablemapview.bean;

import androidx.annotation.NonNull;

import com.oldsboy.monitoruikit.tablemapview.TableMapView;

/**
 * @ProjectName: MonitorUiKit
 * @Package: com.oldsboy.monitoruikit.tablemapview.bean
 * @ClassName: BaseColumnBean
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/5/9 9:59
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/9 9:59
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class BaseColumnBean {
    private String value;
    private int width;
    private int itemType;

    @Override
    public String toString() {
        return "BaseColumnBean{" +
                "value='" + value + '\'' +
                ", width=" + width +
                ", itemType=" + itemType +
                '}';
    }

    public BaseColumnBean(BaseColumnBean baseColumnBean) {
        this();
        if (baseColumnBean != null) {
            this.value = baseColumnBean.getValue();
            this.width = baseColumnBean.getWidth();
            this.itemType = baseColumnBean.getItemType();
        }
    }

    public BaseColumnBean(String value, int width, int itemType) {
        this.value = value;
        this.width = width;
        this.itemType = itemType;
    }

    public BaseColumnBean(String value, int width) {
        this(value, width, TableMapView.ItemEditType.textInput);
    }

    public BaseColumnBean(String value) {
        this(value, 40, TableMapView.ItemEditType.textInput);
    }

    public BaseColumnBean() {
        this("未设置值", 40, TableMapView.ItemEditType.textInput);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
