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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Austin on 8/26/2016.
 */
public class ProfileListAdapter extends BaseAdapter {
    //    private final ArrayList<String> accountNames;
    private final Context context;
    //    private final ArrayList<Uri> data;
    private final ArrayList<Map.Entry<String, Uri>> entries;
    private static LayoutInflater inflater = null;

    public ProfileListAdapter(Context context, HashMap<String, Uri> links) {
        this.context = context;
//        this.accountNames = new ArrayList<>(links.keySet());
//        this.data = new ArrayList<>(links.values());
        this.entries = new ArrayList<>(links.entrySet());
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return entries.get(position);
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
        text.setText(entries.get(position).getKey());
        final Uri link = entries.get(position).getValue();
        Button visitButton = (Button) vi.findViewById(R.id.visit_account_button);
        if (link == null) {
            visitButton.setClickable(false);
            return vi;
        }
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
