package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.squareup.sdk.pos.ChargeRequest;
import com.squareup.sdk.pos.CurrencyCode;
import com.squareup.sdk.pos.PosClient;
import com.squareup.sdk.pos.PosSdk;
import com.sumup.merchant.Models.TransactionInfo;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpLogin;
import com.sumup.merchant.api.SumUpPayment;
import com.tyctak.zerowslib.ViewHolder_TotalPrice;
import com.tyctak.zerowslib.iItemClickListener;
import com.tyctak.zerowslib.ViewHolder_TillContainer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Activity_Customer extends AppCompatActivity implements iItemClickListener {

    Activity_Customer activity = this;

    public enum enmBasket {
        Add,
        Remove
    }

    enum enmSearchType {
        Weighed,
        Stock
    }

    private static final int SQUARE_CHARGE_REQUEST_CODE = 1;
    private static final int SUMUP_CHARGE_REQUEST_CODE = 2;
    private static final int SUMUP_LOGIN_CODE = 3;

    private PosClient squareClient;
    private SumUpLogin sumupClient;

    private Double TotalPrice = 0.0d;
    private Double PricePaidTotal = 0.0d;
    private Double SavingTotal = 0.0d;
    private _Reward reward;
    private Integer purchaseId = 0;
    private String mDateFormat;
    private String mCurrencyCode;
    private Activity_DisplayLog.enmLogArea logArea = Activity_DisplayLog.enmLogArea.None;

    private Activity_Payment.enmProvider selectedPayment;
    private enmSearchType searchType = enmSearchType.Weighed;

    RecyclerView searchRecycler;
    RecyclerView basketRecycler;
    ArrayList<_Product> productList;

    private final ScheduledThreadPoolExecutor executor_ = new ScheduledThreadPoolExecutor(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Activity_Customer", "onCreate");

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_customer);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        logArea = Activity_DisplayLog.enmLogArea.values()[Integer.parseInt(XP_Library.getValue("displaylog", String.valueOf(Activity_DisplayLog.enmLogArea.None.ordinal())))];
        reward = getIntent().getParcelableExtra("reward");

        if (reward == null) {
            ImageView rewardImage = (ImageView) findViewById(R.id.imageReward);
            rewardImage.setVisibility(View.INVISIBLE);
        }

        XP_Library.logValue(logArea,"Activity_Customer.onCreate");

        TextView txtProductSearch = (TextView) findViewById(R.id.txtProductSearch);
        txtProductSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().toLowerCase();
                filterSearch(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        if (savedInstanceState == null) {
            Refresh(false);
        }

        FragmentManager manager = this.getSupportFragmentManager();
        Fragment_Header header = (Fragment_Header) manager.findFragmentById(R.id.customer_header);
        header.displayRunProcess(true);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) { view = new View(activity); }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("Activity_Customer", "onStart");
        XP_Library.logValue(logArea, "Activity_Customer.onStart");

        selectedPayment = Activity_Payment.enmProvider.values()[Integer.parseInt(XP_Library.getValue(Activity_Payment.PAYMENT_TYPE, String.valueOf(Activity_Payment.enmProvider.Square.ordinal())))];

        FlexboxLayoutManager searchLayoutManager = new FlexboxLayoutManager(activity);
        searchLayoutManager.setFlexDirection(FlexDirection.ROW);
        searchLayoutManager.setJustifyContent(JustifyContent.FLEX_START);

        searchRecycler = findViewById(R.id.search_flow);
        searchRecycler.setLayoutManager(new LinearLayoutManager(activity));
        searchRecycler.setLayoutManager(searchLayoutManager);

        FlexboxLayoutManager basketLayoutManager = new FlexboxLayoutManager(activity);
        basketLayoutManager.setFlexDirection(FlexDirection.ROW);
        basketLayoutManager.setJustifyContent(JustifyContent.FLEX_START);

        basketRecycler = findViewById(R.id.basket_flow);
        basketRecycler.setLayoutManager(new LinearLayoutManager(activity));
        basketRecycler.setLayoutManager(basketLayoutManager);

        if (selectedPayment == Activity_Payment.enmProvider.SumUp) {
            sumupClient = SumUpLogin.builder(XP_Library.getValue(Activity_Payment.SUMUP_AFFILIATE_KEY, "")).build();
        } else if (selectedPayment == Activity_Payment.enmProvider.Square) {
            squareClient = PosSdk.createClient(activity, XP_Library.getValue( Activity_Payment.SQUARE_APPLICATION_ID, ""));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("Activity_Customer", "onRestart");
        XP_Library.logValue(logArea,"Activity_Customer.onRestart");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d("Activity_Customer", "onSaveInstanceState");
        XP_Library.logValue(logArea,"Activity_Customer.onSaveInstanceState");

        outState.putParcelableArrayList("productList", productList);
        outState.putInt("searchType", searchType.ordinal());
        outState.putParcelable("reward", reward);
        outState.putDouble("totalPrice", TotalPrice);
        outState.putDouble("pricePaidTotal", PricePaidTotal);
        outState.putDouble("savingTotal", SavingTotal);
        outState.putInt("purchaseid", purchaseId);
        outState.putInt("displaylog", logArea.ordinal());
        outState.putString("dateformat", mDateFormat);
        outState.putString("currencycode", mCurrencyCode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d("Activity_Customer", "onRestoreInstanceState");
        XP_Library.logValue(logArea,"Activity_Customer.onRestoreInstanceState");

        productList = savedInstanceState.getParcelableArrayList("productList");
        searchType = enmSearchType.values()[savedInstanceState.getInt("searchType")];
        reward = savedInstanceState.getParcelable("reward");
        TotalPrice = savedInstanceState.getDouble("totalPrice");
        PricePaidTotal = savedInstanceState.getDouble("pricePaidTotal");
        SavingTotal = savedInstanceState.getDouble("savingTotal");
        purchaseId = savedInstanceState.getInt("purchaseid");
        logArea = Activity_DisplayLog.enmLogArea.values()[savedInstanceState.getInt("displaylog")];
        mDateFormat = savedInstanceState.getString("dateformat");
        mCurrencyCode = savedInstanceState.getString("currencycode");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("Activity_Customer", "onStop");
        XP_Library.logValue(logArea,"Activity_Customer.onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("Activity_Customer", "onDestroy");
        XP_Library.logValue(logArea,"Activity_Customer.onDestroy");

        if (SumUpAPI.isLoggedIn()) SumUpAPI.logout();

        squareClient = null;
        sumupClient = null;

        searchRecycler.setAdapter(null);
        basketRecycler.setAdapter(null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("Activity_Customer", "onPause");
        XP_Library.logValue(logArea,"Activity_Customer.onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("Activity_Customer", "onResume");
        XP_Library.logValue(logArea,"Activity_Customer.onResume");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                XP_Library_WS XPLIBWS = new XP_Library_WS();
                _ClientStatus clientStatus = XPLIBWS.getClientStatus(XP_Library.getBaseUrl());

                mDateFormat = clientStatus.DateFormat;
                mCurrencyCode = clientStatus.CurrencyCode;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDisplay();

                        ((MyRecyclerViewAdapter) searchRecycler.getAdapter()).setClickListener(activity);
                        ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).setClickListener(activity);
                    }
                });
            }
        });

        thread.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Refresh(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Log.d("Activity_Customer", "onWindowFocusChanged " + findViewById(R.id.search_flow).getWidth());

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
    }

    private void updateDisplay() {
        calculateReward();

        LinearLayout rewardDiscount = (LinearLayout) findViewById(R.id.rewardDiscount);

        LinearLayout llTotalPrice = (LinearLayout) findViewById(R.id.llTotalPrice);

        LinearLayout llCurrencySymbol = (LinearLayout) llTotalPrice.findViewWithTag("llCurrencySymbol");
        LinearLayout llPaymentTotal = (LinearLayout) llTotalPrice.findViewWithTag("llPaymentTotal");
        LinearLayout llSaving = (LinearLayout) llTotalPrice.findViewWithTag("llSaving");
        LinearLayout llBeforeDiscount = (LinearLayout) llTotalPrice.findViewWithTag("llBeforeDiscount");

        ImageView rewardImage = (ImageView) findViewById(R.id.imageReward);
        Boolean isReward = (reward != null && reward.IsReward);

        ViewHolder_TotalPrice viewHolder = new ViewHolder_TotalPrice(llTotalPrice);
        viewHolder.populateDisplay(isReward, mCurrencyCode, PricePaidTotal, TotalPrice, SavingTotal);

//        ((TextView) llCurrencySymbol.getChildAt(0)).setText(mCurrencyCode);
//        ((TextView) llPaymentTotal.getChildAt(0)).setText(String.format("%.2f", PricePaidTotal));

        if (isReward) {
//            ((TextView) llSaving.getChildAt(0)).setText(String.format("%s%.2f", mCurrencyCode, SavingTotal));
//            ((TextView) llBeforeDiscount.getChildAt(0)).setText(String.format("%s%.2f", mCurrencyCode, TotalPrice));
            rewardImage.setImageResource(R.drawable.ic_reward);
            rewardDiscount.setVisibility(View.VISIBLE);
        } else {
//            ((TextView) llBeforeDiscount.getChildAt(0)).setText(String.format("%s%.2f", mCurrencyCode, TotalPrice));
            rewardImage.setImageResource(R.drawable.ic_reward_disabled);
            rewardDiscount.setVisibility(View.INVISIBLE);
        }

        Button btnSearchType = (Button) findViewById(R.id.btnSearchType);
        btnSearchType.setText(searchType == enmSearchType.Weighed ? getString(R.string.WeighedItem) : getString(R.string.StockItem));

        Button btnPay = (Button) findViewById(R.id.btnPay);
        btnPay.setEnabled(PricePaidTotal > 0 ? true : false);

        if (searchRecycler.getAdapter() == null) {
            ArrayList<_Product> searchList = new ArrayList<>();
            searchRecycler.setAdapter(new MyRecyclerViewAdapter(ViewHolder_TillContainer.enmGrid.Search, searchList, mDateFormat, mCurrencyCode));
            ((MyRecyclerViewAdapter) searchRecycler.getAdapter()).setClickListener(activity);
        }

        if (basketRecycler.getAdapter() == null) {
            ArrayList<_Product> basketList = new ArrayList<>();
            basketRecycler.setAdapter(new MyRecyclerViewAdapter(ViewHolder_TillContainer.enmGrid.Basket, basketList, mDateFormat, mCurrencyCode));
            ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).setClickListener(activity);
        }

        if (productList != null) {
            Integer size = productList.size();

            for (Integer i = 0; i < size; i++) {
                _Product product = productList.get(i);

                if (product.IsBasket) {
                    if (((MyRecyclerViewAdapter) basketRecycler.getAdapter()).findProduct(product) < 0) {
                        ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).addProduct(product);
                    }
                } else {
                    if (searchType == enmSearchType.Weighed && product.IsWeighed) {
                        purchaseId = (product.Id > purchaseId ? product.Id : purchaseId);
                        addSearchProduct(product, true);
                    }
                }
            }
        }
    }

    private void addSearchProduct(_Product product, Boolean checkBasket) {
        if (((MyRecyclerViewAdapter) searchRecycler.getAdapter()).findProduct(product) < 0 && (!checkBasket || ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).findProduct(product) < 0)) {
            ((MyRecyclerViewAdapter) searchRecycler.getAdapter()).addProduct(product);
            searchRecycler.getAdapter().notifyDataSetChanged();
        }
    }

    private void addStockProduct(_Product product, Boolean checkBasket) {
        if (((MyRecyclerViewAdapter) searchRecycler.getAdapter()).findProduct(product) < 0 && (!checkBasket || ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).findProduct(product) < 0)) {
            ((MyRecyclerViewAdapter) searchRecycler.getAdapter()).addProduct(product);
            searchRecycler.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        hideKeyboard();

        Boolean isSearchClick = view.getParent().toString().contains("search");
        _Product product;

        if (position >= 0) {
            if (isSearchClick) {
                product = ((MyRecyclerViewAdapter) searchRecycler.getAdapter()).getItem(position);
                activity.ProcessAction(Activity_Customer.enmBasket.Add, product);
            } else {
                product = ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItem(position);
                activity.ProcessAction(Activity_Customer.enmBasket.Remove, product);
            }

            basketRecycler.getAdapter().notifyDataSetChanged();
            if (product.IsWeighed) searchRecycler.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Boolean isSearchClick = view.getParent().toString().contains("search");
        _Product product;

        if (position >= 0) {
            Intent intent = new Intent(activity, Dialog_Item.class);

            if (isSearchClick) {
                product = ((MyRecyclerViewAdapter) searchRecycler.getAdapter()).getItem(position);
                intent.putExtra("grid", ViewHolder_TillContainer.enmGrid.Search.ordinal());
            } else {
                product = ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItem(position);
                intent.putExtra("grid", ViewHolder_TillContainer.enmGrid.Basket.ordinal());
            }

            intent.putExtra("product", product);

            startActivity(intent);
        }
    }

    private void filterSearch(String value) {
        ArrayList<_Product> tempList = new ArrayList<>();

        if (productList != null) {
            Integer size = productList.size();

            for (Integer i = 0; i < size; i++) {
                _Product product = productList.get(i);

                if (product.IsWeighed && searchType == enmSearchType.Weighed && product.CombinedSearchString.contains(value) && !findProductInBasket(product)) {
                    tempList.add(product);
                } else if (!product.IsWeighed && searchType == enmSearchType.Stock && product.CombinedSearchString.contains(value)) {
                    tempList.add(product);
                }
            }
        }

        searchRecycler.setAdapter(new MyRecyclerViewAdapter(ViewHolder_TillContainer.enmGrid.Search, tempList, mDateFormat, mCurrencyCode));
        ((MyRecyclerViewAdapter) searchRecycler.getAdapter()).setClickListener(activity);
    }

    private boolean findProductInBasket(_Product product) {
        return (basketRecycler.getAdapter() != null && ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).findProduct(product) >= 0);
    }

    public void ProcessAction(enmBasket action, _Product product) {
        if (action == enmBasket.Add) {
            if (product.IsWeighed) {
                product.IsBasket = true;
                ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).addProduct(product);
                ((MyRecyclerViewAdapter) searchRecycler.getAdapter()).removeProduct(product);
                TotalPrice += product.Price;
            } else {
                Boolean isInBasket = findProductInBasket(product);

                if (!isInBasket) {
                    product.Quantity = 1;
                    product.IsBasket = true;
                    ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).addProduct(product);

                    TotalPrice += product.UnitGross;
                } else {
                    Integer index = ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).findProduct(product);
                    Integer qty = ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItem(index).Quantity;

                    if (qty < 99) {
                        ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItem(index).Quantity += 1;
                        ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItem(index).Price = (((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItem(index).Quantity * ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItem(index).UnitGross);
                        TotalPrice += product.UnitGross;
                    }
                }
            }
        } else {
            Integer index = ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).findProduct(product);

            if (product.Quantity <= 1) {
                ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItem(index).Quantity = 0;
                ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).removeProduct(index);
                product.IsBasket = false;

                if (product.IsWeighed && searchType == enmSearchType.Weighed) {
                    ((MyRecyclerViewAdapter) searchRecycler.getAdapter()).addProduct(product);
                }
            } else {
                ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItem(index).Quantity -= 1;
                ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItem(index).Price = (((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItem(index).Quantity * ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItem(index).UnitGross);
            }

            if (product.IsWeighed) {
                TotalPrice -= product.Price;
            } else {
                TotalPrice -= product.UnitGross;
            }
        }

        DecimalFormat twoDForm = new DecimalFormat("#.##");
        TotalPrice = Double.valueOf(twoDForm.format(TotalPrice));

        updateDisplay();
    }

    private void calculateReward() {
        if (reward != null && reward.IsReward) {
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            SavingTotal = Double.valueOf(twoDForm.format(TotalPrice / 100 * reward.RewardValue));
            PricePaidTotal = Double.valueOf(twoDForm.format(TotalPrice - SavingTotal));
        } else {
            SavingTotal = 0.0d;
            PricePaidTotal = TotalPrice;
        }
    }

    private void Refresh(final boolean refreshAll) {
        XP_Library.logValue(logArea,"Activity_Customer.Refresh");

        if (refreshAll) {
            purchaseId = 0;
            basketRecycler.setAdapter(null);
            searchRecycler.setAdapter(null);
        }

        final ImageView iv = (ImageView) findViewById(R.id.imageRefresh);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.imageRefreshProgress);
        iv.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                XP_Library_WS XPLIBWS = new XP_Library_WS();
                productList = XPLIBWS.getProductList(XP_Library.getBaseUrl(), XP_Library.getWebServicePassword(), -1);

                TotalPrice = 0.0d;
                SavingTotal = 0.0d;
                PricePaidTotal = 0.0d;

                if (refreshAll) {
                    searchType = enmSearchType.Weighed;
                    reward = null;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView txtProductSearch = (TextView) findViewById(R.id.txtProductSearch);
                        txtProductSearch.setText("");

                        updateDisplay();

                        iv.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);
                    }
                });
            }
        });

        thread.start();
    }

    public void btnSearchType(View view) {
        searchType = (searchType == enmSearchType.Weighed ? enmSearchType.Stock : enmSearchType.Weighed);

        Button btnItemType = (Button) findViewById(R.id.btnSearchType);
        btnItemType.setText((searchType == enmSearchType.Weighed ? "WEIGHED" : "STOCK"));

        TextView txtProductSearch = (TextView) findViewById(R.id.txtProductSearch);
        txtProductSearch.setText("");

        filterSearch("");
    }

    public void btnCancel(View view) {
        onBackPressed();
    }

    public void btnReward(View view) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                XP_Library_WS XPLIBWS = new XP_Library_WS();
                reward = XPLIBWS.getReward(XP_Library.getBaseUrl(), XP_Library.getWebServicePassword());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDisplay();
                    }
                });
            }
        });

        thread.start();
    }

    private void writeReceipt(final String paymentReference, final String paymentType) {
        XP_Library.logValue(logArea,"Activity_Customer.writeReceipt");

        final ArrayList<_Product> weighedItems = new ArrayList<>();
        final ArrayList<_Product> stockItems = new ArrayList<>();

        Integer size = ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItemCount();
        for (Integer i = 0; i < size; i++) {
            _Product product = ((MyRecyclerViewAdapter) basketRecycler.getAdapter()).getItem(i);
            if (product.IsWeighed) {
                weighedItems.add(product);
            } else {
                stockItems.add(product);
            }
        }

        final Double totalPrice = TotalPrice;
        final Double pricePaid = PricePaidTotal;
        final Double saving = SavingTotal;
        final Integer userId = 0;
        final String provider = selectedPayment.name();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                XP_Library_WS XPLIBWS = new XP_Library_WS();
                if (XPLIBWS.writeReceipt(XP_Library.getBaseUrl(), XP_Library.getWebServicePassword(), totalPrice, pricePaid, saving, userId, provider, paymentReference, weighedItems, stockItems, paymentType)) {
                    XP_Library.logValue(logArea,"Activity_Customer.writeReceipt OK");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Refresh(true);
                        }
                    });
                }
            }
        });

        thread.start();
    }

    public void btnRefresh(View view) {
        Refresh(true);
    }

    public void btnPay(View view) {
        XP_Library.logValue(logArea,"Activity_Customer.btnPay");

        if (selectedPayment == Activity_Payment.enmProvider.Square) {
            startSquarePaymentTransaction(PricePaidTotal, CurrencyCode.GBP);
        } else if (selectedPayment == Activity_Payment.enmProvider.SumUp) {
            SumUpAPI.openLoginActivity(activity, sumupClient, SUMUP_LOGIN_CODE);
//            startSumupPaymentTransaction(PricePaidTotal, SumUpPayment.Currency.GBP);
        } else if (selectedPayment == Activity_Payment.enmProvider.None) {
            writeReceipt("", "None");
        } else {
            if (squareClient == null) squareClient = PosSdk.createClient(activity, "");

            Intent intent = new Intent(activity, Test_Activity_SquareUp.class);
            startActivityForResult(intent, SQUARE_CHARGE_REQUEST_CODE);
        }
    }

    public void startSumupPaymentTransaction(Double payment, SumUpPayment.Currency currency) {
        SumUpPayment paymentAmount = SumUpPayment.builder().total(new BigDecimal(payment)).currency(currency).skipSuccessScreen().build();

        try {
            SumUpAPI.checkout(activity, paymentAmount, SUMUP_CHARGE_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            AlertDialogHelper.showDialog(this,getString(R.string.Error), getString(R.string.NotInstalledSumUp));
        }
    }

    private void startSquarePaymentTransaction(Double payment, CurrencyCode currency) {
        XP_Library.logValue(logArea,"Activity_Customer.startSquarePaymentTransaction");

        Integer paymentAmount = (int)Math.round(payment * 100);

        try {
            ChargeRequest request = new ChargeRequest.Builder(paymentAmount, currency).build();
            Intent intent = squareClient.createChargeIntent(request);
            startActivityForResult(intent, SQUARE_CHARGE_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            XP_Library.logValue(logArea,"Activity_Customer.startSquarePaymentTransaction Error");
            AlertDialogHelper.showDialog(activity, getString(R.string.Error), getString(R.string.NotInstalledSquare));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Activity_Customer", "onActivityResult");
        XP_Library.logValue(logArea,"Activity_Customer.onActivityResult #1");

        if (requestCode == SUMUP_LOGIN_CODE) {
            startSumupPaymentTransaction(PricePaidTotal, SumUpPayment.Currency.GBP);
            return;
        }

        if (data == null && requestCode == SQUARE_CHARGE_REQUEST_CODE) {
            XP_Library.logValue(logArea,"Activity_Customer.onActivityResult #2");
            AlertDialogHelper.showDialog(activity, getString(R.string.Error),"SQUARE Point of Sale was uninstalled or stopped working");
            return;
        }

        if (data == null && requestCode == SUMUP_CHARGE_REQUEST_CODE) {
            XP_Library.logValue(logArea,"Activity_Customer.onActivityResult #3");
            AlertDialogHelper.showDialog(activity, getString(R.string.Error),"SUMUP Point of Sale was uninstalled or stopped working");
            return;
        }

        if (resultCode != Activity.RESULT_OK && requestCode == SQUARE_CHARGE_REQUEST_CODE) {
            if (squareClient != null) {
                XP_Library.logValue(logArea,"Activity_Customer.onActivityResult #4a");
                Log.d("Activity_Customer", "onActivityResult ERROR");
                ChargeRequest.Error error = squareClient.parseChargeError(data);
                AlertDialogHelper.showDialog(activity, getString(R.string.Failed), "SQUARE Payment did not go through (" + error.code + " - " + error.debugDescription + ")");
            } else {
                XP_Library.logValue(logArea,"Activity_Customer.onActivityResult #4b");
                Log.d("Activity_Customer", "onActivityResult NULL");
                AlertDialogHelper.showDialog(activity, getString(R.string.Failed), "SQUARE Payment did not go through (SQUARECLIENT is null)");
            }
            return;
        }

        if (resultCode != SumUpAPI.Response.ResultCode.SUCCESSFUL && requestCode == SUMUP_CHARGE_REQUEST_CODE) {
            XP_Library.logValue(logArea,"Activity_Customer.onActivityResult #5");
            AlertDialogHelper.showDialog(activity, getString(R.string.Failed),"SUMUP Payment did not go through (" + resultCode + ")");
            return;
        }

        if (requestCode == SQUARE_CHARGE_REQUEST_CODE) {
            XP_Library.logValue(logArea,"Activity_Customer.onActivityResult #6");
            ChargeRequest.Success success = squareClient.parseChargeSuccess(data);

            if (success != null) {
                XP_Library.logValue(logArea,"Activity_Customer.onActivityResult #7");

                String paymentReference = success.clientTransactionId;
                String paymentType = (success.serverTransactionId == null ? "Cash" : "Card");

                writeReceipt(paymentReference, paymentType);

                XP_Library.logValue(logArea,"Activity_Customer.onActivityResult #8");
            } else {
                XP_Library.logValue(logArea,"Activity_Customer.onActivityResult #9");
                AlertDialogHelper.showDialog(activity, getString(R.string.Failed),"Payment did not go through");
            }
        } else if (requestCode == SUMUP_CHARGE_REQUEST_CODE) {
            XP_Library.logValue(logArea,"Activity_Customer.onActivityResult #10");
            String paymentReference = data.getExtras().getString(SumUpAPI.Response.TX_CODE);
            TransactionInfo info = data.getExtras().getParcelable(SumUpAPI.Response.TX_INFO);

            XP_Library.logValue(logArea,"Activity_Customer.onActivityResult #11");
            writeReceipt(paymentReference, info.getPaymentType());
        }
    }

    public void btnRunProcess(View view) {
        Intent intent = new Intent(activity, Activity_Process.class);
        intent.putExtra("source", Activity_MessageReturn.enmSource.Customer.ordinal());
        startActivity(intent);
    }
}
