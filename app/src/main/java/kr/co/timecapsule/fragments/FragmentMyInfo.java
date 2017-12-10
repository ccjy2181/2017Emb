package kr.co.timecapsule.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
    private FirebaseStorage firebaseStorage;
    private StorageReference rootReference;
    private StorageReference pathReference;
    private StorageReference storageRef;
    private UploadTask uploadTask;

    private Uri oriPath;
    private String prof_img;
    public String img_info;
//    private StorageReference storageImagesRef;

    public FragmentMyInfo(){ setHasOptionsMenu(true); }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_info, null, false);

        // 사용자 정보를 받아옴
        mAuth = FirebaseAuth.getInstance();

        // 사용자 정보(닉네임)을 받아오기 위해 firebase에 접근
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("user").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserDTO user = dataSnapshot.getValue(UserDTO.class);

                String pro_str = user.getProf_img();
                System.out.println("user info: " + user);
//                Uri pro_uri = Uri.parse(pro_str);

                System.out.println("uri: " + pro_str);
                if (!pro_str.isEmpty()) {
                    // DB에서 사진 불러오기
                    // 사진정보 확인 지금 null
//                    System.out.println(img_info);
                    storageRef = firebaseStorage.getReferenceFromUrl("gs://embed-member.appspot.com/");
                    pathReference = storageRef.child("profile_img").child(mAuth.getCurrentUser().getUid()).child("27889");
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Bitmap originbm;
                            System.out.println("getDownloadUrl Success!!!!!!!!!!!!!!!!!!");
                            try {
                                oriPath = uri;
                                originbm = getThumbNail(oriPath);
                                profile_img.setImageBitmap(originbm);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("getDownloadUrl Fail!!!!!!!!!!!!!!!!!!");
                        }
                    });
                }else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        storageRef = firebaseStorage.getReferenceFromUrl("gs://embed-member.appspot.com/");
//
//        pathReference = storageRef.child("profile_img").child(mAuth.getCurrentUser().getUid()+"/prof_img");

        getActivity().supportInvalidateOptionsMenu();
        ((MainActivity)getActivity()).changeTitle(R.id.toolbar, "내 정보");

//        Bitmap originbm;
//        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Bitmap originbm;
//                System.out.println("getDownloadUrl Success!!!!!!!!!!!!!!!!!!");
//                try {
//                    oriPath = uri;
//                    originbm = getThumbNail(oriPath);
//                    profile_img.setImageBitmap(originbm);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                System.out.println("getDownloadUrl Fail!!!!!!!!!!!!!!!!!!");
//            }
//        });
//        String originimageID = mAuth.getCurrentUser().getUid();
//        oriPath = Uri.parse("gs://embed-member.appspot.com" + "/profile_img" + originimageID);

//        try {
//            originbm = getThumbNail(oriPath);
//            System.out.println(originbm);
//            if(originbm == null){
//                profile_img = (ImageView)view.findViewById(R.id.character_image);
//            }else {
//                profile_img.setImageBitmap(originbm);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        profile_img.setImageBitmap(originbm);
        profile_img = (ImageView)view.findViewById(R.id.character_image);

        //로컬 파일에서 업로드

        rootReference = firebaseStorage.getReferenceFromUrl("gs://embed-member.appspot.com");

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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    profile_img.setImageBitmap(bm);

                    // Firebase Storage에 이미지 저장
                    StorageReference reference = rootReference.child("profile_img").child(mAuth.getCurrentUser().getUid());
                    StorageReference riversRef = reference.child(returnImg.getLastPathSegment());
                    // 체크
                    img_info = returnImg.getLastPathSegment();

                    System.out.println("getLastPathSegment: " + returnImg.getLastPathSegment());
                    uploadTask = riversRef.putFile(returnImg);

                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Success!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            System.out.println("oriPath: " +returnImg);

                            prof_img = returnImg.toString();
                            System.out.println("oriPath: " +returnImg);

                            // 현재 접속 아이디의 prof_img에 사진 URI를 저장
                            databaseReference.child("user").child(mAuth.getCurrentUser().getUid()+"/prof_img").setValue(prof_img);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Fail!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        }
                    });

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
