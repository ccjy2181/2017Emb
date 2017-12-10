package kr.co.timecapsule.ServiceFlash;

import android.hardware.Camera;
import android.util.Log;

public class Flash {
    public static boolean FLASH_STATUS = false;
    private static Driver4Camera driver4Camera = null;

    public static void flash_on() {
        try {
            Log.w("UtilFlash", "flash_on!!");
            if (!FLASH_STATUS) {//플래쉬 상태가 True(켜진상태)이면 다시 킬 필요없으므로 상태 체크!
                camera_release();//이전 카메라가 혹시나 남아있다면 release를 해줘야 됩니다.
                driver4Camera = new Driver4Camera();//Camera를 생성하기위한 Driver. 밑에 소스올려놓겠습니다.
                Camera camera = driver4Camera.getCamera();//Camera를 받아옵니다.
                Camera.Parameters p = camera.getParameters();//Camera에 Prameters 클래스 객체를 생성합니다.
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);//카메라에 Mode를 설정한 파라미터 객체를 Set합니다.
                camera.startPreview();//카메라를 실행시키는 건아니지만 실행시키는것처럼 해줘야 정상작동합니다.
                FLASH_STATUS = true;//플래쉬가 켜졌으므로 상태값을 true.
            }
        } catch (Exception e) {
            Log.w("UtilFlash", e);

        }

    }

    public static void flash_off() {
        try {
            if (FLASH_STATUS) {
                if (null != driver4Camera.getCamera()) {
                    camera_release();
                }
                FLASH_STATUS = false;//플래쉬가 꺼졌으므로 상태값을 false.
            }
        } catch (Exception e) {
            Log.w("UtilFlash", e);
        }
    }

    public static void camera_release() {
        if (driver4Camera == null)
            return;
        if (null != driver4Camera.getCamera()) {
            driver4Camera.getCamera().release();//플래쉬를 끄면 더이상 카메라는 켜져있으면 안됩니다.
            //카메라는 2개이상이 켜지면 오류가나기때문에 꼭 꺼지면서 카메라를 Release합니다.

            driver4Camera = null;

        }
    }

}
