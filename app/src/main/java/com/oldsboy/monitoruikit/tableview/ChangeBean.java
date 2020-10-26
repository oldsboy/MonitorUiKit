package com.oldsboy.monitoruikit.tableview;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views
 * @ClassName: ColumnValueBean
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/16 17:38
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/16 17:38
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ChangeBean {
    private int position;
    private List<String> line;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<String> getLine() {
        return line;
    }

    public void setLine(List<String> line) {
        this.line = line;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (line != null) {
            builder.append("此changeBean值如下：").append("\n");
            builder.append("位置是：").append(position).append("\n");
            for (int i = 0; i < line.size(); i++) {
                builder.append("一行中第").append(i + 1).append("个值是：").append(line.get(i)).append("\n");
            }
        }
        return builder.toString();
    }
}
