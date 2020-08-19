package com.tyctak.zerowastescalestill;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tyctak.zerowslib.iItemClickListener;
import com.tyctak.zerowslib.ViewHolder_TillContainer;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder_TillContainer> {

    private ArrayList<_Product> mData;
    private LayoutInflater mInflater;
    private iItemClickListener mClickListener;
    private ViewHolder_TillContainer.enmGrid mGrid;
    private String mDateFormat;
    private String mCurrencyCode;
    private Integer mMeasuredWidth = 0;

    MyRecyclerViewAdapter(ViewHolder_TillContainer.enmGrid grid, ArrayList<_Product> products, String dateFormat, String currencyCode) {
        this.mInflater = LayoutInflater.from(MyApp.getAppContext());
        this.mData = products;
        this.mGrid = grid;
        this.mDateFormat = dateFormat;
        this.mCurrencyCode = currencyCode;
    }

    private Integer getItemWidth(Integer width) {
        Integer minWidth = 130;
        Integer maxWidth = 200;

        Integer recyclerWidth = (width - 30);

        Integer remainder = recyclerWidth % minWidth;
        Integer number = Math.max((recyclerWidth / minWidth), 1);
        Integer itemWidth = minWidth;

        if (remainder > 0) {
            itemWidth += ((remainder - (number * 3)) / number);
        }

        return Math.min(itemWidth, maxWidth);
    }

//    public int dpToPx(int dp) {
//        return Math.round(dp * Resources.getSystem().getDisplayMetrics().density);
//    }

    public int pxToDp(int px) {
        return Math.round(px / Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public ViewHolder_TillContainer onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);

        if (mMeasuredWidth == 0) mMeasuredWidth = parent.getMeasuredWidth();
        Log.d("MyRecycler", String.valueOf(mMeasuredWidth));
        Integer dp = pxToDp(mMeasuredWidth);

        Integer width = getItemWidth(((int) dp / 2));

        Log.d("MyRecyclerViewAdapter", String.format("%s-%s", dp, width.toString()));

        FrameLayoutMaxWidth frameLayout = (FrameLayoutMaxWidth) view.findViewById(R.id.recyclerFrameLayout);
        frameLayout.setMaxWidth(width);

        return new ViewHolder_TillContainer(mClickListener, mGrid, view);
    }

    @Override
    public void onBindViewHolder(ViewHolder_TillContainer holder, int position) {
        _Product product = mData.get(position);
        Drawable background;

        if (TextUtils.isEmpty(product.DealCode)) {
            background = MyApp.getAppContext().getResources().getDrawable(R.drawable.custom_item_default);
        } else {
            background = MyApp.getAppContext().getResources().getDrawable(R.drawable.custom_item_dealcode);
        }

        String itemName = (product.Unit.toLowerCase().equals("1 item") ? MyApp.getAppContext().getResources().getString(R.string.StockItem) : MyApp.getAppContext().getResources().getString(R.string.WeighedItem));

        holder.populateDisplay(product.getProductModel(), mCurrencyCode, mDateFormat, itemName, MyApp.getAppContext().getResources().getString(R.string.perName), MyApp.getAppContext().getResources().getString(R.string.qtyName), MyApp.getAppContext().getResources().getString(R.string.netGrossName), background);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder_TillContainer holder) {
        super.onViewRecycled(holder);
    }

    public int findProduct(_Product product) {
        Integer retval = -1;

        Integer size = mData.size();
        for (Integer i = 0; i < size; i++) {
            if ((mData.get(i)).equals(product)) {
                retval = i;
                break;
            }
        }

        return retval;
    }

    public void addProduct(_Product product) {
        mData.add(product);
    }

    public void removeProduct(Integer index) {
        mData.remove((int)index);
    }

    public void removeProduct(_Product product) {
        mData.remove(product);
    }

    _Product getItem(int id) {
        return mData.get(id);
    }

    void setClickListener(iItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}