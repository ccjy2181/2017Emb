package kr.co.timecapsule.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import kr.co.timecapsule.R;
import kr.co.timecapsule.dto.MessageDTO;

/**
 * Created by CJY on 2017-12-10.
 */

public class RaidRecyclerView  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<MessageDTO> items;
    private Context mContext;
    private Date current_date;

    private final int RAID = 0;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RaidRecyclerView(Context context, List<MessageDTO> items) {
        this.mContext = context;
        this.items = items;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        //More to come
//        if (items.get(position).getType().equals("0")) {
//            return DATE;
//        } else if (items.get(position).getType().equals("1")) {
//            return YOU;
//        }else if (items.get(position).getType().equals("2")) {
//            return ME;
//        }
        return RAID;
//        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.raid_notice, viewGroup, false);
        switch (viewType) {
            case RAID:
                viewHolder = new NoticeRaid(v);
                break;
            default:
                viewHolder = new NoticeRaid(v);
                break;
        }
        return viewHolder;
    }
    public void addItem(List<MessageDTO> item) {
        items.addAll(item);
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        NoticeRaid vh = (NoticeRaid) viewHolder;
        switch (viewHolder.getItemViewType()) {
            case RAID:
                configureViewHolder(vh, position);
                break;
            default:
                configureViewHolder(vh, position);
                break;
        }
    }

    private void configureViewHolder(NoticeRaid vh, int position) {
        current_date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long date = items.get(position).getReceive_date().getTime() - current_date.getTime();
        vh.getDate().setText(simpleDateFormat.format(items.get(position).getReceive_date()));
        vh.getRemaintime().setText(simpleDateFormat.format(date));
    }

}
