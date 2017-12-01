package kr.co.timecapsule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mg556 on 2017-11-23.
 */

public class ListViewAdapter extends BaseAdapter {
    // Adaptor에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    // ListViewAdapter의 생성자
    public ListViewAdapter(){

    }

    // Adapter에 사용되는 테이터의 개수를 리턴
    public int getCount(){
        return listViewItemList.size();
    }

    // 지정한 위치(position)에 있느 데이터 리턴 : 필수구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }


    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴 : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "Listview_item" Layout을 inflate 하여 convertView 참조 획득.
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        // 화면에 표시된 View(Layout에 inflate된)으로 부터 위젯에 대한 참조 획득
        TextView numberTextView = (TextView) convertView.findViewById(R.id.list_number);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.list_title);
        TextView writerTextView = (TextView) convertView.findViewById(R.id.list_writer);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.list_date);
        TextView viewsTextView = (TextView) convertView.findViewById(R.id.list_views);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        numberTextView.setText(listViewItem.getNumber());
        titleTextView.setText(listViewItem.getTitle());
        writerTextView.setText(listViewItem.getWriter());
        dateTextView.setText(listViewItem.getDate());
        viewsTextView.setText(listViewItem.getViews());

        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    // _id, number, title, contents, writer, date, views
    public void addItem(String number, String title, String writer, String date, String views){
        ListViewItem item = new ListViewItem();

        item.setNumber(number);
        item.setTitle(title);
        item.setWriter(writer);
        item.setDate(date);
        item.setViews(views);

        listViewItemList.add(item);
    }
}
