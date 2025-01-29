package com.palmscanner.pos.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.palmscanner.pos.viewmodel.datatype.PalmMaskedImage;
import com.palmscanner.pos.viewmodel.datatype.RegistrationStatusItem;

public class RegisterViewModel extends ViewModel {
    private MutableLiveData<PalmMaskedImage> maskedImage = new MutableLiveData<>();
    private MutableLiveData<RegistrationStatusItem> registationStatus = new MutableLiveData<>();


    public void setMaskedImage(PalmMaskedImage image) {
        maskedImage.setValue(image);
    }

    public MutableLiveData<PalmMaskedImage> getMaskedImage() {
        return maskedImage;
    }
    public void setRegistrationStatus(RegistrationStatusItem status) {
        registationStatus.setValue(status);
    }
    public MutableLiveData<RegistrationStatusItem> getRegistrationStatus() {
        return registationStatus;
    }

}
