package com.palmscanner.pos.viewmodel.datatype;

public class RegistrationStatusItem {
    private final boolean success;
    private final String msg;

    public RegistrationStatusItem(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }



    public String getMsg() {
        return msg;
    }


}
