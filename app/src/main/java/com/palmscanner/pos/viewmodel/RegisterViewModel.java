package com.palmscanner.pos.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.palmscanner.pos.viewmodel.datatype.PalmMaskedImage;

public class RegisterViewModel extends ViewModel {
    private MutableLiveData<PalmMaskedImage> maskedImage = new MutableLiveData<>();


    public void setMaskedImage(PalmMaskedImage image) {
        maskedImage.setValue(image);
    }

    public MutableLiveData<PalmMaskedImage> getMaskedImage() {
        return maskedImage;
    }

}
