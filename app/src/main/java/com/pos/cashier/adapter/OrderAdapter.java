package com.pos.cashier.adapter;

import java.util.ArrayList;
import com.pos.cashier.R;
import com.pos.cashier.model.Stock;
import com.pos.cashier.referensi.Constant;
import com.pos.cashier.referensi.FontCache;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class OrderAdapter extends BaseAdapter {
	private ArrayList<Stock> listStock = null;
	private static LayoutInflater inflater = null;
	private Typeface fontDroidSans, fontDroidSansBold;
	private OrderAdapterListener listener;
	private boolean blnInsert = false;
	public Double totalPrice = 0.0D;
	private Activity mActivity;

	public OrderAdapter(Activity activity, OrderAdapterListener mListener, boolean blnInsert) {
		inflater 		  = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fontDroidSans     = FontCache.get(activity, "DroidSans");
		fontDroidSansBold = FontCache.get(activity, "DroidSans-Bold");
		listener		  = mListener;
		this.blnInsert    = blnInsert;
		mActivity		  = activity;
	}

	public void updateListStock(ArrayList<Stock> newListStock) {
		this.listStock = newListStock;
		totalPrice	   = 0.0D;

		for (int i=0; i<newListStock.size(); i++) {
			Double count = 1.0D;
			if (!blnInsert) {
				count = Double.parseDouble(newListStock.get(i).getQty());
			}
			String[] splitPrice = newListStock.get(i).getItemPrice().split("\\.");
			Double price  = Double.parseDouble(splitPrice[0].toString());
			Double amount = price * count;
			totalPrice    = totalPrice + amount;
		}
	}

	@Override
	public int getCount() {
		return this.listStock.size();
	}

	@Override
	public Stock getItem(int position) {
		return this.listStock.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final ViewHolder vh;
		final Stock stock = this.listStock.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.cell_order, parent, false);
			vh = new ViewHolder();

			vh.cellOrder   = (RelativeLayout) convertView.findViewById(R.id.cellOrder);
			vh.lblItemId   = (TextView) convertView.findViewById(R.id.lblItemId);
			vh.lblItemName = (TextView) convertView.findViewById(R.id.lblItemName);
			vh.lblPrice    = (TextView) convertView.findViewById(R.id.lblPrice);
			vh.lblCount	   = (TextView) convertView.findViewById(R.id.lblCount);
			vh.imgDelete   = (ImageView) convertView.findViewById(R.id.imgDelete);
			vh.linCount	   = (LinearLayout) convertView.findViewById(R.id.linCount);
			vh.imgPlus	   = (ImageView) convertView.findViewById(R.id.imgPlus);
			vh.imgMinus	   = (ImageView) convertView.findViewById(R.id.imgMinus);

			vh.lblCount.setTypeface(fontDroidSans);
			vh.lblItemId.setTypeface(fontDroidSans, Typeface.BOLD);
			vh.lblItemName.setTypeface(fontDroidSans);
			vh.lblPrice.setTypeface(fontDroidSans, Typeface.BOLD);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		Double count = 1.0D;
		if (blnInsert) {
			vh.linCount.setVisibility(View.GONE);
			vh.imgDelete.setVisibility(View.GONE);
		} else {
			vh.imgDelete.setVisibility(View.VISIBLE);
			vh.linCount.setVisibility(View.VISIBLE);
			vh.lblCount.setText(stock.getQty());
			count = Double.parseDouble(vh.lblCount.getText().toString());
		}

		vh.lblItemId.setText(stock.getItemId());
		vh.lblItemName.setText(stock.getItemName());

		// PRICE
		String[] splitPrice = stock.getItemPrice().split("\\.");
		final Double price  = Double.parseDouble(splitPrice[0].toString());
		Double amount = price * count;
		vh.lblPrice.setText(Constant.currencyFormater(amount));

		vh.cellOrder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (blnInsert) {
					listener.onClicked(stock);
				}
			}
		});

		vh.imgDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mActivity.isFinishing()) {
					final String strItem = stock.getItemName();
					new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
							.setTitleText("Anda yakin?")
							.setContentText("Ingin menghapus item " + strItem + " !")
							.setCancelText("Tidak")
							.setConfirmText("Ok")
							.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									listStock.remove(position);
									listener.onRemoved();

									sDialog.setTitleText("Terhapus!")
											.setContentText("Item " + strItem + " berhasil dihapus!")
											.setConfirmText("Ok")
											.showCancelButton(false)
											.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
												@Override
												public void onClick(SweetAlertDialog sDialog) {
													sDialog.cancel();
												}
											})
											.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
								}
							})
							.showCancelButton(true)
							.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									sDialog.cancel();
								}
							})
							.show();
				}
			}
		});

		vh.imgPlus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int countt   = Integer.parseInt(vh.lblCount.getText().toString());
				int newCount = countt + 1;
				vh.lblCount.setText("" + newCount);

				stock.setQty(""+newCount);
				listStock.set(position, stock);

				Double count  = Double.parseDouble(""+newCount);
				Double amount = price * count;
				vh.lblPrice.setText(Constant.currencyFormater(amount));
				listener.onRemoved();
			}
		});

		vh.imgMinus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (vh.lblCount.getText().toString().equalsIgnoreCase("1")) {
					vh.imgMinus.setEnabled(false);
				} else {
					int countt   = Integer.parseInt(vh.lblCount.getText().toString());
					int newCount = countt - 1;
					vh.imgMinus.setEnabled(true);
					vh.lblCount.setText("" + newCount);

					stock.setQty(""+newCount);
					listStock.set(position, stock);

					Double count  = Double.parseDouble("" + newCount);
					Double amount = price * count;
					vh.lblPrice.setText(Constant.currencyFormater(amount));
					listener.onRemoved();
				}
			}
		});

		return convertView;
	}

	static class ViewHolder {
		RelativeLayout cellOrder;
		TextView lblItemId, lblItemName, lblPrice, lblCount;
		ImageView imgDelete;
		LinearLayout linCount;
		ImageView imgPlus, imgMinus;
	}

	public interface OrderAdapterListener {
		void onClicked(Stock stock);
		void onRemoved();
	}
}