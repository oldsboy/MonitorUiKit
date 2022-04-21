package com.oldsboy.monitoruikit.inputview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oldsboy.monitoruikit.R;


/**
 * Created by sven on 14-3-8.
 */
public class InputView extends LinearLayout{
    private Context context ;

    private TextView titleTV ;
    public EditText contentTV ;

    private String title;
    public InputView(Context context) {
        this(context,null);
    }

    public InputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context ;
        title =  context.obtainStyledAttributes(attrs, R.styleable.InputView).getString(R.styleable.InputView_input_title);
        initSelf();
    }

    public InputView(Context context, AttributeSet attrs, int defStyle) {
        this(context,attrs);
    }

    private void initSelf(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.input_view, this);
        titleTV = (TextView)findViewById(R.id.inputTV);
        contentTV = (EditText)findViewById(R.id.inputET);
        titleTV.setText(title);
    }

    public TextView getTitle() {
        return titleTV;
    }


}
