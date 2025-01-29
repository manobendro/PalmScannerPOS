package com.palmscanner.pos.callback;

public interface NFCReadCallBack {
    public void onSuccess(String cardNo);
    public void onFailed(String msg);
}
