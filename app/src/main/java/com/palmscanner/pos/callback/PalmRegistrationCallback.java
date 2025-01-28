package com.palmscanner.pos.callback;

import java.util.List;

public interface PalmRegistrationCallback {
    public void onRegistrationSuccess(byte[] palmToken, List<byte[]> images);
    public void onRegistrationFailed(String errorMessage);
    public void onImageCaptured(byte[] imageBitmap);
}
