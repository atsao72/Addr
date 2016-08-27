package com.rook.addr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Austin on 8/26/2016.
 */
public class ProfileListAdapter extends BaseAdapter {
    private static final String[] accountNames = {"Facebook", "Instagram", "Twitter"};
    private final Context context;
    private final Uri[] data;
    private static LayoutInflater inflater = null;

    public ProfileListAdapter(Context context, Uri[] data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.profile_account_item, null);
        TextView text = (TextView) vi.findViewById(R.id.accountName);
        text.setText(accountNames[position]);
        final Uri link = data[position];
        Button visitButton = (Button) vi.findViewById(R.id.visit_account_button);
        visitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                context.startActivity(intent);
            }
        });
        return vi;
    }
}
