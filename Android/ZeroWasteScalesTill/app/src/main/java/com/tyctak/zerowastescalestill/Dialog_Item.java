package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.tyctak.zerowslib.ViewHolder_TillContainer;

public class Dialog_Item extends AppCompatActivity {

    private final Activity activity = this;
    _Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_item);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout((int)(dm.widthPixels * 0.95), (int)(dm.heightPixels * 0.90));

        product = getIntent().getParcelableExtra("product");
        mGrid = ViewHolder_TillContainer.enmGrid.values()[getIntent().getIntExtra("grid", 0)];
    }

    @Override
    protected void onResume() {
        super.onResume();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                XP_Library_WS XPLIBWS = new XP_Library_WS();
                _ClientStatus clientStatus = XPLIBWS.getClientStatus(XP_Library.getBaseUrl());
                final _ProductDetail productDetail = XPLIBWS.getProductDetail(XP_Library.getBaseUrl(), XP_Library.getWebServicePassword(), product);

                mStockItem = getString(R.string.StockItem);
                mWeighedItem = getString(R.string.WeighedItem);

                mDateFormat = clientStatus.DateFormat;
                mCurrencyCode = clientStatus.CurrencyCode;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (productDetail != null) {
                            displayProduct(productDetail);
                        }
                    }
                });
            }
        });

        thread.start();
    }

    private String mDateFormat;
    private String mStockItem;
    private String mWeighedItem;
    private String mCurrencyCode;
    private ViewHolder_TillContainer.enmGrid mGrid;

    private void displayProduct(_ProductDetail product) {
        TextView reference = (TextView) findViewById(R.id.txtReference);
        TextView productName = (TextView) findViewById(R.id.txtProductName);
        TextView category = (TextView) findViewById(R.id.txtCategory);
        TextView message = (TextView) findViewById(R.id.txtMessage);
        TextView totalPrice = (TextView) findViewById(R.id.txtTotalPrice);
        TextView dealCode = (TextView) findViewById(R.id.txtDealCode);
        TextView which = (TextView) findViewById(R.id.txtWhich);
        TextView txtProductCode = (TextView) findViewById(R.id.txtProductCode);
        TextView txtCountry = (TextView) findViewById(R.id.txtCountry);
        TextView txtDescription = (TextView) findViewById(R.id.txtDescription);
        TextView txtIsOrganic = (TextView) findViewById(R.id.txtIsOrganic);
        TextView txtIsVegan = (TextView) findViewById(R.id.txtIsVegan);
        TextView txtAddedSalt = (TextView) findViewById(R.id.txtAddedSalt);
        TextView txtAddedSugar = (TextView) findViewById(R.id.txtAddedSugar);
        TextView txtAllergen = (TextView) findViewById(R.id.txtAllergen);
        TextView txtNutritional = (TextView) findViewById(R.id.txtNutritional);

        if (mGrid == ViewHolder_TillContainer.enmGrid.Search) {
            which.setText(getString(R.string.SearchList));
        } else {
            which.setText(getString(R.string.BasketList));
        }

        if (product.IsWeighed) {
            reference.setText(String.format("#%06d/#%04d %s %s", product.Id, product.ProductId, DateFormat.format(mDateFormat + " hh:mm", product.ActionDate), (product.Unit.toLowerCase().equals("1 item") ? mStockItem : mWeighedItem)));
        } else {
            reference.setText(String.format("#%06d/#%04d %s" , product.Id, product.ProductId, (product.Unit.toLowerCase().equals("1 item") ? mStockItem : mWeighedItem)));
        }

        productName.setText(product.ProductName);
        category.setText(product.Category);

        if (product.Quantity > 0 && mGrid == ViewHolder_TillContainer.enmGrid.Basket) {
            message.setText(String.format("Qty: %s x %s %.2f per item", product.Quantity, mCurrencyCode, product.UnitGross));
            totalPrice.setText(String.format("%s %.2f", mCurrencyCode, product.Price));
        } else {
            if (product.IsWeighed) {
                category.setText(String.format("%s - %s %.2f per %s", product.Category, mCurrencyCode, product.UnitGross, product.Unit));
                message.setText(String.format("Net/Gross: %s/%sgm", product.NetWeight, product.GrossWeight));
                totalPrice.setText(String.format("%s %.2f", mCurrencyCode, product.Price));
            } else {
                message.setText(String.format("%s %.2f per item", mCurrencyCode, product.UnitGross));
                totalPrice.setText(String.format("%s %.2f", mCurrencyCode, product.UnitGross));
            }
        }

        dealCode.setText(XP_Library.getDealCodeName(product.DealCode));

        if (product.IsOrganic != null) {
            txtProductCode.setText(HighlightTitle("Product Code", (TextUtils.isEmpty(product.ProductCode) ? "-" : product.ProductCode)));
            txtCountry.setText(HighlightTitle("Country", (TextUtils.isEmpty(product.Country) ? "-" : product.Country)));
            txtDescription.setText(HighlightTitle("Description", (TextUtils.isEmpty(product.Description) ? "-" : product.Description)));
            txtIsOrganic.setText(HighlightTitle("Is Organic", (product.IsOrganic ? "Yes" : "No")));
            txtIsVegan.setText(HighlightTitle("Is Vegan", (product.IsVegan ? "Yes" : "No")));
            txtAddedSalt.setText(HighlightTitle("Added Salt", (product.AddedSalt ? "Yes" : "No")));
            txtAddedSugar.setText(HighlightTitle("Added Sugar", (product.AddedSugar ? "Yes" : "No")));
            txtAllergen.setText(HighlightTitle("Allergen", (TextUtils.isEmpty(product.Allergen) ? "-" : product.Allergen)));
            txtNutritional.setText(HighlightTitle("Nutritional", (TextUtils.isEmpty(product.Nutritional) ? "-" : product.Nutritional)));
        }
    }

    private SpannableStringBuilder HighlightTitle(String title, String text) {
        SpannableStringBuilder str = new SpannableStringBuilder(title + ": " + text);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }

    public void btnCancel(View view) {
        onBackPressed();
    }
}
