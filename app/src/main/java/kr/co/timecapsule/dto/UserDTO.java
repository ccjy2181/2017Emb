package kr.co.timecapsule.dto;

import java.util.Date;

import lombok.Data;

/**
 * Created by CJY on 2017-12-06.
 */
@Data
public class UserDTO {
    private String nickname;
    private String email;
    private String token;
    private Date regdate;

    public UserDTO(String nickname, String email, String token, Date regdate){
        this.nickname = nickname;
        this.email = email;
        this.token = token;
        this.regdate = regdate;
    }
}
