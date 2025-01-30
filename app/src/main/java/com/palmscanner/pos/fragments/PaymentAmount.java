package com.palmscanner.pos.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.palmscanner.pos.MainActivity;
import com.palmscanner.pos.R;
import com.palmscanner.pos.viewmodel.PaymentViewModel;
import com.palmscanner.pos.viewmodel.datatype.PaymentItem;

import java.util.Random;

public class PaymentAmount extends Fragment implements View.OnClickListener {

    private PaymentViewModel mPaymentViewModel;

    public PaymentAmount() {
        super(R.layout.fragment_pay_amount);
    }

    EditText mAmount;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Init views
        view.findViewById(R.id.payment_confirm).setOnClickListener(this);
        view.findViewById(R.id.payment_cancel).setOnClickListener(this);
        mAmount = view.findViewById(R.id.input_box_payment);
        //Init view model
        mPaymentViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.payment_confirm) {
            if (!mAmount.getText().toString().isEmpty()) {
                mPaymentViewModel.setPaymentItem(new PaymentItem(String.valueOf(new Random().nextInt()), Integer.parseInt(mAmount.getText().toString())));
            }

        } else if (v.getId() == R.id.payment_cancel) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            assert getActivity() != null;
            getActivity().finish();
        }
    }
}
