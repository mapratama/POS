package com.pos.cashier.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pos.cashier.R;
import com.pos.cashier.model.Transaction;
import com.pos.cashier.referensi.FontCache;
import java.util.ArrayList;

public class ListOrderAdapter extends BaseAdapter {
	private ArrayList<Transaction> listTransaction = null;
	private static LayoutInflater inflater = null;
	private Typeface fontDroidSans, fontDroidSansBold;
	private Activity activity;
	private ListOrderAdapterListener listener;

	public ListOrderAdapter(Activity mActivity, ArrayList<Transaction> mListTransaction, ListOrderAdapterListener mListener) {
		inflater 		  = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fontDroidSans     = FontCache.get(mActivity, "DroidSans");
		fontDroidSansBold = FontCache.get(mActivity, "DroidSans-Bold");
		listTransaction   = mListTransaction;
		activity		  = mActivity;
		listener		  = mListener;
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
			convertView = inflater.inflate(R.layout.cell_list_order, parent, false);
			vh = new ViewHolder();

			vh.cellOrder    = (RelativeLayout) convertView.findViewById(R.id.cellOrder);
			vh.lblOrderDate = (TextView) convertView.findViewById(R.id.lblOrderDate);
			vh.lblOrderName = (TextView) convertView.findViewById(R.id.lblOrderName);
			vh.lblStatus    = (TextView) convertView.findViewById(R.id.lblStatus);
			vh.lblAmount	= (TextView) convertView.findViewById(R.id.lblAmount);

			vh.lblOrderDate.setTypeface(fontDroidSans);
			vh.lblOrderName.setTypeface(fontDroidSans, Typeface.BOLD);
			vh.lblStatus.setTypeface(fontDroidSans);
			vh.lblAmount.setTypeface(fontDroidSans, Typeface.BOLD);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		if (transaction.getStatus().equalsIgnoreCase("PROSES")) {
			vh.lblStatus.setTextColor(activity.getResources().getColor(android.R.color.holo_green_dark));
		} else if (transaction.getStatus().equalsIgnoreCase("TUNDA")) {
			vh.lblStatus.setTextColor(activity.getResources().getColor(android.R.color.black));
		} else {
			vh.lblStatus.setTextColor(activity.getResources().getColor(android.R.color.holo_red_dark));
		}

		vh.lblOrderDate.setText(transaction.getDate());
		vh.lblOrderName.setText(transaction.getInvId());
		vh.lblStatus.setText(transaction.getStatus());
		vh.lblAmount.setText(transaction.getAmount());

		vh.cellOrder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				listener.onClicked(transaction, transaction.getStatus());
			}
		});

		return convertView;
	}

	static class ViewHolder {
		RelativeLayout cellOrder;
		TextView lblOrderDate, lblOrderName, lblStatus, lblAmount;
	}

	public interface ListOrderAdapterListener {
		void onClicked(Transaction transaction, String strStatus);
	}
}