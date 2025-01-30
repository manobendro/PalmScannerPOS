package com.palmscanner.pos.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jiebao.nfc.uartnfc.CardReaderDevice;
import com.palmscanner.pos.R;
import com.palmscanner.pos.callback.NFCReadCallBack;
import com.palmscanner.pos.viewmodel.RegisterViewModel;
import com.palmscanner.pos.viewmodel.datatype.BankCardItem;

public class RegisterNFC extends Fragment {
    private CardReaderDevice nfcCardReaderDevice;
    private NFCCardReaderThread nfcCardReaderThread;

    private RegisterViewModel mRegisterViewModel;

    public RegisterNFC() {
        super(R.layout.fragment_reg_nfc);
        this.nfcCardReaderDevice = CardReaderDevice.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.nfcCardReaderDevice.initCardReader();

        assert getActivity() != null;
        this.mRegisterViewModel = new ViewModelProvider(getActivity()).get(RegisterViewModel.class);

        nfcCardReaderThread = new NFCCardReaderThread(new NFCReadCallBack() {
            @Override
            public void onSuccess(String cardNo) {
                nfcCardReaderThread.stopNFCCardReader();
                getActivity().runOnUiThread(()->{
                    mRegisterViewModel.setBankCardData(new BankCardItem(cardNo));
                });
                Log.d("NFC", cardNo);
            }

            @Override
            public void onFailed(String msg) {
                nfcCardReaderThread.stopNFCCardReader();
                getActivity().runOnUiThread(()->{
                    mRegisterViewModel.setBankCardData(new BankCardItem(null));
                });
                Log.d("NFC", msg);
            }
        }, 20 * 1000);
        nfcCardReaderThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.nfcCardReaderDevice.deInitCardReader();
    }

    public static class NFCCardReaderThread extends Thread {

        private boolean shouldStopReading = false;
        private final CardReaderDevice reader;
        private NFCReadCallBack callBack;

        private long startTime;
        private long timeOut;

        public NFCCardReaderThread(NFCReadCallBack callBack, long timeOut) {
            super();
            this.reader = CardReaderDevice.getInstance();
            this.callBack = callBack;
            this.timeOut = timeOut;
        }

        @Override
        public void run() {
            super.run();
            this.startTime = System.currentTimeMillis();
            while (!this.shouldStopReading) {

                if (System.currentTimeMillis() - this.startTime > timeOut) {
                    try {

                        String cardNo = this.reader.readBankCardNo();
                        if (cardNo != null) {
                            this.callBack.onSuccess(cardNo);
                        }
                        Thread.sleep(200); // work 5 times in a seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    this.callBack.onFailed("Timeout.");
                }
            }

        }

        public void stopNFCCardReader() {
            this.shouldStopReading = true;
        }
    }
}
