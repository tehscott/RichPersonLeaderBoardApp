package com.mattandmikeandscott.richpersonleaderboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mattandmikeandscott.richpersonleaderboard.R;
import com.mattandmikeandscott.richpersonleaderboard.domain.Achievement;
import com.mattandmikeandscott.richpersonleaderboard.domain.Payment;

import java.text.NumberFormat;
import java.util.List;

public class PaymentListAdapter extends BaseAdapter {
    private Context context;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance();
    private List<Payment> payments;

    public PaymentListAdapter(Context context, List<Payment> payments) {
        this.context = context;
        this.payments = payments;
    }

    public int getCount() {
        return payments.size();
    }

    public Object getItem(int position) {
        return payments.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final Payment payment = payments.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.payment_list_item, null);

        if(position % 2 == 0) {
            convertView.setBackgroundResource(R.color.colorLightBlue);
        } else {
            convertView.setBackgroundResource(android.R.color.white);
        }

        ((TextView) convertView.findViewById(R.id.payment_date_text)).setText(payment.getDate().toString());
        ((TextView) convertView.findViewById(R.id.payment_amount_text)).setText(formatter.format(payment.getAmount()));

        return convertView;
    }
}