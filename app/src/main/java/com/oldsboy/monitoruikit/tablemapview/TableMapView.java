package com.oldsboy.monitoruikit.tablemapview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.oldsboy.monitoruikit.R;
import com.oldsboy.monitoruikit.tablemapview.bean.BaseColumnBean;
import com.oldsboy.monitoruikit.tablemapview.bean.BaseParseBean;
import com.oldsboy.monitoruikit.tablemapview.bean.BeanResult;
import com.oldsboy.monitoruikit.tablemapview.bean.BeanStringInt;
import com.oldsboy.monitoruikit.tablemapview.bean.CustomColumnBean;
import com.oldsboy.monitoruikit.tablemapview.dialog.Dialog_BigMapTable;
import com.oldsboy.monitoruikit.utils.ScreenUtil;
import com.oldsboy.monitoruikit.utils.StringUtil;
import com.oldsboy.monitoruikit.utils.scroll.NoScrollLinearLayoutManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * @ProjectName: FP-app
 * @Package: com.medicine.fxpg.view
 * @ClassName: Customtablemapview
 * @Description: 表格控件，该控件只能代码生成，不能再xml内配置；配合CustomTableAdapter使用
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/2 15:33
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/2 15:33
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class TableMapView<T> extends LinearLayout {
    public static final String TAG = "customtablemapview";
    private Context context;
    private TableMapView tableMapView;

    /************************************************************************************************************************************************************************************************
     * data
     * **************************************************************************************************************************************************************************************************/
    private String tableName;
    private TableMapRecyclerAdapter adapter;
    private LinkedHashMap<String, BaseColumnBean> tableHead;
    private List<LinkedHashMap<String, CustomColumnBean>> tableList;
    private boolean isShowTable = false;
    /************************************************************************************************************************************************************************************************
     * setting
     * **************************************************************************************************************************************************************************************************/
    private boolean isShowOrder = false;
    private boolean canScroll = true;
    private int headHeight = 0;
    private boolean headTextBold = false;
    private String basePath;

    /************************************************************************************************************************************************************************************************
     * view
     * **************************************************************************************************************************************************************************************************/
    private TextView tv_table_name;
    private RelativeLayout btn_add, btn_edit, btn_delete, btn_save;
    private LinearLayout container_tablehead;
    private LinearLayout container_table_toolbar;
    private RecyclerView recycler_tablebody;
    private ImageView img_big;
    private ImageView img_refresh;
    private ImageView img_bottom;
    private FrameLayout container_title;
    private LinearLayout container;

    private LayoutInflater layoutInflater;
    private NoScrollLinearLayoutManager noScrollLinearLayoutManager;

    /**
     * interface
     * **/
    private onToolBarClick<T> onToolBarClick;
    private TableMapView.dataSetting<T> dataSetting;
    private TableMapRecyclerAdapter.OnMyItemLongClickListener<T> onMyItemLongClickListener;
    private TableMapRecyclerAdapter.OnMyItemDoubleClickListener<T> onMyItemDoubleClickListener;
    private TableMapRecyclerAdapter.OnMyItemClickListener<T> onMyItemClickListener;
    private OnTableBtnClickListener onTableBtnClickListener;

    /************************************************************************************************************************************************************************************************
     * function
     * **************************************************************************************************************************************************************************************************/

    public void reloadData(){
        adapter.setData(getList());
        refreshTableHeight();

        noScrollLinearLayoutManager.setScrollEnabled(true);
    }

    public void setBaseImagePath(String path){
        if (adapter != null) {
            adapter.setBase_picture_path(path);
        }
        this.basePath = path;
    }

    public void setOnTableBtnClickListener(OnTableBtnClickListener onTableBtnClickListener) {
        this.onTableBtnClickListener = onTableBtnClickListener;
    }

    public void setTableTitleCanEnter(boolean canEnter){
        if (canEnter){
            tv_table_name.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Dialog_BigMapTable dialog_bigTable = new Dialog_BigMapTable<>(context, tableName, tableHead, onToolBarClick, dataSetting, onTableBtnClickListener, basePath);
                    dialog_bigTable.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            reloadData();
                        }
                    });
                    dialog_bigTable.show();
                    return false;
                }
            });
            img_big.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog_BigMapTable dialog_bigTable = new Dialog_BigMapTable<>(context, tableName, tableHead, onToolBarClick, dataSetting, onTableBtnClickListener, basePath);
                    dialog_bigTable.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            reloadData();
                        }
                    });
                    dialog_bigTable.show();
                }
            });
        }else {
            img_big.setVisibility(GONE);
            tv_table_name.setOnLongClickListener(null);
            img_big.setOnClickListener(null);
        }
    }

    public void setHeadTextBold(boolean isBold){
        this.headTextBold = isBold;
    }

    public void setHeadHeight(int height){
        this.headHeight = height;
    }

    public void setTextSize(int size){
        TableMapRecyclerAdapter.setTextSize(size);
    }

    public void add(T object){
        if (tableHead != null && isShowTable && dataSetting != null) {
            List<BaseParseBean> allColumn = new ArrayList<>(tableHead.size());
            if (isShowOrder){
                allColumn.add(new BaseParseBean("序号", ""));
            }
            dataSetting.parse(object, allColumn);        //  一列的数据

            LinkedHashMap<String, CustomColumnBean> onLineNewData = new LinkedHashMap<>(tableHead.size());
            for (BaseParseBean column : allColumn) {
                CustomColumnBean CustomColumnBean = new CustomColumnBean(tableHead.get(column.getColumnName()));
                CustomColumnBean.setValue(column.getValue());
                onLineNewData.put(column.getColumnName(), CustomColumnBean);
            }

            adapter.getData().add(onLineNewData);
            adapter.notifyItemInserted(adapter.getData().size());
        }
    }

    public void updata(T object){
        if (tableHead != null && isShowTable && dataSetting != null) {
            List<BaseParseBean> allColumn = new ArrayList<>(tableHead.size());
            if (isShowOrder){
                allColumn.add(new BaseParseBean("序号", ""));
            }
            dataSetting.parse(object, allColumn);        //  一列的数据

            LinkedHashMap<String, CustomColumnBean> onLineNewData = new LinkedHashMap<>(tableHead.size());
            for (BaseParseBean column : allColumn) {
                CustomColumnBean CustomColumnBean = new CustomColumnBean(tableHead.get(column.getColumnName()));
                CustomColumnBean.setValue(column.getValue());
                onLineNewData.put(column.getColumnName(), CustomColumnBean);
            }

            tableList.set(adapter.getLast_click_position(), onLineNewData);
            adapter.notifyItemChanged(adapter.getLast_click_position());
        }
    }

    public BeanResult<T> delete(){
        if (adapter != null && adapter.getData() != null && adapter.getLast_click_position() != -1 && adapter.getData().size() > adapter.getLast_click_position()) {
            LinkedHashMap<String, CustomColumnBean> line = adapter.getData().get(adapter.getLast_click_position());
            LinkedHashMap<String, String> oneLine = new LinkedHashMap<>();
            for (String columnName : line.keySet()) {
                CustomColumnBean customColumnBean = line.get(columnName);
                if (customColumnBean != null) {
                    oneLine.put(columnName, customColumnBean.getValue());
                }
            }
            BeanResult<T> beanResult = new BeanResult<>();
            beanResult.setPosition(adapter.getLast_click_position());
            beanResult.setResult(dataSetting.reParse(oneLine));

            adapter.removeItem(adapter.getLast_click_position());
            refreshTableHeight();
            return beanResult;
        }
        return null;
    }

    public void setTableName(String tableName){
        this.tableName = tableName;
    }

    public void setOnMyItemLongClickListener(TableMapRecyclerAdapter.OnMyItemLongClickListener<T> onMyItemLongClickListener){
        if (this.adapter != null) {
            this.adapter.setOnMyItemLongClickListener(onMyItemLongClickListener);
        }
        this.onMyItemLongClickListener = onMyItemLongClickListener;
    }

    public void setOnMyItemDoubleClickListener(TableMapRecyclerAdapter.OnMyItemDoubleClickListener<T> onMyItemDoubleClickListener){
        if (this.adapter != null) {
            this.adapter.setOnMyItemDoubleClickListener(onMyItemDoubleClickListener);
        }
        this.onMyItemDoubleClickListener = onMyItemDoubleClickListener;
    }

    public void setOnMyItemClickListener(TableMapRecyclerAdapter.OnMyItemClickListener<T> onMyItemClickListener){
        if (this.adapter != null) {
            this.adapter.setOnMyItemClickListener(onMyItemClickListener);
        }
        this.onMyItemClickListener = onMyItemClickListener;
    }

    /**
     * 获取当前点击的行的信息
     * @return  当前点击的行的信息
     */
    public BeanResult<T> getCurrentClickItem(){
        if (adapter != null && dataSetting != null) {
            int last_click_position = adapter.getLast_click_position();
            LinkedHashMap<String, CustomColumnBean> line = adapter.getData().get(last_click_position);

            LinkedHashMap<String, String> res = new LinkedHashMap<>(line.size());
            for (String columnName : line.keySet()) {
                CustomColumnBean customColumnBean = line.get(columnName);

                if (customColumnBean != null) {
                    res.put(columnName, customColumnBean.getValue());
                }
            }

            BeanResult<T> beanResult = new BeanResult<>();
            beanResult.setPosition(last_click_position);
            beanResult.setResult(dataSetting.reParse(res));
            return beanResult;
        }
        return null;
    }

    public void setTableToolbarVisiablilty(int visibility) {
        this.container_table_toolbar.setVisibility(visibility);
    }

    public void setTitleVisiablilty(int visibility) {
        this.container_title.setVisibility(visibility);
    }

    /**
     * 设置单个格子内的值
     * @param position      行数
     * @param columnName    列名
     * @param value         值
     */
    public void setSingleValue(int position, String columnName, String value){
        if (adapter != null) {
            adapter.setSingleValue(position, columnName, value);
            adapter.notifyItemChanged(position);
        }
    }

    /**
     *  设计整体表格可移动设计，当总体不可移动时将显示表格所有高度！
     * @param canScroll
     */
    public void setScrollable(boolean canScroll){
        this.canScroll = canScroll;
    }

    /**
     * 是否显示序号
     * @param isShow 是/否
     */
    public void setShowOrder(boolean isShow){
        this.isShowOrder = isShow;
    }

    /**
     * 目前设计的表格中的缺陷在于此，若是高度自适应，则自带的编辑功能会造成一个样式占满的bug，解决此问题则不用自带控件而调用提供的增删接口；
     * @param lineHeight 设置的行高
     */
    public void setLineHeight(int lineHeight){
        TableMapRecyclerAdapter.setLineHeight(lineHeight);
    }

    /************************************************************************************************************************************************************************************************
     * tableView
     * **************************************************************************************************************************************************************************************************/
    public @interface ItemEditType{
        int index = 99;
        int cannotEdit = 100;
        int textInput = 101;
        int textSpinner = 102;
        int imageSelect = 103;
        int numberInput = 104;
    }

    public TableMapView(Context context, String tableName, LinkedHashMap<String, BaseColumnBean> tableHead, dataSetting<T> dataSetting, onToolBarClick<T> onToolBarClick) {
        super(context);
        this.context = context;
        this.tableHead = tableHead;
        this.tableName = tableName;
        this.dataSetting = dataSetting;
        this.onToolBarClick = onToolBarClick;
        tableMapView = this;

        layoutInflater = (LayoutInflater) this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.custom_table_recycler_view, this);
        bindViews();
    }

    /**
     * 显示表格
     * @param root 表格容器
     */
    public void showTable(ViewGroup root){
        this.initTableHead();
        this.initTool();
        this.tableList = getList();
        this.initListView();
        this.refreshTableHeight();

        if (root != null) {
            root.removeAllViews();
            root.addView(this);
            isShowTable = true;
        }
    }

    private void refreshTableHeight() {
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        if (layoutParams == null) layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);

        if (canScroll) {        //  如果表格可以移动则简单设置为可以match_parent
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }else {                 //  如果表格不可以移动则计算所有表格高度
            if (TableMapRecyclerAdapter.getLineHeight() < 0){
                layoutParams.height = TableMapRecyclerAdapter.getLineHeight();
            }else {
                layoutParams.height = ScreenUtil.px2dp(context, 30)
                        + ScreenUtil.px2dp(context, 30)
                        + container_tablehead.getLayoutParams().height
                        + adapter.getItemCount() * ScreenUtil.px2dp(context, TableMapRecyclerAdapter.getLineHeight())
                        + ScreenUtil.px2dp(context, 2);
            }
        }

        this.setLayoutParams(layoutParams);
//        Log.d(TAG, "当前表格总体高度是：" + layoutParams.height);
    }

    private void initTool() {
        final OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.btn_add) {
                    LinkedHashMap<String, CustomColumnBean> newData = new LinkedHashMap<>();
                    for (String columnName : tableHead.keySet()) {
                        BaseColumnBean baseColumnBean = tableHead.get(columnName);
                        if (baseColumnBean != null) {
                            baseColumnBean.setValue("");
                            newData.put(columnName, new CustomColumnBean(baseColumnBean, TableMapRecyclerAdapter.EDIT_TYPE));
                        }
                    }

                    tableList.add(newData);
                    adapter.notifyItemInserted(tableList.size());

                    refreshTableHeight();
                    recycler_tablebody.scrollToPosition(tableList.size()-1);

                    noScrollLinearLayoutManager.setScrollEnabled(false);
//                            onToolBarClick.clickAdd();
                }else if (id == R.id.btn_edit) {
                    if (adapter.getLast_click_position() != -1) {
                        //  获取那条item数据重新设置，刷新表格
                        if (adapter.getItemViewType(adapter.getLast_click_position()) == TableMapRecyclerAdapter.SHOW_TYPE) {
                            LinkedHashMap<String, CustomColumnBean> line = adapter.getLastClickItem();
                            if (line != null) {
                                for (String columnName : line.keySet()) {
                                    CustomColumnBean customColumnBean = line.get(columnName);
                                    if (customColumnBean != null) {
                                        customColumnBean.setItemStatus(TableMapRecyclerAdapter.EDIT_TYPE);
                                        line.put(columnName, customColumnBean);
                                    }else {
                                        customColumnBean = new CustomColumnBean();
                                        customColumnBean.setItemStatus(TableMapRecyclerAdapter.EDIT_TYPE);
                                    }
                                }
                                adapter.notifyItemChanged(adapter.getLast_click_position());
                                recycler_tablebody.scrollToPosition(adapter.getLast_click_position());

                                noScrollLinearLayoutManager.setScrollEnabled(false);
                                Log.d(TAG, "开启编辑，刷新第：" + adapter.getLast_click_position() + "行的数据");
                            }
                        } else {
                            Log.e(TAG, "不可重复编辑单条item");
                        }
//                                onToolBarClick.clickEdit();
                    } else {
                        Toast.makeText(context, "请选择一条item进行编辑!", Toast.LENGTH_SHORT).show();
                    }
                } else if (id == R.id.btn_delete) {
                    if (adapter.getLast_click_position() != -1) {
                        int last_click_position = adapter.getLast_click_position();
                        LinkedHashMap<String, CustomColumnBean> line = adapter.getData().get(last_click_position);
                        LinkedHashMap<String, String> oneLine = new LinkedHashMap<>();
                        for (String columnName : line.keySet()) {
                            CustomColumnBean customColumnBean = line.get(columnName);
                            if (customColumnBean != null) {
                                oneLine.put(columnName, customColumnBean.getValue());
                            }
                        }
                        BeanResult<T> beanResult = new BeanResult<>();
                        beanResult.setPosition(last_click_position);
                        beanResult.setResult(dataSetting.reParse(oneLine));

                        if (onToolBarClick != null && onToolBarClick.clickDelete(beanResult)) {
//                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                            adapter.removeItem(adapter.getLast_click_position());
                            refreshTableHeight();
                        } else {
                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "请选择一条item进行删除!", Toast.LENGTH_SHORT).show();
                    }
                }else if (id == R.id.btn_save){
                    v.setFocusable(true);
                    v.setFocusableInTouchMode(true);
                    v.requestFocus();
                    v.requestFocusFromTouch();
                    Log.d(TAG, "通过focus调用事件");
                }
            }
        };

        btn_add.setOnClickListener(onClickListener);
        btn_edit.setOnClickListener(onClickListener);
        btn_delete.setOnClickListener(onClickListener);
        btn_save.setOnClickListener(onClickListener);
        btn_save.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if (recycler_tablebody != null) {
                        if (recycler_tablebody.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                            if (!recycler_tablebody.isComputingLayout()) {
                                if (onToolBarClick == null) return;

                                List<List<ColumnValueBean>> changeData = adapter.getChangeData();

                                if (adapter.getLast_click_position() != -1)    adapter.notifyItemChanged(adapter.getLast_click_position());
                                adapter.setLast_click_position(-1);

                                //  保存到列表的设定
                                int minPosition = -1;
                                List<LinkedHashMap<String, BeanStringInt>> tempList = new ArrayList<>();
                                for (List<ColumnValueBean> oneLine : changeData) {                //   多列结果返回，分析数据
                                    LinkedHashMap<String, BeanStringInt> oneLineRes = new LinkedHashMap<>(oneLine.size());
                                    for (int i = 0; i < oneLine.size(); i++) {
                                        ColumnValueBean oneColumn = oneLine.get(i);
                                        int position = oneColumn.getPosition();
                                        String columnName = oneColumn.getColumnName();
                                        String value = oneColumn.getValue();
                                        BeanStringInt beanStringInt = new BeanStringInt(value, position);
                                        oneLineRes.put(columnName, beanStringInt);

                                        if (i == 0){
                                            minPosition = position;
                                        }else {
                                            if (position < minPosition){
                                                minPosition = position;
                                            }
                                        }
                                    }
                                    tempList.add(oneLineRes);
                                }

                                List<BeanResult<T>> resultList = new ArrayList<>();
                                for (LinkedHashMap<String, BeanStringInt> oneLine : tempList) {            //  组合返回结果
                                    int position = -1;
                                    LinkedHashMap<String, String> temp = new LinkedHashMap<>();
                                    for (String columnName : oneLine.keySet()) {
                                        BeanStringInt beanStringInt = oneLine.get(columnName);
                                        if (beanStringInt != null) {
                                            temp.put(columnName, beanStringInt.getValue());
                                            position = beanStringInt.getPosition();
                                        }
                                    }
                                    try {
                                        T resObject = dataSetting.reParse(temp);
                                        BeanResult<T> result = new BeanResult<>();
                                        result.setPosition(position);
                                        result.setResult(resObject);
                                        resultList.add(result);
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                onToolBarClick.clickSave(tableMapView, resultList);

                                if (minPosition != -1){
                                    adapter.notifyItemRangeChanged(minPosition, (tableList.size() - minPosition));

                                    noScrollLinearLayoutManager.setScrollEnabled(true);

                                    tv_table_name.requestFocus();
                                    tv_table_name.requestFocusFromTouch();
                                }else Log.e(TAG, "未经过任何修改，不刷新表格");
                            }else  Log.e(TAG, "修改tableList时，tablebody.isComputingLayout()--->" + recycler_tablebody.isComputingLayout());
                        }else  Log.e(TAG, "修改tableList时，tablebody.getScrollState()--->" + recycler_tablebody.getScrollState());
                    }else Log.e(TAG, "修改tableList时，recyclerView为null");
                }
            }
        });

        View.OnClickListener onClickListener1 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.img_refresh) {
                    if (adapter != null && noScrollLinearLayoutManager != null) {
                        reloadData();
                    }
                }else if (v.getId() == R.id.img_bottom){
                    if (recycler_tablebody != null && tableList != null && tableList.size() != 0) {
                        try {
                            recycler_tablebody.scrollToPosition(tableList.size()-1);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        img_refresh.setOnClickListener(onClickListener1);
        img_bottom.setOnClickListener(onClickListener1);
    }

    /**
     * 分析数据
     * @return  内部维护的list
     */
    private List<LinkedHashMap<String, CustomColumnBean>> getList() {
        List<LinkedHashMap<String, CustomColumnBean>> allLine = new ArrayList<>();
        for (T oneLine : dataSetting.getDataList()) {
            LinkedHashMap<String, CustomColumnBean> onLineNewData = new LinkedHashMap<>(tableHead.size());
            List<BaseParseBean> allColumn = new ArrayList<>(tableHead.size());
            if (isShowOrder){
                allColumn.add(new BaseParseBean("序号", ""));
            }
            dataSetting.parse(oneLine, allColumn);        //  一列的数据
            for (BaseParseBean column : allColumn) {
                CustomColumnBean CustomColumnBean = new CustomColumnBean(tableHead.get(column.getColumnName()));
                CustomColumnBean.setValue(column.getValue());
                onLineNewData.put(column.getColumnName(), CustomColumnBean);
            }
            allLine.add(onLineNewData);
        }
        return allLine;
    }

    /**
     * 创建表头
     */
    private void initTableHead() {
        tv_table_name.setText(StringUtil.isEmpty(tableName)?"默认表名": tableName);
        if (isShowOrder){
            LinkedHashMap<String, BaseColumnBean> tempTableHead = new LinkedHashMap<>(tableHead);

            tableHead.clear();
            tableHead.put("序号", new BaseColumnBean("序号", 40, ItemEditType.index));
            for (String key : tempTableHead.keySet()) {
                tableHead.put(key, tempTableHead.get(key));
            }
        }

        for (String columnName : tableHead.keySet()) {
            BaseColumnBean columnValue = tableHead.get(columnName);
            if (columnValue != null) {
                TableMapRecyclerAdapter.addTextViewToLayout(context, columnValue.getWidth(), StringUtil.isEmpty(columnValue.getValue())?columnName:columnValue.getValue(), container_tablehead, headTextBold);
            }else {
                TableMapRecyclerAdapter.addTextViewToLayout(context, 40, columnName, container_tablehead, headTextBold);
            }
        }

        ViewGroup.LayoutParams layoutParams = container_tablehead.getLayoutParams();
        if (layoutParams == null) layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        if (headHeight > 0) {
            layoutParams.height = ScreenUtil.px2dp(context, headHeight);
        }else if (headHeight < 0){
            layoutParams.height = headHeight;
        }else {         //  0
            if (TableMapRecyclerAdapter.getLineHeight() > 5){
                layoutParams.height = ScreenUtil.px2dp(context, TableMapRecyclerAdapter.getLineHeight()) - ScreenUtil.px2dp(context, 5);
            } else {
                layoutParams.height = ScreenUtil.px2dp(context, 40);
            }
        }
        container_tablehead.setLayoutParams(layoutParams);
    }

    private void initListView() {
        noScrollLinearLayoutManager = new NoScrollLinearLayoutManager(context, canScroll);
        noScrollLinearLayoutManager.setScrollEnabled(true);
        recycler_tablebody.setLayoutManager(noScrollLinearLayoutManager);

        adapter = new TableMapRecyclerAdapter(context, recycler_tablebody, tableList, dataSetting);
        if (onTableBtnClickListener != null) {
            adapter.setOnSpinnerClickListener(onTableBtnClickListener.onSpinnerClickListener());
            adapter.setOnImageViewClickListener(onTableBtnClickListener.onImageViewClickListener());
        }
        if (onMyItemClickListener != null) {
            adapter.setOnMyItemClickListener(onMyItemClickListener);
        }
        if (onMyItemDoubleClickListener != null) {
            adapter.setOnMyItemDoubleClickListener(onMyItemDoubleClickListener);
        }
        if (onMyItemLongClickListener != null) {
            adapter.setOnMyItemLongClickListener(onMyItemLongClickListener);
        }

        adapter.isShowOrder(isShowOrder);
        adapter.setBase_picture_path(basePath);
        recycler_tablebody.setAdapter(adapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void bindViews() {
        tv_table_name = findViewById(R.id.tv_table_name);
        btn_add = findViewById(R.id.btn_add);
        btn_edit = findViewById(R.id.btn_edit);
        btn_delete = findViewById(R.id.btn_delete);
        container_tablehead = findViewById(R.id.container_tablehead);
        recycler_tablebody = findViewById(R.id.container_tablebody);
        btn_save = findViewById(R.id.btn_save);
        img_big = findViewById(R.id.img_big);
        img_bottom = findViewById(R.id.img_bottom);
        img_refresh = findViewById(R.id.img_refresh);
        container_table_toolbar = findViewById(R.id.container_table_toolbar);
        container_title = findViewById(R.id.container_title);
        container = findViewById(R.id.container);


        setTableTitleCanEnter(true);
    }

    /************************************************************************************************************************************************************************************************
     * interface
     * **************************************************************************************************************************************************************************************************/
    public interface dataSetting<T> {
        List<T> getDataList();
        void parse(T task, List<BaseParseBean> allColumn);
        T reParse(LinkedHashMap<String, String> oneLine);
    }

    public interface onToolBarClick<T>{
        boolean clickDelete(BeanResult<T> data);
        void clickSave(TableMapView tableMapView, List<BeanResult<T>> data);
    }

    public interface OnTableBtnClickListener {
        TableMapRecyclerAdapter.OnImageViewClickListener onImageViewClickListener();
        TableMapRecyclerAdapter.OnSpinnerClickListener onSpinnerClickListener();
    }
}
