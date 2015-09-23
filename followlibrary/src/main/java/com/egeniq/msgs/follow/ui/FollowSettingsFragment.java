package com.egeniq.msgs.follow.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.egeniq.msgs.follow.R;
import com.egeniq.msgs.follow.data.Constants;
import com.egeniq.msgs.follow.data.entity.FollowEndpoint;
import com.egeniq.msgs.follow.data.entity.FollowSubscription;
import com.egeniq.msgs.follow.data.models.FollowModel;
import com.egeniq.support.app.Fragment;

import java.util.ArrayList;

/**
 * Follow Settings.
 */
public class FollowSettingsFragment extends Fragment {
    private final static String TAG = FollowSettingsFragment.class.getName();
    private final static boolean DEBUG = Constants.DEBUG;

    private ViewSwitcher _loginSwitcher;
    private View _loggedInBox;
    private TextView _loggedInAsView;

    private SubscriptionAdapter _subscriptionAdapter;
    private EndpointAdapter _updatesEndpointAdapter;
    private EndpointAdapter _breakingNewsEndpointAdapter;

    private boolean _isTablet;
    private ArrayList<FollowSubscription> _subscriptions = new ArrayList<FollowSubscription>();

    private boolean _hasMoreSubscriptions = false;
    private final BroadcastReceiver _userChangedListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            _updateForUserState();
        }
    };

    private OnClickListener _onClickListener;

    /**
     * Constructor
     */
    public FollowSettingsFragment() {
        // empty
    }

    /**
     * Constructor
     */
    public static FollowSettingsFragment newInstance() {
        return new FollowSettingsFragment();
    }

    /**
     * Create.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _isTablet = getResources().getBoolean(R.bool.isTablet);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(_isTablet ? R.layout.tablet_follow_settings : R.layout.phone_follow_settings, null);

//        if (!_isTablet) {
//            ((TextView)view.findViewById(R.id.headerTitle)).setText(getString(R.string.phone_follow_settings));
//        }

        _loginSwitcher = (ViewSwitcher)view.findViewById(R.id.loginSwitcher);
        _loggedInBox = view.findViewById(R.id.boxLoggedIn);

        LinearListView listSubscriptions = (LinearListView)view.findViewById(R.id.listSubscriptions);
        _subscriptionAdapter = new SubscriptionAdapter(getActivity());
        listSubscriptions.setAdapter(_subscriptionAdapter);

        listSubscriptions.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {
                Object item = parent.getAdapter().getItem(position);
                if (SubscriptionAdapter.TYPE_MORE_ITEM.equals(item)) {
                    // Handle more click
                    _fetchSubscriptions(_subscriptions.size()); // Fetch next 10 items starting from current offset
                } else if (item instanceof FollowSubscription) {
                    // Handle regular item click
                    _openContentItemForSubscription((FollowSubscription)item);
                }
            }
        });

        _subscriptionAdapter.setOnDeleteListener(new SubscriptionAdapter.OnDeleteListener() {
            @Override
            public void onDeleteClick(final FollowSubscription subscription) {
                _subscriptions.remove(subscription);
                _subscriptionAdapter.setItems(_subscriptions.toArray(new FollowSubscription[0]), _hasMoreSubscriptions);

                FollowModel.INSTANCE.unfollow(subscription.getCode(), new FollowModel.OnActionListener() {
                    @Override
                    public void onActionComplete() {
                    }

                    @Override
                    public void onActionError(String code, String message) {
                    }
                });
            }
        });

        LinearListView listEndpoints = (LinearListView)view.findViewById(R.id.listEndpoints);
        _updatesEndpointAdapter = new EndpointAdapter(EndpointAdapter.Mode.UPDATES);
        listEndpoints.setAdapter(_updatesEndpointAdapter);

        _updatesEndpointAdapter.setOnCheckedChangedListener(new EndpointAdapter.OnCheckedChangedListener() {
            @Override
            public void onCheckedChanged(FollowEndpoint endpoint, boolean checked) {
                endpoint.setReceivesUpdates(checked);

                FollowModel.INSTANCE.updateEndpointReceivesUpdates(endpoint.getToken(), checked, new FollowModel.OnActionListener() {
                    @Override
                    public void onActionComplete() {
                    }

                    @Override
                    public void onActionError(String code, String message) {
                    }
                });
            }
        });

        LinearListView listAlertEndpoints = (LinearListView)view.findViewById(R.id.listAlertEndpoints);
        _breakingNewsEndpointAdapter = new EndpointAdapter(EndpointAdapter.Mode.BREAKING_NEWS);
        listAlertEndpoints.setAdapter(_breakingNewsEndpointAdapter);

        _breakingNewsEndpointAdapter.setOnCheckedChangedListener(new EndpointAdapter.OnCheckedChangedListener() {
            @Override
            public void onCheckedChanged(FollowEndpoint endpoint, boolean checked) {
                endpoint.setReceivesBreakingNews(checked);

                FollowModel.INSTANCE.updateEndpointReceivesBreakingNews(endpoint.getToken(), checked, new FollowModel.OnActionListener() {
                    @Override
                    public void onActionComplete() {
                    }

                    @Override
                    public void onActionError(String code, String message) {
                    }
                });
            }
        });

        View loginWithGigyaButton = view.findViewById(R.id.loginWithGigya);
        loginWithGigyaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_onClickListener != null) {
                    _onClickListener.clicked(FollowButton.GIGYA_LOGIN);
                }
            }
        });

        _loggedInAsView = (TextView)view.findViewById(R.id.loggedInAs);
        _loggedInAsView.setText("");

        View logoutButton = view.findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _logout();
            }
        });

        return view;
    }

    /**
     * Resume.
     */
    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        //TODO  connect facebook user silently

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(_userChangedListener, new IntentFilter(FollowModel.ACTION_USER_CHANGED));
        _updateForUserState();
    }

    /**
     * Pause.
     */
    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(_userChangedListener);

        _subscriptions.clear();
        _hasMoreSubscriptions = false;
    }

    /**
     * Update views based on login state.
     */
    private void _updateForUserState() {
        if (FollowModel.INSTANCE.isUserLinked()) {
            _loginSwitcher.setDisplayedChild(0);
            _loggedInBox.setVisibility(View.VISIBLE);
            _loggedInAsView.setText(FollowModel.INSTANCE.getUserEmail());
        } else {
            _loginSwitcher.setDisplayedChild(1);
            _loggedInBox.setVisibility(View.GONE);
        }

        _subscriptionAdapter.setItems(new FollowSubscription[0], false);
        _updatesEndpointAdapter.setItems(new FollowEndpoint[0]);
        _breakingNewsEndpointAdapter.setItems(new FollowEndpoint[0]);

        _fetchSubscriptions();
        _fetchEndpoints();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        _onClickListener = onClickListener;
    }

    /**
     * Logout.
     */
    private void _logout() {
        // logout RTLid => remove cookies
        CookieSyncManager.createInstance(getActivity());
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieStr = cookieManager.getCookie(".rtl.nl");
        if (cookieStr != null) {
            String[] cookies = cookieStr.split(";");
            for (int i = 0; i < cookies.length; i++) {
                String[] parts = cookies[i].split("=");
                cookieManager.setCookie(".rtl.nl", parts[0].trim() + "=; Expires=Wed, 31 Dec 1970 23:59:59 GMT");
            }

            CookieSyncManager.getInstance().sync();
        }

        // forget the user in the msgs.com.egeniq.msgs.follow model
        FollowModel.INSTANCE.unlinkUser();
    }

//    /**
//     * RTLid login.
//     */
//    private void _showGigyaLogin() {
//        Intent intent = new Intent(getActivity(), GigyaActivity.class);
//        startActivity(intent);
//        getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);
//    }

    /**
     * Fetch subscriptions.
     */
    private void _fetchSubscriptions() {
        _subscriptions.clear();
        _hasMoreSubscriptions = false;
        _fetchSubscriptions(0);
    }

    /**
     * Fetch subscriptions.
     *
     * @param offset
     */
    private void _fetchSubscriptions(final int offset) {
        FollowModel.INSTANCE.fetchSubscriptions(10, offset, new FollowModel.OnFetchListListener<FollowSubscription>() {
            @Override
            public void onFetchComplete(FollowSubscription[] subscriptions, boolean hasMore) {
                if (offset == 0) {
                    _subscriptions.clear();
                }

                for (FollowSubscription subscription : subscriptions) {
                    _subscriptions.add(subscription);
                }

                _hasMoreSubscriptions = hasMore;
                _subscriptionAdapter.setItems(_subscriptions.toArray(new FollowSubscription[0]), hasMore);
            }

            @Override
            public void onFetchError(String code, String message) {
                _hasMoreSubscriptions = false;
                _subscriptionAdapter.setItems(_subscriptions.toArray(new FollowSubscription[0]), false); // Sets adapter to empty row.
            }
        });
    }

    /**
     * Fetch endpoints.
     */
    private void _fetchEndpoints() {
        FollowModel.INSTANCE.fetchEndpoints(new FollowModel.OnFetchListListener<FollowEndpoint>() {
            @Override
            public void onFetchComplete(FollowEndpoint[] endpoints, boolean hasMore) {
                _updatesEndpointAdapter.setItems(endpoints);
                _breakingNewsEndpointAdapter.setItems(endpoints);
            }

            @Override
            public void onFetchError(String code, String message) {
                _updatesEndpointAdapter.setItems(new FollowEndpoint[0]);
                _breakingNewsEndpointAdapter.setItems(new FollowEndpoint[0]);
            }
        });
    }

    /**
     * Open content item.
     */
    private void _openContentItemForSubscription(FollowSubscription subscription) {

        //TODO
//        if (subscription.getGuid() > 0 && subscription.getType() != null) {
//            Intent intent;
//            if (_isTablet) {
//                intent = nl.rtl.rtlnieuws.tablet.contentitem.ContentItemActivity.createIntent(getActivity(), subscription.getGuid(), subscription.getType(), -1, false);
//                getActivity().startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.hold);
//            } else {
//                intent = nl.rtl.rtlnieuws.phone.contentitem.ContentItemActivity.createIntent(getActivity(), subscription.getGuid(), subscription.getType(), -1, false);
//                getActivity().startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        } else {
//            if (DEBUG) {
//                Log.d(TAG, "No data available. Cannot open content item.");
//            }
//        }
    }

    public enum FollowButton {
        GIGYA_LOGIN
    }

    public interface OnClickListener {
        void clicked(FollowButton button);
    }
}
