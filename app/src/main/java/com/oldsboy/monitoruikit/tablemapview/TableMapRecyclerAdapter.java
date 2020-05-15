package com.oldsboy.monitoruikit.tablemapview;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oldsboy.monitoruikit.R;
import com.oldsboy.monitoruikit.tablemapview.bean.BeanResult;
import com.oldsboy.monitoruikit.tablemapview.bean.CustomColumnBean;
import com.oldsboy.monitoruikit.tableview.dialog.Dialog_ShowPicture;
import com.oldsboy.monitoruikit.utils.BitmapsUtil;
import com.oldsboy.monitoruikit.utils.FileUtil;
import com.oldsboy.monitoruikit.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.oldsboy.monitoruikit.utils.ScreenUtil.px2dp;
import static com.oldsboy.monitoruikit.utils.StringUtil.isEmpty;


/**
 * 通用Adapter,条纹属性、动态字段数、动态字段宽度
 */
public class TableMapRecyclerAdapter extends RecyclerView.Adapter<TableMapRecyclerAdapter.ListViewHolder> {
    public static final String TAG = "customTableAdapter";
    public static final int SHOW_TYPE = 0;
    public static final int EDIT_TYPE = 1;

    //  可以考虑以对象List的方式来加载
    //  根据对象内字段的数量来设定列表的字段的数量

    private Context context;

    private List<LinkedHashMap<String, CustomColumnBean>> tableList;
    private String base_picture_path;
    private int last_click_position = -1;
    private boolean isShowOrder;

    private RecyclerView tablebody;

    private ColumnValueBean changeBean;

    private TableMapRecyclerAdapter.OnMyItemClickListener onMyItemClickListener;
    private TableMapRecyclerAdapter.OnMyItemLongClickListener onMyItemLongClickListener;
    private TableMapRecyclerAdapter.OnMyItemDoubleClickListener onMyItemDoubleClickListener;
    private TableMapView.dataSetting dataSetting;

    private static int textSize = 12;
//    private static int lineHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
    private static int lineHeight = 40;
    private static final int SPINNER_SIZE = 30;

    /**
     * function
     */
    public static void setTextSize(int textSize) {
        TableMapRecyclerAdapter.textSize = textSize;
    }

    public static void setLineHeight(int lineHeight) {
        TableMapRecyclerAdapter.lineHeight = lineHeight;
    }

    public static int getLineHeight() {
        return lineHeight;
    }

    public void isShowOrder(boolean isShowOrder) {
        this.isShowOrder = isShowOrder;
    }

    public void setBase_picture_path(String base_picture_path){
        this.base_picture_path = base_picture_path;
    }

    public interface OnMyItemClickListener<T>{
        void onItemClickListener(View v, int position, BeanResult<T> data);
    }

    public interface OnMyItemLongClickListener<T> {
        void onItemLongClickListener(View v, int position, BeanResult<T> data);
    }

    public interface OnMyItemDoubleClickListener<T> {
        void onItemDoubleClickListener(View v, int position, BeanResult<T> data);
    }

    public void setOnMyItemClickListener(OnMyItemClickListener myItemClickListener) {
        this.onMyItemClickListener = myItemClickListener;
    }

    public void setOnMyItemLongClickListener(OnMyItemLongClickListener onMyItemLongClickListener){
        this.onMyItemLongClickListener = onMyItemLongClickListener;
    }

    public void setOnMyItemDoubleClickListener(OnMyItemDoubleClickListener onMyItemDoubleClickListener){
        this.onMyItemDoubleClickListener = onMyItemDoubleClickListener;
    }

    public void setOnSpinnerClickListener(TableMapRecyclerAdapter.OnSpinnerClickListener onSpinnerClickListener) {
        this.onSpinnerClickListener = onSpinnerClickListener;
    }

    public TableMapRecyclerAdapter.OnSpinnerClickListener getOnSpinnerClickListener() {
        return onSpinnerClickListener;
    }

    private TableMapRecyclerAdapter.OnSpinnerClickListener onSpinnerClickListener;

    public interface OnSpinnerClickListener {
        void onSpinner0Click(View v, LinearLayout root, EditText editText);
        void onSpinner1Click(View v, LinearLayout root, EditText editText);
        void onSpinner2Click(View v, LinearLayout root, EditText editText);
        void onSpinner3Click(View v, LinearLayout root, EditText editText);
        void onSpinner4Click(View v, LinearLayout root, EditText editText);
        void onSpinner5Click(View v, LinearLayout root, EditText editText);
        void onSpinner6Click(View v, LinearLayout root, EditText editText);
    }

    public void setOnImageViewClickListener(OnImageViewClickListener onImageViewClickListener) {
        this.onImageViewClickListener = onImageViewClickListener;
    }

    private OnImageViewClickListener onImageViewClickListener;

    public interface OnImageViewClickListener {
        void onImageView0Click(View v, LinearLayout root, ImageView imgView, EditText editText);
        void onImageView1Click(View v, LinearLayout root, ImageView imgView, EditText editText);
    }

    public void removeItem(int current_position) {
        tableList.remove(getItem(current_position));
        this.notifyItemRemoved(current_position);
        this.notifyItemRangeChanged(current_position, this.getData().size() - current_position);
        last_click_position = -1;
    }

    public LinkedHashMap<String, CustomColumnBean> getItem(int position) {
        return tableList.get(position);
    }

    public int getLast_click_position() {
        return last_click_position;
    }

    public void setLast_click_position(int last_click_position) {
        this.last_click_position = last_click_position;
    }

    public LinkedHashMap<String, CustomColumnBean> getLastClickItem(){
        if (last_click_position != -1 && last_click_position < tableList.size()){
            return tableList.get(last_click_position);
        }
        return null;
    }

    public List<LinkedHashMap<String, CustomColumnBean>> getData(){
        return tableList;
    }

    public void setData(List<LinkedHashMap<String, CustomColumnBean>> newData){
        this.tableList.clear();
        this.tableList.addAll(newData);
        this.notifyDataSetChanged();
    }

    public void setSingleValue(int position, String columnName, String value){
        LinkedHashMap<String, CustomColumnBean> oneLine = tableList.get(position);
        CustomColumnBean customColumnBean = oneLine.get(columnName);
        if (customColumnBean != null) {
            customColumnBean.setValue(value);
        }
    }

    public void setEditValue(RecyclerView tablebody, List<LinkedHashMap<String, CustomColumnBean>> tableList, int position, String columnName, String value){
        if (tablebody != null) {
            if (tablebody.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                if (!tablebody.isComputingLayout()) {
                    if (tableList.size() > position) {
                        LinkedHashMap<String, CustomColumnBean> oneLine = tableList.get(position);
                        CustomColumnBean oneGe = oneLine.get(columnName);
                        if (oneGe != null) {
                            oneGe.setValue(value);
                            oneGe.setItemStatus(EDIT_TYPE);
                        }
                        Log.d(TAG, "修改了第" + position + " 行的tableList，现在列名为：" + columnName + " 的值是：" + value);
                    }
                }else  Log.e(TAG, "修改tableList时，tablebody.isComputingLayout()--->" + tablebody.isComputingLayout());
            }else  Log.e(TAG, "修改tableList时，tablebody.getScrollState()--->" + tablebody.getScrollState());
        }else Log.e(TAG, "修改tableList时，recyclerView为null");
    }

    /**
     * 凡是status类型是1的就将一行的editText值拿到
     * @return  所有status为1的数据
     */
    public List<List<ColumnValueBean>> getChangeData() {
        List<List<ColumnValueBean>> changeData = new ArrayList<>();
        for (int position = 0; position < tableList.size(); position++) {
            LinkedHashMap<String, CustomColumnBean> oneLine = tableList.get(position);
            List<ColumnValueBean> oneLineRes = new ArrayList<>();
            for (String columnName : oneLine.keySet()) {
                CustomColumnBean lieValue = oneLine.get(columnName);
                if (lieValue != null && lieValue.getItemStatus() == EDIT_TYPE){         //  改变过了的
                    oneLineRes.add(new ColumnValueBean(position, columnName, lieValue.getValue()));
                    lieValue.setItemStatus(SHOW_TYPE);          //  获取过以后设置回显示状态
                }
            }
            if (oneLineRes.size() > 0) {
                changeData.add(oneLineRes);
            }
        }
        return changeData;
    }

    /**
     * content
     */

    TableMapRecyclerAdapter(Context context, RecyclerView tablebody, List<LinkedHashMap<String, CustomColumnBean>> tableList, TableMapView.dataSetting dataSetting) {
        this.tableList = tableList;
        this.context = context;
        this.tablebody = tablebody;
        this.dataSetting = dataSetting;
    }

    @Override
    public int getItemViewType(int position) {
        LinkedHashMap<String, CustomColumnBean> oneLine = tableList.get(position);
        int edit_num = 0;
        for (int i = 0; i < oneLine.keySet().size(); i++) {
            String columnName = oneLine.keySet().iterator().next();
            CustomColumnBean geziValue = oneLine.get(columnName);
            if (geziValue != null && geziValue.getItemStatus() == EDIT_TYPE){
                edit_num++;
            }
        }

        if (edit_num == oneLine.keySet().size()){
            return EDIT_TYPE;
        }else {
            return SHOW_TYPE;
        }
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListViewHolder holder = null;
        int height = px2dp(context, lineHeight);
        if (lineHeight < 0){
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        LinearLayout.LayoutParams rootPara = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setLayoutParams(rootPara);

        holder = new ListViewHolder(root);
        if (tableList.size() > 0){
            LinkedHashMap<String, CustomColumnBean> firstHang = tableList.get(0);           //  拿第一行的宽度配置，其实拿表头的配置更合理，不过必要性不大
            int spinner_num = 0;
            int image_num = 0;
            for (String columnName : firstHang.keySet()) {                                  //  创建每列View
                CustomColumnBean baseColumnBean = firstHang.get(columnName);
                if (baseColumnBean != null) {
                    int width = baseColumnBean.getWidth();
                    int itemType = baseColumnBean.getItemType();

                    if (viewType == SHOW_TYPE){
                        View view;
                        if (itemType== TableMapView.ItemEditType.imageSelect) {
                            view = addImageViewToLayout(context, width, root);
                        } else {
                            view = addTextViewToLayout(context, width, null, root, false);
                        }
                        holder.mapView.put(columnName, view);
                    }else if (viewType == EDIT_TYPE){
                        View view;
                        if (itemType== TableMapView.ItemEditType.textSpinner) {
                            view = addEditSpinnerToLayout(context, width, root, spinner_num++);
                        }else if (itemType== TableMapView.ItemEditType.imageSelect){
                            view = addEditImageViewToLayout(context, width, root, image_num++);
                        }else if (itemType== TableMapView.ItemEditType.cannotEdit){
                            view = addEditTextToLayout(context, width, root, false, EditorInfo.TYPE_NULL);
                        }else if (itemType== TableMapView.ItemEditType.numberInput){
                            view = addEditTextToLayout(context, width, root, true, EditorInfo.TYPE_CLASS_NUMBER);
                        }else if (itemType== TableMapView.ItemEditType.index){
                            view = addTextViewToLayout(context, width, null, root, false);
                        }else {
                            view = addEditTextToLayout(context, width, root, true, EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
                        }
                        holder.mapView.put(columnName, view);
                    }
                }
            }
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, final int position) {
        if (lineHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.itemView.setLayoutParams(layoutParams);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp_position = last_click_position;
                last_click_position = position;                                             //  刷新前后两个item的背景！
                if (temp_position != -1 && getItemViewType(temp_position) != EDIT_TYPE)
                    notifyItemChanged(temp_position);
                if (last_click_position != -1 && getItemViewType(last_click_position) != EDIT_TYPE)
                    notifyItemChanged(last_click_position);
                if (onMyItemClickListener != null) {
                    LinkedHashMap<String, CustomColumnBean> oneLine = tableList.get(position);
                    LinkedHashMap<String, String> oneLineResult = new LinkedHashMap<>();
                    for (String columnName : oneLine.keySet()) {
                        CustomColumnBean customColumnBean = oneLine.get(columnName);
                        if (customColumnBean != null) {
                            oneLineResult.put(columnName, customColumnBean.getValue());
                        }
                    }
                    BeanResult beanResult = new BeanResult();
                    beanResult.setPosition(last_click_position);
                    beanResult.setResult(dataSetting.reParse(oneLineResult));
                    onMyItemClickListener.onItemClickListener(v, position, beanResult);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onMyItemLongClickListener != null) {
                    LinkedHashMap<String, CustomColumnBean> oneLine = tableList.get(position);
                    LinkedHashMap<String, String> oneLineResult = new LinkedHashMap<>();
                    for (String columnName : oneLine.keySet()) {
                        CustomColumnBean customColumnBean = oneLine.get(columnName);
                        if (customColumnBean != null) {
                            oneLineResult.put(columnName, customColumnBean.getValue());
                        }
                    }
                    BeanResult beanResult = new BeanResult();
                    beanResult.setPosition(last_click_position);
                    beanResult.setResult(dataSetting.reParse(oneLineResult));
                    onMyItemLongClickListener.onItemLongClickListener(v, position, beanResult);
                }
                return false;
            }
        });


        if ((position + 1) % 2 != 0) {
            holder.itemView.setBackgroundResource(R.drawable.custom_table_list_2);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.custom_table_list_1);
        }
        if (last_click_position != -1){
            if (last_click_position == position){
                holder.itemView.setBackgroundResource(R.drawable.list_body_border);
            }
        }

        LinkedHashMap<String, CustomColumnBean> firstHang = tableList.get(position);
        int status = getItemViewType(position);
        if (status == EDIT_TYPE) Log.d(TAG, "反应第" + position + "行是编辑状态");
        for (final String columnName : firstHang.keySet()) {
            CustomColumnBean baseColumnBean = firstHang.get(columnName);
            if (baseColumnBean != null){
                String value = baseColumnBean.getValue();
                int itemType = baseColumnBean.getItemType();

                View geziRoot = holder.mapView.get(columnName);
                if (geziRoot != null) {
                    if (status == SHOW_TYPE) {
                        TextView textView = geziRoot.findViewById(R.id.custom_table_item_text_view1);
                        if (itemType == TableMapView.ItemEditType.imageSelect) {
                            ImageView imageView = geziRoot.findViewById(R.id.custom_table_item_image_view1);
                            showPicture(imageView, value);
                        }else if (itemType == TableMapView.ItemEditType.index){
                            setText(textView, String.valueOf(position));
                            continue;
                        }

                        setText(textView, value);
                    } else if (status == EDIT_TYPE) {
                        if (itemType == TableMapView.ItemEditType.imageSelect) {
                            ImageView imageView = geziRoot.findViewById(R.id.custom_table_item_image_view1);
                            showPicture(imageView, value);
                        }else if (itemType == TableMapView.ItemEditType.index){
                            TextView textView = geziRoot.findViewById(R.id.custom_table_item_text_view1);
                            setText(textView, String.valueOf(position));
                            continue;
                        }

                        final EditText editText = geziRoot.findViewById(R.id.custom_table_item_edit_text1);
                        setText(editText, value);
                        TextWatcher textWatcher = new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                String value = s.toString();
                                setEditValue(tablebody, tableList, position, columnName, value);
                            }
                        };
                        if (editText.getTag() != null && editText.getTag() instanceof TextWatcher) {
                            editText.removeTextChangedListener((TextWatcher) editText.getTag());
                        }
                        editText.addTextChangedListener(textWatcher);
                        editText.setTag(textWatcher);
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (tableList != null) {
            return tableList.size();
        } else return 0;
    }

    class ListViewHolder extends RecyclerView.ViewHolder{
        LinkedHashMap<String, View> mapView;

        ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mapView = new LinkedHashMap<>();
        }
    }

    @interface Type{
        int Type_Spinner = 0;
        int Type_Image = 1;
    }
    private void setBtnClick(@Type int type, Button button, final int func_num, final LinearLayout root, final EditText editText, final ImageView imageView){
        View.OnClickListener onClickListener = null;
        if (type == Type.Type_Spinner){
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (func_num){
                        case 0:
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner0Click(v, root, editText);
                            }
                            break;
                        case 1:
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner1Click(v, root, editText);
                            }
                            break;
                        case 2:
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner2Click(v, root, editText);
                            }
                            break;
                        case 3:
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner3Click(v, root, editText);
                            }
                            break;
                        case 4:
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner4Click(v, root, editText);
                            }
                            break;
                        case 5:
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner5Click(v, root, editText);
                            }
                            break;
                        case 6:
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner6Click(v, root, editText);
                            }
                            break;
                    }
                }
            };
        }else if (type == Type.Type_Image){
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (func_num){
                        case 0:
                            if (onImageViewClickListener != null){
                                onImageViewClickListener.onImageView0Click(v, root, imageView, editText);
                            }
                            break;
                        case 1:
                            if (onImageViewClickListener != null){
                                onImageViewClickListener.onImageView1Click(v, root, imageView, editText);
                            }
                            break;
                    }
                }
            };
        }
        button.setOnClickListener(onClickListener);
    }


    /**
     * baseView
     */
    public static View addTextViewToLayout(Context context, int width, String value, LinearLayout root, boolean headTextBold) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);

        linearLayout.addView(getNormalTextView(context, width-1, value, View.VISIBLE, headTextBold));

        linearLayout.addView(getNormal1pxView(context));
        root.addView(linearLayout);
        return linearLayout;
    }

    public View addEditTextToLayout(Context context, int width, LinearLayout root, boolean enable, int input_type) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);

        linearLayout.addView(getNormalEditTextView(context, width-1, null, View.VISIBLE, enable, input_type));

        linearLayout.addView(getNormal1pxView(context));
        root.addView(linearLayout);
        return linearLayout;
    }

    private View addEditSpinnerToLayout(Context context, int width, final LinearLayout root, int func_num) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);

        final EditText editText;
        linearLayout.addView(editText = getNormalEditTextView(context, width-1-SPINNER_SIZE, null, View.VISIBLE, true, EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE));

        Button button = getNormalButton(context, px2dp(context, SPINNER_SIZE));
        setBtnClick(Type.Type_Spinner, button, func_num, root, editText, null);
        linearLayout.addView(button);

        linearLayout.addView(getNormal1pxView(context));

        root.addView(linearLayout);
        return linearLayout;
    }

    private View addEditImageViewToLayout(Context context, int width, final LinearLayout root, int func_num) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);

        final EditText editText;
        linearLayout.addView(editText = getNormalEditTextView(context, 0, null, View.VISIBLE, true, EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE));

        final ImageView imgView;
        linearLayout.addView(imgView = getNormalImageView(context, width-1-SPINNER_SIZE));


        Button button = getNormalButton(context, px2dp(context, SPINNER_SIZE));
        setBtnClick(Type.Type_Image, button, func_num, root, editText, imgView);
        linearLayout.addView(button);

        linearLayout.addView(getNormal1pxView(context));

        root.addView(linearLayout);
        return linearLayout;
    }

    private View addImageViewToLayout(Context context, int width, final LinearLayout root) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);

        final TextView textView;
        linearLayout.addView(textView = getNormalTextView(context, 0, null, View.VISIBLE, false));

        final ImageView imgView;
        linearLayout.addView(imgView = getNormalImageView(context, width-1));

        linearLayout.addView(getNormal1pxView(context));

        root.addView(linearLayout);
        return linearLayout;
    }

    private static TextView getNormalTextView(Context context, int width, String value, int visible, boolean headTextBold){
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams tvPara = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tvPara.weight = 1;
        textView.setLayoutParams(tvPara);
        textView.setText(value);            //  此处已经绑定值了
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(textSize);
        if (headTextBold){
            TextPaint paint = textView.getPaint();
            paint.setFakeBoldText(true);
        }
        textView.setVisibility(visible);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setId(R.id.custom_table_item_text_view1);
        textView.setPadding(px2dp(context, 4), px2dp(context, 5), px2dp(context, 4), px2dp(context, 5));
        textView.setTextColor(Color.parseColor("#646464"));
        return textView;
    }

    private EditText getNormalEditTextView(Context context, int width, String value, int visible, boolean enable, int input_type){
        EditText editText = new EditText(context);
        LinearLayout.LayoutParams tvPara = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tvPara.gravity = Gravity.CENTER_VERTICAL;
        tvPara.topMargin = 0;
        tvPara.bottomMargin = 0;
        tvPara.leftMargin = 0;
        tvPara.rightMargin = 0;
        tvPara.weight = 1;
        editText.setLayoutParams(tvPara);
        editText.setText(value);            //  此处已经绑定值了
        editText.setBackgroundResource(0);
        editText.setGravity(Gravity.START|Gravity.TOP);
        editText.setVisibility(visible);
        editText.setSingleLine(false);
        editText.setInputType(input_type);
        editText.setHorizontallyScrolling(false);
        editText.setMinLines(1);
        editText.setMaxLines(30);
        editText.setEnabled(enable);
        editText.setTextSize(textSize);
        editText.setId(R.id.custom_table_item_edit_text1);
        editText.setPadding(0, 0, 0, 0);
        editText.setTextColor(Color.parseColor("#646464"));
        return editText;
    }

    private static Button getNormalButton(Context context, int width){
        Button button = new Button(context);
        LinearLayout.LayoutParams btnParam = new LinearLayout.LayoutParams(width, px2dp(context, SPINNER_SIZE));
        btnParam.gravity = Gravity.CENTER_VERTICAL;
        button.setLayoutParams(btnParam);
        button.setBackgroundResource(R.drawable.btn_edit_right);
        button.setId(R.id.custom_table_item_btn1);
        return button;
    }

    private ImageView getNormalImageView(final Context context, int width){
        ImageView imgView = new ImageView(context);
        int height = px2dp(context, lineHeight - 2);
        if (lineHeight < 0){
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        LinearLayout.LayoutParams tvPara = new LinearLayout.LayoutParams(px2dp(context, width), height);
        tvPara.gravity = Gravity.CENTER_VERTICAL;
        imgView.setLayoutParams(tvPara);
        showPicture(imgView, null);
        imgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imgView.setVisibility(View.VISIBLE);
        imgView.setId(R.id.custom_table_item_image_view1);
        return imgView;
    }

    private void showPicture(ImageView imageView, final String img_name){
        if (imageView != null) {
            try {
                if (this.base_picture_path != null && !this.base_picture_path.isEmpty()){
                    final String file_path = this.base_picture_path + "/" + img_name;
                    if (StringUtil.isEmpty(img_name)){
                        imageView.setImageBitmap(null);
                    }else if (FileUtil.isFileExists(file_path) && new File(file_path).length() > 0 && FileUtil.isFile(file_path)){
                        imageView.setImageBitmap(BitmapsUtil.decodeFilePath(file_path));
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialog_ShowPicture dialog_showPicture = new Dialog_ShowPicture(context, file_path);
                                dialog_showPicture.show();
                            }
                        });
                    }else {
                        Log.e(TAG, "图片损坏！图片路径：" + file_path);
                        imageView.setImageResource(R.drawable.custom_picture_break);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                imageView.setImageResource(R.drawable.custom_picture_break);
            }
        }else Log.e(TAG, "设置的imageView控件是null！imageName-->" + img_name);
    }

    private void setText(TextView textView, String string){
        if (textView != null) {
            if (!isEmpty(string)) {
                textView.setText(string);
            }else {
                textView.setText("");
            }
        }else Log.e(TAG, "设置的TextView是null！string-->" + string);
    }

    private static View getNormal1pxView(Context context){
        LinearLayout.LayoutParams vPara = new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
        View view = new View(context);
        view.setBackgroundResource(R.color.table_line);
        view.setLayoutParams(vPara);
        return view;
    }
}