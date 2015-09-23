package com.egeniq.follow_lib_android.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.egeniq.follow_lib_android.R;
import com.egeniq.follow_lib_android.data.entity.FollowSubscription;

import java.util.ArrayList;


/**
 * Adapter to populate ListView for displaying subscriptions.
 */
public class SubscriptionAdapter extends BaseAdapter {
    private static final int TYPE_SUBSCRIPTION = 0;
    private static final int TYPE_MORE = 1;
    private static final int TYPE_EMPTY = 2;
    private static final int TYPE_COUNT = TYPE_EMPTY + 1;

    public static final String TYPE_MORE_ITEM = "MORE";
    public static final String TYPE_EMPTY_ITEM = "EMPTY";

    private Context _context;
    private ArrayList<Object> _items = new ArrayList<Object>();
    private OnDeleteListener _deleteListener;
    private boolean _hasMore;

    public interface OnDeleteListener {
        public void onDeleteClick(FollowSubscription subscription);
    }

    /**
     * Constructor.
     *
     * @param context
     */
    public SubscriptionAdapter(Context context) {
        _context = context;
    }

    /**
     * Set OnDeleteListener.
     *
     * @param listener
     */
    public void setOnDeleteListener(OnDeleteListener listener) {
        _deleteListener = listener;
    }

    /**
     * Set items.
     *
     * @param items
     */
    public void setItems(FollowSubscription[] subscriptions, boolean hasMore) {
        _hasMore = hasMore;
        _items.clear();

        if (subscriptions == null) {
            _items.add(new String(TYPE_EMPTY_ITEM));
        } else {
            for (FollowSubscription subscription : subscriptions) {
                _items.add(subscription);
            }

            if (subscriptions.length == 0) {
                _items.add(new String(TYPE_EMPTY_ITEM));
            } else if (_hasMore) {
                _items.add(new String(TYPE_MORE_ITEM));
            }
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
    public Object getItem(int position) {
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
     * Get view type count.
     */
    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    /**
     * Get view type.
     */
    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof String) {
            if (item.equals(TYPE_EMPTY_ITEM)) {
                return TYPE_EMPTY;
            } else if (item.equals(TYPE_MORE_ITEM)) {
                return TYPE_MORE;
            }
        }
        return TYPE_SUBSCRIPTION;
    }

    /**
     * Get view.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int viewType = getItemViewType(position);

        if (convertView == null) {
            switch (viewType) {
                case TYPE_SUBSCRIPTION:
                    convertView = LayoutInflater.from(_context).inflate(R.layout.follow_subscription_item, parent, false);
                    holder = new ViewHolder();
                    holder.name = (TextView)convertView.findViewById(R.id.name);
                    holder.deleteButton = (ImageButton)convertView.findViewById(R.id.delete);
                    convertView.setTag(holder);
                    break;
                case TYPE_MORE:
                    convertView = LayoutInflater.from(_context).inflate(R.layout.follow_subscription_item_more, parent, false);
                    break;
                case TYPE_EMPTY:
                    convertView = LayoutInflater.from(_context).inflate(R.layout.follow_subscription_item_empty, parent, false);
                    break;
            }
        } else {
            if (viewType == TYPE_SUBSCRIPTION) {
                holder = (ViewHolder)convertView.getTag();
            }
        }

        if (viewType == TYPE_SUBSCRIPTION) {
            final FollowSubscription item = (FollowSubscription)getItem(position);
            holder.name.setText(item.getName());
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_deleteListener != null) {
                        _deleteListener.onDeleteClick(item);
                    }
                }
            });
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        ImageButton deleteButton;
    }
}
