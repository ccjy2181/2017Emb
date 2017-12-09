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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import kr.co.timecapsule.MainActivity;
import kr.co.timecapsule.R;
import kr.co.timecapsule.SelectGalleryResolver;


public class FragmentMyInfo extends Fragment {

    private static final int PICK_FROM_ALBUM = 1;
    //권한 추가
    private static final String[] MY_PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSIONS = 1;

    SelectGalleryResolver GalleryResolver;
    ImageView profile_img;

    public FragmentMyInfo(){ setHasOptionsMenu(true); }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_info, null, false);

        getActivity().supportInvalidateOptionsMenu();
        ((MainActivity)getActivity()).changeTitle(R.id.toolbar, "내 정보");

        profile_img = (ImageView)view.findViewById(R.id.character_image);

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    boolean permission = hasAllPermissionsGranted();
                    Log.e("test","permission : "+permission);
                    if(!permission)
                        return;
                }
                getGalley();
            }
        });
        return view;
    }

    private void getGalley() {
        Intent intent = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public boolean hasAllPermissionsGranted() {
        for (String permission : MY_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity() , MY_PERMISSIONS, REQUEST_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    Bitmap bm = null;
                    try {
                        bm = getThumbNail(returnImg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    profile_img.setImageBitmap(bm);
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
//
//
//                bmOptions.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(String.valueOf(uri), bmOptions);
//
//                BitmapFactory.Options opts = new BitmapFactory.Options();
//                Bitmap bm = BitmapFactory.decodeFile(String.valueOf(uri), opts);
//                System.out.println("############################################################################");
//                System.out.println("Strign U"+ String.valueOf(uri));
//                System.out.println("U"+ uri);
//                System.out.println("############################################################################");
//
//                ExifInterface exif = new ExifInterface(String.valueOf(uri));
//                String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
//                int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
//                int rotationAngle = 0;
//                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
//                if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
//                if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
//
//                Matrix matrix = new Matrix();
//                matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
//                Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bmOptions.outWidth, bmOptions.outHeight, matrix, true);
//
//                System.out.println("############################################################################");
//                System.out.println(bmOptions.outWidth);
//                System.out.println(bmOptions.outHeight);
//                System.out.println("############################################################################");
//
//
//                if(thumbnail.getHeight() > thumbnail.getWidth()){
//                    thumbnail = imgRotate(thumbnail);
//                    System.out.println("*****************************************************ok?");
//                }
                thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                        getActivity().getContentResolver(), ImageId,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        bmOptions);

            } else {
                Toast.makeText(getActivity(), "불러올수 없는 이미지 입니다.", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        }
        return thumbnail;
    }

    // 썸네일 회전, 일단 보류
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
