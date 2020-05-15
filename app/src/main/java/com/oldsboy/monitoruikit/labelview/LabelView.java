package com.oldsboy.monitoruikit.labelview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oldsboy.monitoruikit.R;


/**
 * Created by sven on 14-3-8.
 */
public class LabelView extends LinearLayout{
    public TextView contentTV;
    private String title;

    private Context context ;
    public LabelView(Context context) {
        this(context,null);
    }

    public LabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context ;
        title =  context.obtainStyledAttributes(attrs,R.styleable.LabelView).getString(R.styleable.LabelView_label_title);
        initSelf();
    }

    public LabelView(Context context, AttributeSet attrs, int defStyle) {
        this(context,attrs);
    }

    private void initSelf(){
        LayoutInflater inflater ;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.lable_view, this);
        TextView labelTV = (TextView) findViewById(R.id.lableTv);
        contentTV = (TextView)findViewById(R.id.lableVa);
        labelTV.setText(title);
    }
}
