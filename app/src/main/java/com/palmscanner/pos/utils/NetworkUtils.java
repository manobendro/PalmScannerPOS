package com.palmscanner.pos.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

public class NetworkUtils {

    private final Context context;
    private final NetworkCallback callback;
    private final ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private BroadcastReceiver networkReceiver;
    private boolean isMonitoring = false;
    public NetworkUtils(Context context, NetworkCallback callback) {
        this.context = context;
        this.callback = callback;
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                    return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                }
            } else {
                // For devices below Android 6.0
                android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }

    public void startMonitoring() {
        if (isMonitoring) return; // Already monitoring
        isMonitoring = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0+ (API 24 and above) uses NetworkCallback
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    if (callback != null) callback.onNetworkChanged(true);
                }

                @Override
                public void onLost(Network network) {
                    if (callback != null) callback.onNetworkChanged(false);
                }
            };
            connectivityManager.registerDefaultNetworkCallback(networkCallback);

        } else {
            // For Android versions below 7.0, use BroadcastReceiver
            networkReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean isConnected = isInternetAvailable(context);
                    if (callback != null) callback.onNetworkChanged(isConnected);
                }
            };
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(networkReceiver, filter);
        }
    }

    public void stopMonitoring() {
        if (!isMonitoring) return; // Not monitoring
        isMonitoring = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (networkCallback != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            }
        } else {
            if (networkReceiver != null) {
                context.unregisterReceiver(networkReceiver);
            }
        }
    }

    public interface NetworkCallback {
        void onNetworkChanged(boolean isConnected);
    }
}

