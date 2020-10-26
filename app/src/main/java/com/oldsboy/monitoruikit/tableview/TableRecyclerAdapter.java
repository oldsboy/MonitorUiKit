package com.oldsboy.monitoruikit.tableview;

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
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oldsboy.monitoruikit.R;
import com.oldsboy.monitoruikit.tableview.dialog.Dialog_ItemDetail;
import com.oldsboy.monitoruikit.tableview.dialog.Dialog_ShowPicture;
import com.oldsboy.monitoruikit.utils.BitmapsUtil;
import com.oldsboy.monitoruikit.utils.FileUtil;
import com.oldsboy.monitoruikit.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.oldsboy.monitoruikit.utils.ScreenUtil.px2dp;
import static com.oldsboy.monitoruikit.utils.StringUtil.isEmpty;


/**
 * 通用Adapter,条纹属性、动态字段数、动态字段宽度
 */
public class TableRecyclerAdapter extends RecyclerView.Adapter<TableRecyclerAdapter.ListViewHolder> {
    public static final String TAG = "customTableAdapter";
    public static final int SHOW_TYPE = 0;
    public static final int EDIT_TYPE = 1;

    //  可以考虑以对象List的方式来加载
    //  根据对象内字段的数量来设定列表的字段的数量

    private Context context;

    private List<List<String[]>> tableList;
    private List<String[]> tableHeadList;
    private String base_picture_path;
    private int last_click_item = -1;
    private boolean needOrder;
    private boolean isLongClickShowDetail = false;

    private RecyclerView tablebody;

    private ChangeBean changeBean;

    private TableRecyclerAdapter.OnMyItemClickListener myItemClickListener;
    private TableRecyclerAdapter.OnMyItemLongClickListener onMyItemLongClickListener;
    private TableRecyclerAdapter.OnMyItemDoubleClickListener onMyItemDoubleClickListener;
    private TableView.returnDataSetting returnDataSetting;

    static String sufferString = "%待填入%";            //  占位前缀，分辨item类型的重要设置

    private static int textSize = 12;
    private static int lineHeight = 45;
    private static final int SPINNER_SIZE = 30;

    public static void setTextSize(int textSize) {
        TableRecyclerAdapter.textSize = textSize;
    }

    public static void setLineHeight(int lineHeight) {
        TableRecyclerAdapter.lineHeight = lineHeight;
    }

    public static int getLineHeight() {
        return lineHeight;
    }

    public void setEnableLongClickShowDetail(boolean isLongClickShowDetail) {
        this.isLongClickShowDetail = isLongClickShowDetail;
    }

    public interface OnMyItemClickListener<T>{
        void onItemClickListener(View v, int position, T data);
    }

    public interface OnMyItemLongClickListener<T> {
        void onItemLongClickListener(View v, int position, T data);
    }

    public interface OnMyItemDoubleClickListener<T> {
        void onItemDoubleClickListener(View v, int position, T data);
    }

    public void setMyItemClickListener(OnMyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public void setOnMyItemLongClickListener(OnMyItemLongClickListener onMyItemLongClickListener){
        this.onMyItemLongClickListener = onMyItemLongClickListener;
    }

    public void setOnMyItemDoubleClickListener(OnMyItemDoubleClickListener onMyItemDoubleClickListener){
        this.onMyItemDoubleClickListener = onMyItemDoubleClickListener;
    }

    public List<String[]> getItem(int position) {
        return tableList.get(position);
    }

    public int getLast_click_item() {
        return last_click_item;
    }

    public void setLast_click_item(int last_click_item) {
        this.last_click_item = last_click_item;
    }

    /**
     * 凡是item类型是edit的就将一行的editText值拿到
     * @return
     */
    public List<ChangeBean> getChangeData() {
        List<ChangeBean> changeData = new ArrayList<>();
        if (changeBean != null && changeBean.getPosition() != -1) {
            changeData.add(changeBean);
            Log.d(TAG, "表格更新了" + changeData.size() + "行数据");

            StringBuilder builder = new StringBuilder();
            for (String s : changeBean.getLine()) {
                builder.append(s).append("， ");
            }
            Log.d(TAG, "数据的内容：position:" + changeBean.getPosition() + "，line:" + builder.toString());
            return changeData;
        }else Log.e(TAG, "获取的changeBean的位置-1，故没有返回");
        return changeData;
    }

    private ChangeBean getLineChangeData(int position){
        if (getItemViewType(position) == EDIT_TYPE && tablebody.getLayoutManager()!= null){
            View view = tablebody.getLayoutManager().findViewByPosition(position);
            if (view != null) {
                List<View> allView = getAllChildViews(view);
                List<String> line = new ArrayList<>(tableHeadList.size());
                for (int i = 0; i < allView.size(); i++) {
                    if (allView.get(i) instanceof EditText) {
                        EditText editText = (EditText) allView.get(i);
                        String string = editText.getText().toString();
                        if (StringUtil.isEmpty(string)) string = "";
                        line.add(string);
                    }
                }
                ChangeBean changeBean = new ChangeBean();
                changeBean.setPosition(position);
                changeBean.setLine(line);
                return changeBean;
            }else {     //  大概是按了 编辑以后没有修改直接保存， 导致了view是null
                List<String[]> strings = tableList.get(position);
                List<String> line = new ArrayList<>(tableHeadList.size());
                for (String[] string : strings) {
                    String value = string[TableView.HeadIndex.value];
                    if (value != null) {
                        value = value.replaceFirst(TableRecyclerAdapter.sufferString, "");
                    }
                    line.add(value);
                }
                ChangeBean changeBean = new ChangeBean();
                changeBean.setPosition(position);
                changeBean.setLine(line);
                return changeBean;
            }
        }
        return null;
    }

    public void setLineLieValue(int position, int lie, String value){
        if (needOrder) lie += 1;

        String[] getzi = new String[]{value, tableHeadList.get(lie)[TableView.HeadIndex.width], tableHeadList.get(lie)[TableView.HeadIndex.itemType]};
        tableList.get(position).set(lie, getzi);
        notifyItemChanged(position);
    }

    public List<List<String[]>> getData(){
        return tableList;
    }

    public void setData(List<List<String[]>> newData){
        this.tableList.clear();
        this.tableList.addAll(newData);
        this.notifyDataSetChanged();
    }

    public void removeItem(int current_position) {
        this.getData().remove(getItem(current_position));
        this.notifyItemRemoved(current_position);
        this.notifyItemRangeChanged(current_position, this.getData().size() - current_position);
        last_click_item = -1;
    }

    public void setOnSpinnerClickListener(TableRecyclerAdapter.OnSpinnerClickListener onSpinnerClickListener) {
        this.onSpinnerClickListener = onSpinnerClickListener;
    }

    public TableRecyclerAdapter.OnSpinnerClickListener getOnSpinnerClickListener() {
        return onSpinnerClickListener;
    }

    private TableRecyclerAdapter.OnSpinnerClickListener onSpinnerClickListener;

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

    public void updata(List<List<String[]>> tablelist){
        this.tableList = tablelist;
        this.notifyDataSetChanged();
    }

    TableRecyclerAdapter(Context context, List<String[]> tableHeadList, List<List<String[]>> tableList, RecyclerView tablebody, String picture_base_path, boolean needOrder, TableView.returnDataSetting returnDataSetting) {
        this.tableList = tableList;
        this.context = context;
        this.tablebody = tablebody;
        this.base_picture_path = picture_base_path;
        this.tableHeadList = tableHeadList;             //拿来专门做视图
        this.needOrder = needOrder;
        this.returnDataSetting = returnDataSetting;

        changeBean = new ChangeBean();
        changeBean.setPosition(-1);
        ArrayList<String> strings = new ArrayList<>(tableHeadList.size());
        for (String[] temp : tableHeadList) {
            strings.add("");
        }
        changeBean.setLine(strings);
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListViewHolder holder = null;
        int height = px2dp(context, lineHeight);
        if (lineHeight == ViewGroup.LayoutParams.WRAP_CONTENT){
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        LinearLayout.LayoutParams rootPara = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setLayoutParams(rootPara);

        holder = new ListViewHolder(root);
        int spinner_num = 0;
        int image_num = 0;
        for (int lie = 0; lie < tableHeadList.size(); lie++) {
            String[] mg = tableHeadList.get(lie);
            int width = Integer.valueOf(mg[TableView.HeadIndex.width]);                         //  列宽[1]
            String itemType = mg[TableView.HeadIndex.itemType];                   //  下拉[2]
            String value = mg[TableView.HeadIndex.value];

            if (viewType == SHOW_TYPE){
                View view;
                if (itemType.equals(TableView.ItemEditType.imageSelect)) {
                    view = addImageViewToLayout(context, width, root);
                } else {
                    view = addTextViewToLayout(context, width, null, root, false);
                }
                holder.list.add(view);
            }else if (viewType == EDIT_TYPE){
                View view;
                if (itemType.equals(TableView.ItemEditType.textSpinner)) {
                    view = addEditSpinnerToLayout(context, width, root, spinner_num++);
                }else if (itemType.equals(TableView.ItemEditType.imageSelect)){
                    view = addEditImageViewToLayout(context, width, root, image_num++);
                }else if (itemType.equals(TableView.ItemEditType.cannotEdit)){
                    view = addEditTextToLayout(context, width, root, false, EditorInfo.TYPE_NULL);
                }else if (itemType.equals(TableView.ItemEditType.numberInput)){
                    view = addEditTextToLayout(context, width, root, true, EditorInfo.TYPE_CLASS_NUMBER);
                }else if (itemType.equals(TableView.ItemEditType.index)){
                    view = addTextViewToLayout(context, width, null, root, false);
                }else {
                    view = addEditTextToLayout(context, width, root, true, EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
                }
                holder.list.add(view);
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
                int temp_position = last_click_item;
                last_click_item = position;                                             //  刷新前后两个item的背景！
                if (temp_position != -1 && getItemViewType(temp_position) != EDIT_TYPE)
                    notifyItemChanged(temp_position);
                if (last_click_item != -1 && getItemViewType(last_click_item) != EDIT_TYPE)
                    notifyItemChanged(last_click_item);
                if (myItemClickListener != null && returnDataSetting != null){
                    myItemClickListener.onItemClickListener(holder.itemView, position, returnDataSetting.reParse(formatStringArrayToString(tableList, position, needOrder)));
                }
                if (onMyItemDoubleClickListener != null && returnDataSetting != null){
                    if (temp_position == position) {
                        onMyItemDoubleClickListener.onItemDoubleClickListener(holder.itemView, position, returnDataSetting.reParse(formatStringArrayToString(tableList, position, needOrder)));
                    }
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isLongClickShowDetail){
                    Dialog_ItemDetail dialog_itemDetail = new Dialog_ItemDetail(context, tableHeadList, tableList, position);
                    dialog_itemDetail.show();
                }else {
                    if (onMyItemLongClickListener != null && returnDataSetting != null) {
                        onMyItemLongClickListener.onItemLongClickListener(holder.itemView, position, returnDataSetting.reParse(formatStringArrayToString(tableList, position, needOrder)));
                    } else {
                        Log.e(TAG, "未设置returnDataSetting，故无法触发长按事件");
                    }
                }
                return false;
            }
        });


        if ((position + 1) % 2 != 0) {
            holder.itemView.setBackgroundResource(R.drawable.custom_table_list_2);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.custom_table_list_1);
        }
        if (last_click_item != -1){
            if (last_click_item == position){
                holder.itemView.setBackgroundResource(R.drawable.list_body_border);
            }
        }

        int cell_num = tableList.get(position).size();      //  一行的格子数量
        int itemViewType = getItemViewType(position);
        for (int lie = 0; lie < cell_num; lie++) {
            String[] mg = tableList.get(position).get(lie);
            String value = mg[TableView.HeadIndex.value];
            if (!isEmpty(value) && value.startsWith(sufferString)) {     //  值[0]
                value = value.substring(sufferString.length());
            }
            String itemType = mg[TableView.HeadIndex.itemType];                   //  下拉[2]

            if (itemViewType == EDIT_TYPE){
                if (itemType.equals(TableView.ItemEditType.imageSelect)){
                    ImageView imageView = holder.itemView.findViewById(R.id.custom_table_item_image_view1);
                    showPicture(imageView, value);
                }else if (itemType.equals(TableView.ItemEditType.index)){
                    TextView textView = holder.list.get(lie).findViewById(R.id.custom_table_item_text_view1);
                    setText(textView, String.valueOf(position));
                    continue;
                }

                final EditText editText = holder.list.get(lie).findViewById(R.id.custom_table_item_edit_text1);
                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        changeBean = getLineChangeData(position);
                    }
                };
                if (editText.getTag() != null && editText.getTag() instanceof TextWatcher){
                    editText.removeTextChangedListener((TextWatcher) editText.getTag());
                }
                editText.addTextChangedListener(textWatcher);
                editText.setTag(textWatcher);
                setText(editText, String.valueOf(value));
            }else if (itemViewType == SHOW_TYPE){
                TextView textView = holder.list.get(lie).findViewById(R.id.custom_table_item_text_view1);
                if (itemType.equals(TableView.ItemEditType.imageSelect)) {
                    ImageView imageView = holder.list.get(lie).findViewById(R.id.custom_table_item_image_view1);
                    showPicture(imageView, value);
                }else if (itemType.equals(TableView.ItemEditType.index)){
                    setText(textView, String.valueOf(position));
                    continue;
                }

                setText(textView, value);
            }
        }
    }

    static List<String> formatStringArrayToString(List<List<String[]>> tableList, int position, boolean needOrder) {
        List<String> strings = new ArrayList<>();
        if (position != -1) {
            for (int lie = 0; lie < tableList.get(position).size(); lie++) {
                if (needOrder) {
                    if (lie == 0) {
                        continue;
                    }
                }
                String[] s = tableList.get(position).get(lie);
                strings.add(s[0]);
            }
        }
        return strings;
    }

    @Override
    public int getItemViewType(int position) {
        if (tableList != null){
            List<String []> line = tableList.get(position);
            for (int i = 0; i < line.size(); i++) {
                String [] gezi = line.get(i);
                String value = gezi[TableView.HeadIndex.value];
                if (!value.startsWith(sufferString)){
                    return SHOW_TYPE;
                }
            }
        }
        return EDIT_TYPE;
    }

    @Override
    public int getItemCount() {
        if (tableList != null) {
            return tableList.size();
        } else return 0;
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

    class ListViewHolder extends RecyclerView.ViewHolder{
        List<View> list;

        ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.list = new ArrayList<>();
        }
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

        final EditText editText = getNormalEditTextView(context, 0, null, View.VISIBLE, true, EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setEnabled(false);
        linearLayout.addView(editText);

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

    private List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                //再次 调用本身（递归）
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }
}