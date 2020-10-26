package com.oldsboy.monitoruikit.tableview.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.oldsboy.monitoruikit.R;
import com.oldsboy.monitoruikit.tableview.TableView;

import java.util.List;


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
public class Dialog_ItemDetail extends BaseDialog {
    private ImageView imgContent;
    private LinearLayout containerDetail;
    
    private List<String[]> tableHeadList;
    private List<String[]> oneLineData;
    private int position;
    
    public Dialog_ItemDetail(Context context, List<String[]> tableHeadList, List<List<String[]>> tableList, int position) {
        super(context);
        this.tableHeadList = tableHeadList;
        this.oneLineData = tableList.get(position);
        this.position = position;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_item_detail);
        
        bindViews();
        try {
            initData();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "生成失败！表头字段数和数据字段数不一致！", Toast.LENGTH_SHORT).show();
        }
    }

    private void initData() throws IndexOutOfBoundsException{
        //  根据列名生成view
        if (tableHeadList.size() == oneLineData.size()){
            for (int i = 0; i < tableHeadList.size(); i++) {
                String columnName = tableHeadList.get(i)[TableView.HeadIndex.value];
                String value = oneLineData.get(i)[TableView.HeadIndex.value];
                String itemType = tableHeadList.get(i)[TableView.HeadIndex.itemType];

                if (!itemType.equals(TableView.ItemEditType.cannotEdit)){
                    View itemRoot = LayoutInflater.from(getContext()).inflate(R.layout.item_dialog_detail, containerDetail, false);
                    TextView tvColumnName = (TextView) itemRoot.findViewById(R.id.tv_column_name);
                    TextView tvContent = (TextView) itemRoot.findViewById(R.id.tv_content);
                    if (itemType.equals(TableView.ItemEditType.index)){
                        tvColumnName.setText("序号");
                        tvContent.setText(String.valueOf(position));
                    }else {
                        tvColumnName.setText(columnName);
                        tvContent.setText(value);
                    }

                    containerDetail.addView(itemRoot);
                }

                if (i % 2 != 0){
                    containerDetail.setBackgroundResource(R.drawable.custom_table_list_2);
                }else {
                    containerDetail.setBackgroundResource(R.drawable.custom_table_list_1);
                }
            }
        }
    }

    private void bindViews() {
        ImageView imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgContent != null) {
                    imgContent.setImageBitmap(null);
                    imgContent.setImageDrawable(null);
                }
                dismiss();
            }
        });
        imgContent = (ImageView) findViewById(R.id.img_content);
        containerDetail = (LinearLayout) findViewById(R.id.container_detail);
    }
}
