package kr.co.timecapsule.system;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class URLConnector {
    private InputStream result;
    private String url;
    private boolean isSynchronized;
    private CountDownLatch latch;

    public InputStream starter(boolean isSynchronized, String url){
        this.isSynchronized = isSynchronized;
        this.url = url;

        if(this.isSynchronized){
            this.latch = new CountDownLatch(1);
        }

        new Thread() {
            public void run() {
                connection();
            }
        }.start();

        if(this.isSynchronized){
            try{
                this.latch.await();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        return result;
    }

    public void connection(){
        try {
            // URL 객체 생성
            URL url = new URL(this.url);

            String response = "";

            // URLConnection 생성
            HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.connect();

            this.result = httpUrlConnection.getInputStream();
            if(this.isSynchronized) {
                this.latch.countDown();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}