package kr.co.timecapsule.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
@Data
public class MessageDTO implements Serializable {
    private String user;
    private String title;
    private String contents;
    private double location_latitude;
    private double location_longitude;
    private String image_string;
    private Date receive_date;

    //add data
    private String key;
}