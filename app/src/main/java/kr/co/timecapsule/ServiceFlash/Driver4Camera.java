package kr.co.timecapsule.ServiceFlash;

import android.hardware.Camera;
import android.util.Log;

public class Driver4Camera {
    private Camera camera;

    private static final String LOG_TAG = "Driver4Camera";

    public Driver4Camera() {
        // Create an instance of Camera
        camera = getCameraInstance(getCameraId());
    }

    public int getCameraId() {
        Log.d(LOG_TAG, "getCameraId()");
        int cameraId = -1;
        // Search for the back facing camera (or any camera)
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK || numberOfCameras == 1) {
                Log.d(LOG_TAG, "CameraInfo.CAMERA_FACING_BACK = "
                        + (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK));
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public static Camera getCameraInstance(int cameraId) {
        Log.d(LOG_TAG, "getCameraInstance(" + cameraId + ")");
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance
            Camera.Parameters cp = c.getParameters();
            Log.d(LOG_TAG, "getCameraInstance(" + cameraId + "): Camera.Parameters = "
                    + cp.flatten());
        } catch (Exception e) {
            Log.d(LOG_TAG, "Camera.open(" + cameraId + ") exception=" + e);
        }
        Log.d(LOG_TAG, "getCameraInstance(" + cameraId + ") = " + c);
        return c; // returns null if camera is unavailable
    }

    public Camera getCamera() {
        return camera;
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release(); // release the camera for other applications
            camera = null;
        }
    }

    public void onPause() {
        releaseCamera();
    }

    public void onResume() {
        if (camera == null) {
            camera = getCameraInstance();
        }

    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    private static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


}