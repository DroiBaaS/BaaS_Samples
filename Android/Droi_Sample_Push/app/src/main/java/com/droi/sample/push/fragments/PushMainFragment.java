package com.droi.sample.push.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droi.sample.push.R;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.TaskDispatcher;
import com.droi.sdk.push.DroiPush;
import com.droi.sdk.push.utils.GetDeviceIdCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by skyer on 2017/10/6.
 */

public class PushMainFragment extends Fragment {

    @BindView(R.id.display_device_id)
    TextView displayIdView;

    @BindView(R.id.display_tags)
    TextView tagsView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.push_main_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Device Id
        DroiPush.getDeviceIdInBackground(getActivity(), new GetDeviceIdCallback() {
            @Override
            public void onGetDeviceId(String s) {
                Log.d("DROI_PUSH", "DeviceID: " + s);
                displayIdView.setText(s);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Get all push tags
            DroiPush.queryTagsInBackground(getActivity(), new DroiCallback<String>() {
                @Override
                public void result(final String tags, DroiError droiError) {
                    if (!droiError.isOk()) {
                        Log.e("Push", "Query tags fail. " + droiError.toString());
                        return;
                    }

                    TaskDispatcher.getDispatcher(TaskDispatcher.MainThreadName).enqueueTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jobj = new JSONObject(tags);
                                String tagStrs = jobj.getString("tags");
                                if (tagStrs == null || tagStrs.isEmpty())
                                    tagStrs = getResources().getString(R.string.empty_tags);
                                tagsView.setText(tagStrs);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }
}
