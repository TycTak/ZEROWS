package com.tyctak.zerowslib;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder_TillContainer extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public enum enmGrid {
        Search,
        Basket
    }

    private TextView mReference;
    private TextView mProductType;
    private TextView mProductName;
    private TextView mCategory;
    private TextView mMessage;
    private TextView mTotalPrice;
    private LinearLayout mllPurchase;
    private enmGrid mGrid;

    private iItemClickListener mClickListener;

    public ViewHolder_TillContainer(iItemClickListener clickListener, enmGrid grid, View itemView) {
        super(itemView);

        initialiseDisplay(itemView);

        mGrid = grid;
        mClickListener = clickListener;

        mReference = (TextView) itemView.findViewWithTag("txtReference");
        mProductType = (TextView) itemView.findViewWithTag("txtProductType");
        mProductName = (TextView) itemView.findViewWithTag("txtProductName");
        mCategory = (TextView) itemView.findViewWithTag("txtCategory");
        mMessage = (TextView) itemView.findViewWithTag("txtMessage");
        mTotalPrice = (TextView) itemView.findViewWithTag("txtTotalPrice");
        mllPurchase = (LinearLayout) itemView.findViewWithTag("llPurchase");

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public int dpToPx(int dp) {
        return Math.round(dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void initialiseDisplay(View view) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // ########## txtReference
        TextView txtReference = new TextView(view.getContext());
        txtReference.setLayoutParams(layoutParams);
        txtReference.setTextSize(10);
        txtReference.setSingleLine(true);
        txtReference.setClickable(false);
        txtReference.setTag("txtReference");

        // ########## txtProductType
        TextView txtProductType = new TextView(view.getContext());
        LinearLayout.LayoutParams txtProductTypeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtProductTypeParams.setMargins(dpToPx(2), 0,0,0);
        txtProductType.setLayoutParams(txtProductTypeParams);
        txtProductType.setTextSize(10);
        txtProductType.setSingleLine(true);
        txtProductType.setClickable(false);
        txtProductType.setTag("txtProductType");

        // ########## llReference
        LinearLayout llReference = new LinearLayout(view.getContext());
        llReference.setOrientation(LinearLayout.HORIZONTAL);
        llReference.addView(txtReference);
        llReference.addView(txtProductType);

        // ########## txtProductName
        TextView txtProductName = new TextView(view.getContext());
        LinearLayout.LayoutParams txtProductNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtProductNameParams.setMargins(0, dpToPx(-7),0,0);
        txtProductName.setLayoutParams(txtProductNameParams);
        txtProductName.setTextSize(22);
        txtProductName.setSingleLine(true);
        txtProductName.setClickable(false);
        txtProductName.setTag("txtProductName");

        // ########## txtCategory
        TextView txtCategory = new TextView(view.getContext());
        LinearLayout.LayoutParams txtCategoryParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtCategoryParams.setMargins(0, dpToPx(-5),0,0);
        txtCategory.setLayoutParams(txtCategoryParams);
        txtCategory.setTextSize(13);
        txtCategory.setSingleLine(true);
        txtCategory.setClickable(false);
        txtCategory.setTag("txtCategory");

        // ########## txtMessage
        TextView txtMessage = new TextView(view.getContext());
        LinearLayout.LayoutParams txtMessageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtMessageParams.setMargins(0, dpToPx(-1),0,0);
        txtMessage.setLayoutParams(txtMessageParams);
        txtMessage.setTextSize(11);
        txtMessage.setSingleLine(true);
        txtMessage.setClickable(false);
        txtMessage.setTag("txtMessage");

        // ########## txtTotalPrice
        TextView txtTotalPrice = new TextView(view.getContext());
        RelativeLayout.LayoutParams txtTotalPriceParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        txtTotalPriceParams.setMargins(0, dpToPx(-4),dpToPx(3),0);
        txtTotalPriceParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        txtTotalPriceParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        txtTotalPrice.setLayoutParams(txtTotalPriceParams);
        txtTotalPrice.setTextSize(30);
        txtTotalPrice.setSingleLine(true);
        txtTotalPrice.setClickable(false);
        txtTotalPrice.setTag("txtTotalPrice");

        RelativeLayout rlTotalPrice = new RelativeLayout(view.getContext());
        RelativeLayout.LayoutParams rlTotalPriceParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlTotalPrice.setLayoutParams(rlTotalPriceParams);
        rlTotalPrice.addView(txtTotalPrice);

        // ########## llBody
        LinearLayout llBody = new LinearLayout(view.getContext());
        RelativeLayout.LayoutParams llBodyParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        llBody.setLayoutParams(llBodyParams);
        llBody.setOrientation(LinearLayout.VERTICAL);
        llBody.addView(llReference);
        llBody.addView(txtProductName);
        llBody.addView(txtCategory);
        llBody.addView(txtMessage);
        llBody.addView(rlTotalPrice);

        LinearLayout llPurchase = view.findViewWithTag("llPurchase");
        llPurchase.addView(llBody);
    }

    @Override
    public void onClick(View view) {
        if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View view) {
        if (mClickListener != null) mClickListener.onItemLongClick(view, getAdapterPosition());
        return true;
    }

    public void populateDisplay(_ProductModel product, String currencyCode, String dateFormat, String itemName, String perName, String qtyName, String netGrossName, Drawable background) {
        if (product.IsWeighed) {
            mReference.setText(String.format("#%06d %s ", product.Id, DateFormat.format(dateFormat + " hh:mm", product.ActionDate)));
        } else {
            mReference.setText(String.format("#%06d " , product.Id));
        }

        mProductType.setText(itemName);

        mProductName.setText(product.ProductName);
        mCategory.setText(product.Category);

        if (product.Quantity > 0 && mGrid == enmGrid.Basket) {
            mMessage.setText(String.format("%s: %s x %s %.2f %s %s", qtyName, product.Quantity, currencyCode, product.UnitGross, perName, product.Unit));
            mTotalPrice.setText(String.format("%s %.2f", currencyCode, product.Price));
        } else {
            if (product.IsWeighed) {
                mCategory.setText(String.format("%s %.2f %s %s", currencyCode, product.UnitGross, perName, product.Unit));
                mMessage.setText(String.format("%s: %s/%sgm", netGrossName, product.NetWeight, product.GrossWeight));
                mTotalPrice.setText(String.format("%s %.2f", currencyCode, product.Price));
            } else {
                mMessage.setText(String.format("%s %.2f %s %s", currencyCode, product.UnitGross, perName, product.Unit));
                mTotalPrice.setText(String.format("%s %.2f", currencyCode, product.UnitGross));
            }
        }

        this.mllPurchase.setBackground(background);
    }
}
