package kr.co.timecapsule.system;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageManager {

    InputStream inputStream;
    Bitmap bitmap;

    public String encodingImageData(String imageURL){
        return bitMapToString(getImageBitmap(getHtmlData(imageURL)));
    }

    public Bitmap decodingImageData(String s){
        return StringToBitMap(s);
    }

    private String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream ByteStream=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    private InputStream getHtmlData(String s){
        URLConnector urlConnector = new URLConnector();
        return urlConnector.starter(true, s);
    }

    private Bitmap getImageBitmap(InputStream is){
        System.out.println("getImageBitmap__________________________");
        System.out.println(is);

        inputStream = is;

        Thread mThread = new Thread() {
            @Override
            public void run() {
                //  이미지 뷰에 지정할 Bitmap을 생성하는 과정
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        };

        mThread.start();

        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

}
