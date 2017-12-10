package kr.co.timecapsule.dto;


import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class BoardDTO implements Serializable {
    private String user;
    private String nickname;
    private String title;
    private String contents;
    private Date receive_date;
    private String image_string;
    private boolean isRead;
    private int color = -1;
    //add data
    private String key;
}
