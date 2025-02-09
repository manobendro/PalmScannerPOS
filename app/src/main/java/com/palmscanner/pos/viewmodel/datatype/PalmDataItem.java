package com.palmscanner.pos.viewmodel.datatype;

import androidx.lifecycle.ViewModel;

public class PalmDataItem extends ViewModel {

    private byte[] palmToken;
    private String uuid;

    public PalmDataItem(byte[] palmToken, String uuid) {
        this.palmToken = palmToken;
        this.uuid = uuid;
    }

    public byte[] getPalmToken() {
        return palmToken;
    }

    public void setPalmToken(byte[] palmToken) {
        this.palmToken = palmToken;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
