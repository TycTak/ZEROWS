package com.tyctak.zerowslib;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewHolder_TotalPrice {

    private LinearLayout mCurrencySymbol;
    private LinearLayout mPaymentTotal;
    private LinearLayout mBeforeDiscount;
    private LinearLayout mSaving;

    public ViewHolder_TotalPrice(View itemView) {
        initialiseDisplay(itemView);

        mCurrencySymbol = (LinearLayout) itemView.findViewWithTag("llCurrencySymbol");
        mPaymentTotal = (LinearLayout) itemView.findViewWithTag("llPaymentTotal");
        mBeforeDiscount = (LinearLayout) itemView.findViewWithTag("llBeforeDiscount");
        mSaving = (LinearLayout) itemView.findViewWithTag("llSaving");
    }

    private void initialiseDisplay(View view) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

//        TextView txtCurrencySymbol = new TextView(view.getContext());
//        txtCurrencySymbol.setLayoutParams(layoutParams);
//        txtCurrencySymbol.setTextSize(48);
////        txtCurrencySymbol.setForegroundGravity(Gravity.LEFT);
//        txtCurrencySymbol.setMaxLines(1);
//        txtCurrencySymbol.setClickable(false);
//        // ########## txtCurrencySymbol
//        LinearLayout llCurrencySymbol = view.findViewWithTag("llCurrencySymbol");
//        llCurrencySymbol.addView(txtCurrencySymbol);

//        TextView txtPaymentTotal = new TextView(view.getContext());
//        txtPaymentTotal.setLayoutParams(layoutParams);
//        txtPaymentTotal.setTextSize(48);
////        txtPaymentTotal.setForegroundGravity(Gravity.RIGHT);
//        txtPaymentTotal.setMaxLines(1);
//        txtPaymentTotal.setClickable(false);
//        // ########## txtPaymentTotal
//        LinearLayout llPaymentTotal = view.findViewWithTag("llPaymentTotal");
//        llPaymentTotal.addView(txtPaymentTotal);

        TextView txtBeforeDiscount = new TextView(view.getContext());
        txtBeforeDiscount.setLayoutParams(layoutParams);
        txtBeforeDiscount.setTextSize(16);
        txtBeforeDiscount.setTypeface(null, Typeface.BOLD);
        txtBeforeDiscount.setMaxLines(1);
        txtBeforeDiscount.setClickable(false);
        // ########## txtBeforeDiscount
        LinearLayout llBeforeDiscount = view.findViewWithTag("llBeforeDiscount");
        llBeforeDiscount.addView(txtBeforeDiscount);

        TextView txtSaving = new TextView(view.getContext());
        txtSaving.setLayoutParams(layoutParams);
        txtSaving.setTextSize(16);
        txtSaving.setTypeface(null, Typeface.BOLD);
        txtSaving.setMaxLines(1);
        txtSaving.setClickable(false);
        // ########## txtBeforeDiscount
        LinearLayout llSaving = view.findViewWithTag("llSaving");
        llSaving.addView(txtSaving);
    }

    public void populateDisplay(Boolean isReward, String currencyCode, Double pricePaidTotal, Double totalPrice, Double saving) {
        ((TextView) mCurrencySymbol.getChildAt(0)).setText(currencyCode);
        ((TextView) mPaymentTotal.getChildAt(0)).setText(String.format("%.2f", pricePaidTotal));

        if (isReward) {
            ((TextView) mSaving.getChildAt(0)).setText(String.format("%s%.2f", currencyCode, saving));
            ((TextView) mBeforeDiscount.getChildAt(0)).setText(String.format("%s%.2f", currencyCode, totalPrice));
        } else {
            ((TextView) mBeforeDiscount.getChildAt(0)).setText(String.format("%s%.2f", currencyCode, totalPrice));
        }
    }
}
