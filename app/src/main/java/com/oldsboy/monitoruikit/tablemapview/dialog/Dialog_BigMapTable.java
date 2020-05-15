package com.oldsboy.monitoruikit.tablemapview.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oldsboy.monitoruikit.R;
import com.oldsboy.monitoruikit.tablemapview.TableMapView;
import com.oldsboy.monitoruikit.tablemapview.bean.BaseColumnBean;
import com.oldsboy.monitoruikit.tableview.dialog.BaseMapDialog;

import java.util.LinkedHashMap;

/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views.activity
 * @ClassName: Dialog_ShowMapPicture
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/20 17:29
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/20 17:29
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class Dialog_BigMapTable<T> extends BaseMapDialog {
    private Context context;
    private String tableName;
    private String basePath;
    private TableMapView.dataSetting<T> dataSetting;
    private TableMapView.onToolBarClick<T> onToolBarClick;
    private LinkedHashMap<String, BaseColumnBean> tableHead;
    private TableMapView.OnTableBtnClickListener onTableBtnClickListener;

    private FrameLayout table1;

    public Dialog_BigMapTable(@NonNull Context context, String tableName, LinkedHashMap<String, BaseColumnBean> tableHead, TableMapView.onToolBarClick<T> onToolBarClick, TableMapView.dataSetting<T> dataSetting, TableMapView.OnTableBtnClickListener onTableBtnClickListener, String basePath) {
        super(context);
        this.context = context;
        this.tableName = tableName;
        this.tableHead = tableHead;
        this.dataSetting = dataSetting;
        this.onToolBarClick = onToolBarClick;
        this.onTableBtnClickListener = onTableBtnClickListener;
        this.basePath = basePath;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_big_table);

        bindViews();

        initTable();
    }

    private void initTable() {
        try {
            TableMapView tableMapView = new TableMapView<>(context, tableName, tableHead, dataSetting, onToolBarClick);
            tableMapView.setShowOrder(true);
            tableMapView.setScrollable(true);
            tableMapView.setOnTableBtnClickListener(onTableBtnClickListener);
            tableMapView.setTableTitleCanEnter(false);
            tableMapView.setBaseImagePath(basePath);
            tableMapView.showTable(table1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindViews() {
        ImageView imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        imgBack.setFocusable(true);
        imgBack.setFocusableInTouchMode(true);
        imgBack.requestFocus();

        table1 = findViewById(R.id.table1);
    }
}
