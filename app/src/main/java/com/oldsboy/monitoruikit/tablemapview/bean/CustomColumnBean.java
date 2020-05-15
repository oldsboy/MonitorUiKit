package com.oldsboy.monitoruikit.tablemapview.bean;

import com.oldsboy.monitoruikit.tablemapview.TableMapRecyclerAdapter;

/**
 * @ProjectName: MonitorUiKit
 * @Package: com.oldsboy.monitoruikit.tablemapview.bean
 * @ClassName: CustomColumnBean
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/5/9 16:24
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/9 16:24
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class CustomColumnBean extends BaseColumnBean {
    private int itemStatus;

    public CustomColumnBean(CustomColumnBean customColumnBean) {
        if (customColumnBean != null) {
            this.itemStatus = customColumnBean.itemStatus;
            super.setWidth(customColumnBean.getWidth());
            super.setItemType(customColumnBean.getItemType());
            super.setValue(customColumnBean.getValue());
        }
    }

    public CustomColumnBean(BaseColumnBean baseColumnBean) {
        super(baseColumnBean);
        this.itemStatus = TableMapRecyclerAdapter.SHOW_TYPE;
    }

    public CustomColumnBean(BaseColumnBean baseColumnBean, int itemStatus) {
        super(baseColumnBean);
        this.itemStatus = itemStatus;
    }

    public CustomColumnBean() {
    }

    public int getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(int itemStatus) {
        this.itemStatus = itemStatus;
    }
}
