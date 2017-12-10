package kr.co.timecapsule.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import kr.co.timecapsule.R;
import kr.co.timecapsule.dto.BoardDTO;
import kr.co.timecapsule.firebase.MyFirebaseConnector;
import kr.co.timecapsule.helper.CircleTransform;
import kr.co.timecapsule.helper.FlipAnimator;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {
    private Context mContext;
    private List<BoardDTO> messages;
    private MessageAdapterListener listener;
    private SparseBooleanArray selectedItems;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    private double latitude, longitude;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView nickname, title, content, iconText, receive_date;
        public ImageView imgProfile;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;

        public MyViewHolder(View view) {
            super(view);
            nickname = (TextView) view.findViewById(R.id.nickname_adt);
            title = (TextView) view.findViewById(R.id.title_adt);
            content = (TextView) view.findViewById(R.id.content_adt);
            receive_date = (TextView) view.findViewById(R.id.receive_date);
            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);

            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
            iconText = (TextView) view.findViewById(R.id.icon_text);

            messageContainer = (LinearLayout) view.findViewById(R.id.message_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
//            listener.onRowLongClicked(getAdapterPosition());
//            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


    public MessagesAdapter(Context mContext, List<BoardDTO> messages, MessageAdapterListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_row, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        BoardDTO message = messages.get(position);

        // displaying text view data
        holder.nickname.setText(message.getNickname());
        holder.title.setText(message.getTitle());
        holder.content.setText(message.getContents());
        holder.receive_date.setText(message.getReceive_date().getYear() + "-" + message.getReceive_date().getMonth() + "-" + message.getReceive_date().getDay() + " " + message.getReceive_date().getMonth() + ":" + message.getReceive_date().getMinutes());

        // displaying the first letter of From in icon text
        holder.iconText.setText(message.getNickname().substring(0, 1));

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

        // change the font style depending on message read status
        applyReadStatus(holder, message);

        // handle icon animation
        applyIconAnimation(holder, position);

        // display profile image
        applyProfilePicture(holder, message);

        // apply click events
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });

        holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

    private void applyProfilePicture(MyViewHolder holder, BoardDTO message) {
        if (!TextUtils.isEmpty(message.getImage_string())) {
//            Glide.with(mContext).load(message.getImage_string())
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .transform(new CircleTransform(mContext))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(holder.imgProfile);
            byte[] encodeByte = Base64.decode(message.getImage_string(), Base64.DEFAULT);
            InputStream inputStream = new ByteArrayInputStream(encodeByte);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            holder.imgProfile.setImageBitmap(bitmap);
            holder.imgProfile.setColorFilter(null);
            holder.iconText.setVisibility(View.GONE);
        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle);
            holder.imgProfile.setColorFilter(message.getColor());
            holder.iconText.setVisibility(View.VISIBLE);
        }
    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }


    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    public String getItemUser(int position) {
        return messages.get(position).getUser();
    }

    private void applyReadStatus(MyViewHolder holder, BoardDTO message) {
        if (message.isRead()) {
            holder.nickname.setTypeface(null, Typeface.NORMAL);
            holder.title.setTypeface(null, Typeface.NORMAL);
            holder.nickname.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
            holder.title.setTextColor(ContextCompat.getColor(mContext, R.color.message));
        } else {
            holder.nickname.setTypeface(null, Typeface.BOLD);
            holder.title.setTypeface(null, Typeface.BOLD);
            holder.nickname.setTextColor(ContextCompat.getColor(mContext, R.color.from));
            holder.title.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        messages.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface MessageAdapterListener {
        void onIconClicked(int position);

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }
}