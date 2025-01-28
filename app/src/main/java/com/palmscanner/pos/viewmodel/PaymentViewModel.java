package com.palmscanner.pos.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.palmscanner.pos.viewmodel.datatype.PalmMaskedImage;
import com.palmscanner.pos.viewmodel.datatype.PaymentItem;

public class PaymentViewModel extends ViewModel {
    private final MutableLiveData<PaymentItem> paymentItem = new MutableLiveData<>();
    private final MutableLiveData<PalmMaskedImage> palmMaskedImage = new MutableLiveData<>();

    public void setPaymentItem(PaymentItem item) {
        paymentItem.setValue(item);
    }

    public LiveData<PaymentItem> getPaymentItem() {
        return paymentItem;
    }

    public void setPalmMaskedImage(PalmMaskedImage item) {
        palmMaskedImage.setValue(item);
    }

    public LiveData<PalmMaskedImage> getPalmMaskedImage() {
        return palmMaskedImage;
    }
}
