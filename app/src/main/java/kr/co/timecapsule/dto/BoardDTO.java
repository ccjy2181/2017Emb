package kr.co.timecapsule.dto;


import lombok.Data;

@Data
public class BoardDTO {
    private int id;
    private String from;
    private String subject;
    private String message;
    private String timestamp;
    private String picture;
    private boolean isImportant;
    private boolean isRead;
    private int color = -1;
}
