package com.pos.cashier.adapter;

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
import com.pos.cashier.R;
import com.pos.cashier.model.Transaction;
import com.pos.cashier.referensi.Constant;
import com.pos.cashier.referensi.FontCache;
import java.util.ArrayList;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LihatOrderAdapter extends BaseAdapter {
	private ArrayList<Transaction> listTransaction = null;
	private static LayoutInflater inflater = null;
	private Typeface fontDroidSans, fontDroidSansBold;
	private Activity mActivity;
	private LihatOrderAdapterListener listener;
	public Double totalPrice = 0.0D;
	private String strStatus;
	public boolean blnChange = false;
	public ArrayList<Integer> listChange = new ArrayList<>();
	private String[] splitPrice;

	public LihatOrderAdapter(Activity activity, LihatOrderAdapterListener listener, String strStatus) {
		inflater 		  = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fontDroidSans     = FontCache.get(activity, "DroidSans");
		fontDroidSansBold = FontCache.get(activity, "DroidSans-Bold");
		mActivity	      = activity;
		this.listener  	  = listener;
		this.strStatus	  = strStatus;
	}

	public void updateListTransaction(ArrayList<Transaction> newListTransaction) {
		this.listTransaction = newListTransaction;
		totalPrice	   		 = 0.0D;

		for (int i=0; i<newListTransaction.size(); i++) {
			Double count  = Double.parseDouble(newListTransaction.get(i).getQty());
			String[] splitPrice = newListTransaction.get(i).getPrice().split("\\.");
			Double price  = Double.parseDouble(splitPrice[0].toString());
			//Double amount = price * count;
			totalPrice    = totalPrice + price;

			// Check if count change
			Double oldCount = Double.parseDouble(listTransaction.get(i).getQty());
			if (count != oldCount) {
				listChange.add(i);
				blnChange = true;
			}
		}
	}

	@Override
	public int getCount() {
		return this.listTransaction.size();
	}

	@Override
	public Transaction getItem(int position) {
		return this.listTransaction.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final ViewHolder vh;
		final Transaction transaction = this.listTransaction.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.cell_order, parent, false);
			vh = new ViewHolder();

			vh.cellOrder   = (RelativeLayout) convertView.findViewById(R.id.cellOrder);
			vh.lblItemId   = (TextView) convertView.findViewById(R.id.lblItemId);
			vh.lblItemName = (TextView) convertView.findViewById(R.id.lblItemName);
			vh.lblPrice    = (TextView) convertView.findViewById(R.id.lblPrice);
			vh.lblCount	   = (TextView) convertView.findViewById(R.id.lblCount);
			vh.linCount	   = (LinearLayout) convertView.findViewById(R.id.linCount);
			vh.imgPlus	   = (ImageView) convertView.findViewById(R.id.imgPlus);
			vh.imgMinus	   = (ImageView) convertView.findViewById(R.id.imgMinus);
			vh.imgDelete   = (ImageView) convertView.findViewById(R.id.imgDelete);

			vh.lblCount.setTypeface(fontDroidSans);
			vh.lblItemId.setTypeface(fontDroidSans, Typeface.BOLD);
			vh.lblItemName.setTypeface(fontDroidSans);
			vh.lblPrice.setTypeface(fontDroidSans, Typeface.BOLD);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		if (strStatus.equalsIgnoreCase("BATAL") || strStatus.equalsIgnoreCase("PROSES")) {
			vh.imgDelete.setVisibility(View.GONE);
			vh.imgPlus.setVisibility(View.GONE);
			vh.imgMinus.setVisibility(View.GONE);
		}

		vh.linCount.setVisibility(View.VISIBLE);
		vh.lblCount.setText(transaction.getQty());
		vh.lblItemId.setText(transaction.getStockId());
		vh.lblItemName.setText(transaction.getStockName());

		// PRICE
		Double count = Double.parseDouble(vh.lblCount.getText().toString());
		final String[] splitPrice = transaction.getPrice().split("\\.");
		final Double price  = Double.parseDouble(splitPrice[0].toString()) / count;
		Double amount = price * count;
		vh.lblPrice.setText(Constant.currencyFormater(amount));

		vh.imgDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mActivity.isFinishing()) {
					final String strItem = transaction.getStockName();
					new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
							.setTitleText("Anda yakin?")
							.setContentText("Ingin menghapus item " + strItem + " !")
							.setCancelText("Tidak")
							.setConfirmText("Ok")
							.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									listTransaction.remove(position);
									listener.onRemoved(true, transaction.getStockName());

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

				Double count  = Double.parseDouble(""+newCount);
				Double dPrice = Double.parseDouble(splitPrice[0].toString()) / countt;
				Double amount = dPrice * count;

				transaction.setQty(""+newCount);
				transaction.setPrice(""+amount);
				listTransaction.set(position, transaction);

				listener.onRemoved(false, "");
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

					Double count  = Double.parseDouble("" + newCount);
					Double dPrice = Double.parseDouble(splitPrice[0].toString()) / countt;
					Double amount = dPrice * count;

					transaction.setQty(""+newCount);
					transaction.setPrice(""+amount);
					listTransaction.set(position, transaction);

					listener.onRemoved(false, "");
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

	public interface LihatOrderAdapterListener {
		void onRemoved(boolean blnRemoved, String strName);
	}
}