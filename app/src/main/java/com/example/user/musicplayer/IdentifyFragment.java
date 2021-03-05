package com.example.user.musicplayer;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.animation.AnimationUtils;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acrcloud.rec.sdk.ACRCloudClient;
import com.acrcloud.rec.sdk.ACRCloudConfig;
import com.acrcloud.rec.sdk.IACRCloudListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class IdentifyFragment extends Fragment implements IACRCloudListener {

    private TextView identify, errorMessage;
    private ImageView identifyIcon;

    private ACRCloudClient mClient;
    private ACRCloudConfig mConfig;

    private boolean mProcessing = false;
    private boolean initState = false;

    private Intent intent;
    public IdentifyFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_identify, container, false);
        intent = new Intent(getContext(),SongIdentified.class);

        identify = (TextView) view.findViewById(R.id.idenify);
        errorMessage = (TextView) view.findViewById(R.id.message);
        identifyIcon = (ImageView) view.findViewById(R.id.identify_icon);

        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

        mConfig = new ACRCloudConfig();
        mConfig.acrcloudListener = this;
        mConfig.accessKey = "ede7f191c9855069915ea15dbf30d6bd";
        mConfig.accessSecret = "Mc4Ac2TLd5tv6q9VUgpizs25EMEg9M8muYPGBOWq";
        mConfig.host = "identify-eu-west-1.acrcloud.com";
        mConfig.context = getContext();
        mConfig.protocol = ACRCloudConfig.ACRCloudNetworkProtocol.PROTOCOL_HTTP;
        mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;

        mClient = new ACRCloudClient();
        //mClient.initWithConfig(mConfig);

        initState = this.mClient.initWithConfig(this.mConfig);
        if (this.initState) {
            this.mClient.startPreRecord(3000);
        }
        identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifyIcon.animate().rotation(1000f).setDuration(12000);

                start();
            }
        });
        return view;
    }


    @Override
    public void onResult(String result) {
        if (this.mClient != null) {
            this.mClient.cancel();
            mProcessing = false;
        }
        try {
            JSONObject j = new JSONObject(result);
            JSONObject j1 = j.getJSONObject("status");
            int j2 = j1.getInt("code");
            if(j2 == 0){
                JSONObject metadata = j.getJSONObject("metadata");
                //
                if (metadata.has("humming")) {
                    JSONArray hummings = metadata.getJSONArray("humming");
                    JSONObject tt = (JSONObject) hummings.get(0);
                    String title = tt.getString("title");
                    JSONArray artistt = tt.getJSONArray("artists");
                    JSONObject art = (JSONObject) artistt.get(0);
                    String artist = art.getString("name");
                }
                if (metadata.has("music")) {
                    JSONArray musics = metadata.getJSONArray("music");
                    JSONObject tt = (JSONObject) musics.get(0);
                    String title = tt.getString("title");

                    intent.putExtra("title", title);

                    JSONArray artistt = tt.getJSONArray("artists");
                    JSONObject art = (JSONObject) artistt.get(0);
                    String artist = art.getString("name");
                    intent.putExtra("artist", artist);
                }
                if (metadata.has("streams")) {
                    JSONArray musics = metadata.getJSONArray("streams");
                        JSONObject tt = (JSONObject) musics.get(0);
                        String title = tt.getString("title");
                        String channelId = tt.getString("channel_id");
                }
                if (metadata.has("custom_files")) {
                    JSONArray musics = metadata.getJSONArray("custom_files");
                        JSONObject tt = (JSONObject) musics.get(0);
                        String title = tt.getString("title");
                }
                Log.i("bnm", result);
                startActivity(intent);
            } else{
                errorMessage.setText("Opps..."+"\n"+"Some Problem Occured");
                Toast.makeText(getActivity(), "Could'nt Identify", Toast.LENGTH_LONG).show();
                identifyIcon.clearAnimation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onVolumeChanged(double v) {

    }
    public void start() {
        if (!this.initState) {
            Toast.makeText(getActivity(), "init error", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mProcessing) {
            mProcessing = true;
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false;
            }
        }
    }

}
