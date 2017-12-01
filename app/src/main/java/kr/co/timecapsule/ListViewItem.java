package kr.co.timecapsule;

/**
 * Created by mg556 on 2017-11-23.
 */

public class ListViewItem {
    private String id;
    private String contentsStr;
    private String numberInt;
    private String titleStr;
    private String writerStr;
    private String dateStr;
    private String viewsInt;

    public void setNumber(String number) {
        numberInt = number;
    }

    public void setTitle(String title){
        titleStr = title;
    }

    public void setContents(String contents){
        contentsStr = contents;
    }

    public void setWriter(String writer){
        writerStr = writer;
    }

    public void setDate(String date){
        dateStr = date;
    }

    public void setViews(String views){
        viewsInt = views;
    }

    public String getNumber(){
        return this.numberInt;
    }

    public String getTitle(){
        return this.titleStr;
    }

    public String getContents(){
        return this.contentsStr;
    }

    public String getWriter(){
        return this.writerStr;
    }

    public String getDate(){
        return this.dateStr;
    }

    public String getViews(){
        return this.viewsInt;
    }
}
