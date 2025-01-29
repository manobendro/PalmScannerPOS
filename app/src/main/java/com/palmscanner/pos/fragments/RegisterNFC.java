package com.palmscanner.pos.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jiebao.nfc.uartnfc.CardReaderDevice;
import com.palmscanner.pos.R;

public class RegisterNFC extends Fragment {
    private CardReaderDevice nfcCardReaderDevice;

    public RegisterNFC() {
        super(R.layout.fragment_reg_nfc);
        this.nfcCardReaderDevice = CardReaderDevice.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.nfcCardReaderDevice.initCardReader();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.nfcCardReaderDevice.deInitCardReader();
    }

    public static class NFCCardReaderThread extends Thread {

        private boolean shouldStopReading = false;
        private CardReaderDevice reader;
        public NFCCardReaderThread() {
            super();
            this.reader = CardReaderDevice.getInstance();
        }

        @Override
        public void run() {
            super.run();

            while (!this.shouldStopReading) {
                try {

                    String cardNo = this.reader.readBankCardNo();

                    Thread.sleep(200); // work 5 times in a seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        public void stopNFCCardReader() {
            this.shouldStopReading = true;
        }
    }
}
