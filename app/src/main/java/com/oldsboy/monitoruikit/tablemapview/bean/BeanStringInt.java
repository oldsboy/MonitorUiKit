package com.oldsboy.monitoruikit.tablemapview.bean;

/**
 * @ProjectName: MonitorUiKit
 * @Package: com.oldsboy.monitoruikit.tablemapview.bean
 * @ClassName: BeanStringInt
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/5/11 16:58
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/11 16:58
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class BeanStringInt {
    private String value;
    private int position;

    public BeanStringInt(String value, int position) {
        this.value = value;
        this.position = position;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
