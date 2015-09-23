package com.egeniq.follow_lib_android.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.egeniq.follow_lib_android.R;
import com.egeniq.follow_lib_android.data.entity.FollowEndpoint;

import java.util.ArrayList;


/**
 * Endpoint adapter.
 */
public class EndpointAdapter extends BaseAdapter {
    public enum Mode {
        BREAKING_NEWS, UPDATES
    }

    ;

    public interface OnCheckedChangedListener {
        public void onCheckedChanged(FollowEndpoint endpoint, boolean checked);
    }

    private final Mode _mode;
    private final ArrayList<FollowEndpoint> _items = new ArrayList<FollowEndpoint>();

    private OnCheckedChangedListener _checkedChangedListener;

    /**
     * Constructor.
     *
     * @param context
     */
    public EndpointAdapter(Mode mode) {
        _mode = mode;
    }

    /**
     * Set checked listener.
     *
     * @param listener
     */
    public void setOnCheckedChangedListener(OnCheckedChangedListener listener) {
        _checkedChangedListener = listener;
    }

    /**
     * Set items.
     *
     * @param items
     */
    public void setItems(FollowEndpoint[] endpoints) {
        _items.clear();

        for (FollowEndpoint endpoint : endpoints) {
            _items.add(endpoint);
        }

        notifyDataSetChanged();
    }

    /**
     * Get count.
     */
    @Override
    public int getCount() {
        return _items.size();
    }

    /**
     * Get item.
     */
    @Override
    public FollowEndpoint getItem(int position) {
        return _items.get(position);
    }

    /**
     * Get item id.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Disable item.
     */
    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    /**
     * View holder.
     */
    private static class ViewHolder {
        TextView name;
        CompoundButton toggle;
    }

    /**
     * Get view.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_endpoint_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.toggle = (CompoundButton)convertView.findViewById(R.id.toggle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.name.setEnabled(isEnabled(position));
        holder.toggle.setEnabled(isEnabled(position));

        holder.name.setText(getItem(position).getName());
        holder.toggle.setChecked(_mode == Mode.BREAKING_NEWS ? getItem(position).getReceivesBreakingNews() : getItem(position).getReceivesUpdates());
        holder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (_checkedChangedListener != null) {
                    _checkedChangedListener.onCheckedChanged(getItem(position), isChecked);
                }
            }
        });

        return convertView;
    }
}