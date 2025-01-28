package com.palmscanner.pos.threads;

import android.content.Context;

import com.palmscanner.pos.PosApp;
import com.palmscanner.pos.callback.PalmRegistrationCallback;
import com.saintdeem.palmvein.SDPVUnifiedAPI;
import com.saintdeem.palmvein.bean.EnrollPicture;
import com.saintdeem.palmvein.bean.EnrollResult;
import com.saintdeem.palmvein.bean.UnifiedMsg;
import com.saintdeem.palmvein.callback.PalmEnroll;
import com.saintdeem.palmvein.device.bean.CaptureResult;
import com.saintdeem.palmvein.device.bean.DeviceMsg;
import com.saintdeem.palmvein.service.SDPVServiceConstant;
import com.saintdeem.palmvein.service.bean.DetectRoiResult;
import com.saintdeem.palmvein.service.bean.ServiceMsg;

public class PalmRegistrationThread extends Thread implements PalmEnroll {
    private boolean shouldStopRegistration = false;
    private SDPVUnifiedAPI sdpvUnifiedAPI;
    private final Context mContext;
    private PalmRegistrationCallback callback;
    private final PosApp app;

    private int retryCount = 5;

    public PalmRegistrationThread(Context mContext, PalmRegistrationCallback callback, int retryCount) {
        this.mContext = mContext;
        this.callback = callback;

        sdpvUnifiedAPI = SDPVUnifiedAPI.getInstance();
        app = (PosApp) mContext.getApplicationContext();
        sdpvUnifiedAPI.setEnrollListener(this);
        if (retryCount > 0) this.retryCount = retryCount;
    }

    @Override
    public void run() {
        super.run();
        while (!this.shouldStopRegistration) {
            try {
                if (app.hasPermission()) {
                    if (sdpvUnifiedAPI.isDeviceConnect()) {
                        //Take image
                        DeviceMsg<CaptureResult> image = sdpvUnifiedAPI.captureImage();
                        this.callback.onImageCaptured(sdpvUnifiedAPI.maskImage(image.getData().getImage()).getData());

                        //detect roi
                        ServiceMsg<DetectRoiResult> roi = sdpvUnifiedAPI.detectRoi(image.getData().getImage());
                        if (roi.resultCode == SDPVServiceConstant.RETURN_SERVICE_SUCCESS) {
                            sdpvUnifiedAPI.enroll(new EnrollPicture(roi.getData().getImageRoi(), image.getData().getImage()));
                        }else if( this.retryCount <= 0){
                            this.callback.onRegistrationFailed("No ROI found");
                            this.shouldStopRegistration = true;
                        }else{
                            this.retryCount--;
                        }
                    } else {
                        sdpvUnifiedAPI.initDevice(mContext);
                    }
                } else {
                    //TODO: check device permission
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopRegistration() {
        shouldStopRegistration = true;
    }

    @Override
    public void enrollComplete(EnrollResult enrollResult) {
        if (this.callback != null) {
            this.callback.onRegistrationSuccess(enrollResult.getTmpl(), enrollResult.getImages());
            this.shouldStopRegistration = true;
        }
    }

    @Override
    public void enrollFail(UnifiedMsg<String> unifiedMsg) {
        if (this.callback != null) {
            this.callback.onRegistrationFailed(unifiedMsg.getData());
        }
    }

    @Override
    public void enrollTimes(int i) {

    }

    @Override
    public void enrollTips(String s) {

    }
}
