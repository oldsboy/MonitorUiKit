package com.oldsboy.monitoruikit.tableview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.oldsboy.monitoruikit.R;
import com.oldsboy.monitoruikit.utils.ScreenUtil;


/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views.dialog
 * @ClassName: BaseMapDialog
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/21 10:00
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/21 10:00
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class BaseDialog extends Dialog {
    public BaseDialog(@NonNull Context context) {
        super(context, R.style.dialog_style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            ScreenUtil.configDialog(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
    }
}
