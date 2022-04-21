package com.oldsboy.monitoruikit.inputbtnview;

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
public class InputButtonView extends RelativeLayout{
    private Context context;
    private TextView tv;
    public EditText contentIB;
    public Button btn ;

    private String title ;
    private String button_name ;

    public InputButtonView(Context context) {
        this(context,null);
    }

    public InputButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        title = context.obtainStyledAttributes(attrs, R.styleable.InputViewButton).getString(R.styleable.InputViewButton_ib_title);
        button_name = context.obtainStyledAttributes(attrs,R.styleable.InputViewButton).getString(R.styleable.InputViewButton_button_name);
        initSelf();
    }

    public InputButtonView(Context context, AttributeSet attrs, int defStyle) {
        this(context,attrs);
    }

    private void initSelf(){
        LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.input_button_view,this);
        tv = (TextView)findViewById(R.id.selectEditViewTV);
        contentIB = (EditText)findViewById(R.id.selectEditViewET);
        btn = (Button)findViewById(R.id.selectEditViewBtn);
        tv.setText(title);
        btn.setText(button_name);
    }

    public TextView getTitle() {
        return tv;
    }
}
