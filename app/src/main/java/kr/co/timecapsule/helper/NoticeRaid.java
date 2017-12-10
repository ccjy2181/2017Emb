package kr.co.timecapsule.helper;

import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import kr.co.timecapsule.R;

/**
 * Created by CJY on 2017-12-10.
 */

public class NoticeRaid extends RecyclerView.ViewHolder {

        private static final int COUNT_DOWN_INTERVAL = 1000;
        private TextView date, remain_time;
        private CountDownTimer countDownTimer;
        private long timer;

        public NoticeRaid(View v) {
                super(v);
                date = (TextView) v.findViewById(R.id.open_time);
                remain_time = (TextView) v.findViewById(R.id.remain_time);
                if (timer > 0) {
                        countDownTimer();
                        countDownTimer.start();
                } else {
                        remain_time.setText("열렸습니다!");
                }
        }

        public void countDownTimer(){
                countDownTimer = new CountDownTimer(timer, COUNT_DOWN_INTERVAL) {
                        public void onTick(long millisUntilFinished) {
                                remain_time.setText(String.valueOf(timer));
                                timer--;
                        }
                        public void onFinish() {
                                remain_time.setText(String.valueOf("열렸습니다!"));
                        }
                };
        }

        public TextView getDate() {
                return date;
                }

        public void setDate(TextView date) {
                this.date = date;
                }

        public TextView getRemaintime() {
                return remain_time;
        }

        public void setRemaintime(TextView remain_time) {
                this.remain_time = remain_time;
        }
        public void setTimer(long timer){
                this.timer = timer;
        }
}
