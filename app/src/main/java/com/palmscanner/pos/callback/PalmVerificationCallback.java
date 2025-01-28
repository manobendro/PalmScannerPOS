package com.palmscanner.pos.callback;

public interface PalmVerificationCallback {
    public void onVerificationSuccess(String palmToken, String successMessage);
    public void onVerificationFailed(String errorMessage);
    public void onImageCaptured(byte[] imageBitmap);
}
