package com.oldsboy.monitoruikit.selectedit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oldsboy.monitoruikit.R;


/**
 * Created by sven on 14-3-8.
 */
public class SelectEditView extends RelativeLayout{
    private Context context;
    private TextView tv;
    public EditText contentET;
    public Button btn ;

    private String title ;

    public SelectEditView(Context context) {
        this(context,null);
    }

    public SelectEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        title = context.obtainStyledAttributes(attrs, R.styleable.SelectEditView).getString(R.styleable.SelectEditView_se_title);

        initSelf();
    }

    public SelectEditView(Context context, AttributeSet attrs, int defStyle) {
        this(context,attrs);
    }

    private void initSelf(){
        LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.select_edit_view,this);
        tv = (TextView)findViewById(R.id.selectEditViewTV);
        contentET = (EditText)findViewById(R.id.selectEditViewET);
        btn = (Button)findViewById(R.id.selectEditViewBtn);
        tv.setText(title);
    }
}
