package com.oldsboy.monitoruikit.tablemapview.bean;

/**
 * @ProjectName: MonitorUiKit
 * @Package: com.oldsboy.monitoruikit.tablemapview.bean
 * @ClassName: BeanResult
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/5/11 16:56
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/11 16:56
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class BeanResult<T> {
    private int position;
    private T result;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
