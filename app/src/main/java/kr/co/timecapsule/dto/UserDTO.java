package kr.co.timecapsule.dto;

import java.util.Date;

import lombok.Data;

/**
 * Created by CJY on 2017-12-06.
 */
@Data
public class UserDTO {
    private String token;
    private Date regdate;
}
