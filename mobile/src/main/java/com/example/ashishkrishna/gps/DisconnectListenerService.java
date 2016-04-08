package com.example.ashishkrishna.gps;

/**
 * Created by ashishkrishna on 8/13/15.
 */

import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.Set;



/**
 * Listens for changes in connectivity between this wear device and the phone. More precisely, we
 * need to distinguish the case that the wear device and the phone are connected directly from all
 * other possible cases. To this end, the phone app has registered itself to provide the "find_me"
 * capability and we need to look for connected nodes that provide this capability AND are nearby,
 * to exclude a connection through the cloud. The proper way would have been to use the
 * {@code onCapabilitiesChanged()} callback but currently that callback cannot discover the case
 * where a connection switches from wifi to direct; this shortcoming will be addressed in future
 * updates but for now we will use the {@code onConnectedNodes()} callback.
 */
public class DisconnectListenerService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "ExampleFindPhoneApp";

    private static final int FORGOT_PHONE_NOTIFICATION_ID = 1;

    public static String bestNode;
    /* the capability that the phone app would provide */
    private static final String FIND_ME_CAPABILITY_NAME = "do_stuff_better";

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        initializer();
    }

    @Override
    public void onConnectedNodes(List<Node> connectedNodes) {
        // After we are notified by this callback, we need to query for the nodes that provide the
        // "find_me" capability and are directly connected.
        if (mGoogleApiClient.isConnected()) {
            setOrUpdateNotification();
        } else if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    public void initializer() {
        if (mGoogleApiClient.isConnected()) {
            setOrUpdateNotification();
        } else if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }


    private void setOrUpdateNotification() {
        Wearable.CapabilityApi.getCapability(
                mGoogleApiClient, FIND_ME_CAPABILITY_NAME,
                CapabilityApi.FILTER_REACHABLE).setResultCallback(
                new ResultCallback<CapabilityApi.GetCapabilityResult>() {
                    @Override
                    public void onResult(CapabilityApi.GetCapabilityResult result) {
                        if (result.getStatus().isSuccess()) {
                            Log.w("FOUND", "FOUND");
                            updateFindMeCapability(result.getCapability());
                        } else {
                            Log.e(TAG,
                                    "setOrUpdateNotification() Failed to get capabilities, "
                                            + "status: "
                                            + result.getStatus().getStatusMessage());
                        }
                    }
                });
    }

    private void updateFindMeCapability(CapabilityInfo capabilityInfo) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();
        if (connectedNodes.isEmpty()) {
            Log.w("This was empty", "This was empty");
        } else {
            for (Node node : connectedNodes) {
                Log.w("NOde:", node.toString());
                // we are only considering those nodes that are directly connected
                if (node.isNearby()) {
                    bestNode = node.getId();
                    ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                            .cancel(FORGOT_PHONE_NOTIFICATION_ID);
                }
            }
        }
    }

    /**
     * Creates a notification to inform user that the connectivity to phone has been lost (possibly
     * left the phone behind).
     */

    @Override
    public void onConnected(Bundle bundle) {
        setOrUpdateNotification();
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

}

