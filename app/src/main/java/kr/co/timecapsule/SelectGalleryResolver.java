package kr.co.timecapsule;

import android.*;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by mg556 on 2017-12-07.
 */

public class SelectGalleryResolver extends AppCompatActivity {
    private static final int PICK_FROM_ALBUM = 1;
    ImageView mycharacter_image;

    //권한 추가
    private static final int REQUEST_PERMISSIONS = 1;
    private static final String[] MY_PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public boolean hasAllPermissionsGranted() {
        for (String permission : MY_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, MY_PERMISSIONS, REQUEST_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            int index = 0;
            for (String permission : permissions) {
                if(permission.equalsIgnoreCase(android.Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    getGalley();
                    break;
                }
                index++;
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_info);

        mycharacter_image = (ImageView) findViewById(R.id.character_image);
//        Button button = (Button) findViewById(R.id.button);
        // 아이콘 클릭시 갤러리 권한 선택
        mycharacter_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(SelectGalleryResolver.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    boolean permission = hasAllPermissionsGranted();
                    Log.e("test","permission : "+permission);
                    if(!permission)
                        return;
                }
                getGalley();
            }
        });
    }

    private void getGalley() {
        Intent intent = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("test","data : "+data);
                if(data != null) {
                    Uri returnImg = data.getData();
                    if("com.google.android.apps.photos.contentprovider".equals(returnImg.getAuthority())) {
                        for(int i=0;i<returnImg.getPathSegments().size();i++) {
                            String temp = returnImg.getPathSegments().get(i);
                            if(temp.startsWith("content://")) {
                                returnImg = Uri.parse(temp);
                                break;
                            }
                        }
                    }
                    // 썸네일 가져오기
                    Bitmap bm = getThumbNail(returnImg);
                    mycharacter_image.setImageBitmap(bm);
                }
            }
        }
    }

    private Bitmap getThumbNail(Uri uri) {
        Log.d("test","from uri : "+uri);
        String[] filePathColumn = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE/*, MediaStore.Images.Media.ORIENTATION*/};

        ContentResolver cor = getContentResolver();
        //content 프로토콜로 리턴되기 때문에 실제 파일의 위치로 변환한다.
        Cursor cursor = cor.query(uri, filePathColumn, null, null, null);

        Bitmap thumbnail = null;
        if(cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            long ImageId = cursor.getLong(columnIndex);
            if(ImageId != 0) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                        getContentResolver(), ImageId,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        bmOptions);
            } else {
                Toast.makeText(this, "불러올수 없는 이미지 입니다.", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        }
        return thumbnail;
    }
}
