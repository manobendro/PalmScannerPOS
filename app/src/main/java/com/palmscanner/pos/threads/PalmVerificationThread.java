package com.palmscanner.pos.threads;

import android.content.Context;

import com.palmscanner.pos.PosApp;
import com.palmscanner.pos.callback.PalmVerificationCallback;
import com.saintdeem.palmvein.SDPVUnifiedAPI;
import com.saintdeem.palmvein.device.bean.CaptureResult;
import com.saintdeem.palmvein.device.bean.DeviceMsg;
import com.saintdeem.palmvein.service.SDPVServiceConstant;
import com.saintdeem.palmvein.service.bean.DetectRoiResult;
import com.saintdeem.palmvein.service.bean.ServiceMsg;

public class PalmVerificationThread extends Thread {

    //TODO: Should implement timeout feature

    private boolean shouldStopVerification = false;
    private final Context mContext;
    private final PalmVerificationCallback mPalmVerificationCallback;
    private long timeout;
    private long startTime;
    private final PosApp app; // Make this final
    private final SDPVUnifiedAPI sdpvUnifiedAPI; // Make this final
    private static final int DEFAULT_NUMBER_OF_RETRY = 10 * 1000; // Default retry value

    public PalmVerificationThread(Context mContext, PalmVerificationCallback mPalmVerificationCallback, int timeout) {
        super("PalmVerificationThread");
        this.mContext = mContext;
        this.timeout = timeout > 0 ? timeout : DEFAULT_NUMBER_OF_RETRY; // using default if invalid value
        this.mPalmVerificationCallback = mPalmVerificationCallback;
        this.app = (PosApp) mContext.getApplicationContext();
        this.sdpvUnifiedAPI = SDPVUnifiedAPI.getInstance();
    }

    @Override
    public void run() {
        this.startTime = System.currentTimeMillis();
        final int[] retryCount = {5};
        while (!shouldStopVerification) {
            try {
                if (System.currentTimeMillis() - startTime < timeout) {
                    if (app.hasPermission()) {
                        if (sdpvUnifiedAPI.isDeviceConnect()) {

                            //capture image
                            DeviceMsg<CaptureResult> result = sdpvUnifiedAPI.captureImage();
                            this.mPalmVerificationCallback.onImageCaptured(sdpvUnifiedAPI.maskImage(result.getData().getImage()).getData());

                            //detect roi
                            ServiceMsg<DetectRoiResult> serviceMsg = sdpvUnifiedAPI.detectRoi(result.getData().getImage());

                            //if there is roi
                            if (serviceMsg.resultCode == SDPVServiceConstant.RETURN_SERVICE_SUCCESS) {
                                sdpvUnifiedAPI.identifyFeature(serviceMsg.getData().getImageRoi(), (code, msg, token) -> {
                                    if (code == SDPVServiceConstant.RETURN_SERVICE_SUCCESS) {
                                        stopVerification();//Stop verification coz already verified palm
                                        this.mPalmVerificationCallback.onVerificationSuccess(token, msg);
                                    }else{
                                        if(retryCount[0] > 0){
                                            retryCount[0]--;
                                        }else {
                                            stopVerification();//After numberOfRetry times, stop verification
                                            this.mPalmVerificationCallback.onVerificationFailed("Not registered.");
                                        }
                                    }
                                });
                            }

                        } else {
                            //TODO: try to connect device and init
                            SDPVUnifiedAPI.getInstance().initDevice(mContext);
                        }


                    } else {
                        //todo: try to gain permission
                        throw new Exception("There is no usb permission for palm scanner.");
                    }
                } else {
                    stopVerification();//After numberOfRetry times, stop verification
                    this.mPalmVerificationCallback.onVerificationFailed("No palm detected.");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopVerification() {
        this.shouldStopVerification = true;
    }
}
