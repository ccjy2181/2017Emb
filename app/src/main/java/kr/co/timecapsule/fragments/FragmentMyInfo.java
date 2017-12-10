package kr.co.timecapsule.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.co.timecapsule.IDListDbHelper;
import kr.co.timecapsule.ImageDbHelper;
import kr.co.timecapsule.MainActivity;
import kr.co.timecapsule.R;
import kr.co.timecapsule.SelectGalleryResolver;
import kr.co.timecapsule.dto.UserDTO;
import kr.co.timecapsule.firebase.MyFirebaseConnector;


public class FragmentMyInfo extends Fragment {

    private static final int PICK_FROM_ALBUM = 1;
    //권한 추가
    private static final String[] MY_PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSIONS = 1;

    ImageView profile_img;
    TextView tv_nickname, tv_email;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;


    // local DB부분
    private ImageDbHelper imageDbHelper;
    private SQLiteDatabase Image_DB;
    Cursor mCursor;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Image.db";

    public FragmentMyInfo(){ setHasOptionsMenu(true); }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        imageDbHelper = new ImageDbHelper(getActivity(), DATABASE_NAME, null, DATABASE_VERSION);
        Image_DB = imageDbHelper.getWritableDatabase();
        Cursor c = Image_DB.rawQuery("SELECT image FROM IMAGETABLE WHERE _id=1", null);
        c.moveToNext();

        if(c.getCount() == 0){
            // 공백의 데이터 삽입
            Image_DB.execSQL("INSERT INTO IMAGETABLE VALUES (null, ' ');");
        }else{
            byte[] bytes = c.getBlob(0);
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            profile_img.setImageBitmap(bm);
        }

        final View view = inflater.inflate(R.layout.fragment_my_info, null, false);

        // 사용자 정보를 받아옴
        mAuth = FirebaseAuth.getInstance();

        // 사용자 정보(닉네임)을 받아오기 위해 firebase에 접근
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        getActivity().supportInvalidateOptionsMenu();
        ((MainActivity)getActivity()).changeTitle(R.id.toolbar, "내 정보");

        profile_img = (ImageView)view.findViewById(R.id.character_image);

        //로컬 파일에서 업로드

        //firebase에 접근하여 실제 데이터를 받아와서 textview에 출력
        databaseReference.child("user").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        tv_nickname = (TextView)view.findViewById(R.id.myinfo_user_nickname) ;
                        tv_email = (TextView)view.findViewById(R.id.myinfo_user_email);
                        UserDTO user = dataSnapshot.getValue(UserDTO.class);

                        if(user!=null) {
                            String nickname = user.getNickname();
                            String email = user.getEmail();
                            tv_nickname.setText(nickname);
                            tv_email.setText(email);
                        } else {
                            tv_nickname.setText("익명");
                            tv_email.setText("익명 이메일");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = tv_nickname.getText().toString();
                // 비회원은 갤러리에서 사진을 받아와 저장하는 것이 불가능, 회원만 가능
                if( n != "익명") {
                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        boolean permission = hasAllPermissionsGranted();
                        Log.e("test", "permission : " + permission);
                        if (!permission)
                            return;
                    }
                    getGalley();
                }else{
                    Toast.makeText(getActivity(), "비회원은 사진을 등록할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    // 갤러리로 이동
    private void getGalley() {
        Intent intent = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    // 권한 받아오기
    public boolean hasAllPermissionsGranted() {
        for (String permission : MY_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity() , MY_PERMISSIONS, REQUEST_PERMISSIONS);
                return false;
            }
        }
        return true;
    }


    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("test","data : "+data);
                if(data != null) {
                    final Uri returnImg = data.getData();
                    String filepath = data.getData().getPath();
                    Uri file = Uri.parse("media" + filepath);
                    if("com.google.android.apps.photos.contentprovider".equals(returnImg.getAuthority())) {
                        for(int i=0;i<returnImg.getPathSegments().size();i++) {
                            String temp = returnImg.getPathSegments().get(i);

                        }
                    }

                    // 썸네일 가져오기
                    Bitmap bm = null;
                    try {
                        bm = getThumbNail(returnImg);
                        profile_img.setImageBitmap(bm);
                        // local DB에 저장
                        Image_DB.update("IMAGETABLE", bm, "_id=1", null);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private Bitmap getThumbNail(Uri uri) throws IOException {
        Log.d("test","from uri : "+uri);
        String[] filePathColumn = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE/*, MediaStore.Images.Media.ORIENTATION*/};

        ContentResolver cor = getActivity().getContentResolver();
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
                        getActivity().getContentResolver(), ImageId,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        bmOptions);

                // 이미지 가로가 세로보다 클 경우 이미지가 옆으로 눞혀보이는 것을 방지
                if(thumbnail.getHeight() < thumbnail.getWidth()){
                    thumbnail = imgRotate(thumbnail);
                }

            } else {
                Toast.makeText(getActivity(), "불러올수 없는 이미지 입니다.", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        }
        return thumbnail;
    }

    // 썸네일 회전
    private Bitmap imgRotate(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(-90);

        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        bmp.recycle();

        return resizedBitmap;
    }

}
