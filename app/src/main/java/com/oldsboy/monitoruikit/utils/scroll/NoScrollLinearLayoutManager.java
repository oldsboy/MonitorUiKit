package com.oldsboy.monitoruikit.utils.scroll;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * @ProjectName: MonitorUiKit
 * @Package: com.oldsboy.monitoruikit.utils.scroll
 * @ClassName: NoScrollLinearLayoutManager
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/24 16:55
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/24 16:55
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class NoScrollLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled;//设置了一个可控变量，true为可滚动，false不可

    private boolean canScroll;          //  再设置一个可控变量，当2个可滑动时再滑动

    public NoScrollLinearLayoutManager(Context context, boolean canScroll) {
        super(context);
        this.canScroll = canScroll;
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return canScroll && isScrollEnabled && super.canScrollVertically();
    }

}
