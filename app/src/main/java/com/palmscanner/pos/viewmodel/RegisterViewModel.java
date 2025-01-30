package com.palmscanner.pos.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.palmscanner.pos.viewmodel.datatype.BankCardItem;
import com.palmscanner.pos.viewmodel.datatype.PalmMaskedImage;
import com.palmscanner.pos.viewmodel.datatype.RegistrationStatusItem;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<PalmMaskedImage> maskedImage = new MutableLiveData<>();
    private final MutableLiveData<RegistrationStatusItem> registrationStatus = new MutableLiveData<>();

    private final MutableLiveData<BankCardItem> bankCardData = new MutableLiveData<>();

    public MutableLiveData<PalmMaskedImage> getMaskedImage() {
        return maskedImage;
    }

    public void setMaskedImage(PalmMaskedImage image) {
        maskedImage.setValue(image);
    }

    public MutableLiveData<RegistrationStatusItem> getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatusItem status) {
        registrationStatus.setValue(status);
    }

    public MutableLiveData<BankCardItem> getBankCardData() {
        return bankCardData;
    }

    public void setBankCardData(BankCardItem bankCardItem) {
        bankCardData.setValue(bankCardItem);
    }

}
