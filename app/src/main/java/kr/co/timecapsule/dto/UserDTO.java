package kr.co.timecapsule.dto;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * Created by CJY on 2017-12-06.
 */
@Data
public class UserDTO {
    private String prof_img;
    private String nickname;
    private String email;
    private String token;
    private Date regdate;

}
