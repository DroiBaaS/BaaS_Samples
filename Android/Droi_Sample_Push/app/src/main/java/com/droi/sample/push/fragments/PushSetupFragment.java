package com.droi.sample.push.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.droi.sample.push.Constants;
import com.droi.sample.push.R;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.TaskDispatcher;
import com.droi.sdk.push.DroiPush;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by skyer on 2017/10/6.
 */

public class PushSetupFragment extends Fragment {

    @BindView(R.id.enable_push)
    CheckBox enablePushView;

    @BindView(R.id.enable_silent)
    CheckBox enableSilentView;

    @BindView(R.id.from_hour)
    Spinner fromHour;

    @BindView(R.id.from_minute)
    Spinner fromMinute;

    @BindView(R.id.to_hour)
    Spinner toHour;

    @BindView(R.id.to_minute)
    Spinner toMinute;

    @BindView(R.id.tag_list)
    ListView tagList;

    private ArrayAdapter<String> tagsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.push_setup_layout, container, false);
        ButterKnife.bind(this, view);

        enablePushView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                DroiPush.setPushableState(getActivity(), checked);
            }
        });

        enableSilentView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                updateSilentTime();

                fromHour.setEnabled(checked);
                fromMinute.setEnabled(checked);
                toHour.setEnabled(checked);
                toMinute.setEnabled(checked);
            }
        });

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateSilentTime();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        fromHour.setOnItemSelectedListener(itemSelectedListener);
        fromMinute.setOnItemSelectedListener(itemSelectedListener);
        toHour.setOnItemSelectedListener(itemSelectedListener);
        toMinute.setOnItemSelectedListener(itemSelectedListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tagsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.single_text_item, R.id.title);
        tagList.setAdapter(tagsAdapter);

        enablePushView.setChecked(DroiPush.getPushableState(getActivity()));
        int[] time = DroiPush.getSilentTime(getActivity());
        if (time == null) {
            enableSilentView.setChecked(false);
            fromHour.setEnabled(false);
            fromMinute.setEnabled(false);
            toHour.setEnabled(false);
            toMinute.setEnabled(false);
        } else {
            enableSilentView.setChecked(true);
            fromHour.setEnabled(true);
            fromMinute.setEnabled(true);
            toHour.setEnabled(true);
            toMinute.setEnabled(true);

            fromHour.setSelection(time[0]);
            fromMinute.setSelection(time[1]);
            toHour.setSelection(time[2]);
            toMinute.setSelection(time[3]);
        }

        DroiPush.queryTagsInBackground(getActivity(), new DroiCallback<String>() {
            @Override
            public void result(final String tags, DroiError droiError) {
                if (!droiError.isOk()) {
                    Log.e("PUSH", "query tags fail. " + droiError.toString());
                    return;
                }

                TaskDispatcher.getDispatcher(TaskDispatcher.MainThreadName).enqueueTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jobj = new JSONObject(tags);
                            String tagStr = jobj.getString("tags");
                            tagsAdapter.clear();
                            if (tagStr == null || tagStr.isEmpty())
                                return;

                            tagsAdapter.addAll(tagStr.split(","));
                            tagsAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_ADD_TAG) {
            final String tag = data.getStringExtra("tag");
            if (tag == null) return;

            for (int i=0; i<tagsAdapter.getCount(); ++i) {
                if (tag.equals(tagsAdapter.getItem(i)))
                    return;
            }

            DroiPush.appendTagsInBackground(getActivity(), tag, new DroiCallback<String>() {
                @Override
                public void result(String result, DroiError droiError) {
                    if (!droiError.isOk()) {
                        Log.e("PUSH", "Add tag fail. " + droiError);
                        return;
                    }

                    TaskDispatcher.getDispatcher(TaskDispatcher.MainThreadName).enqueueTask(new Runnable() {
                        @Override
                        public void run() {
                            tagsAdapter.add(tag);
                            tagsAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });

            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateSilentTime() {
        if (!enableSilentView.isChecked()) {
            DroiPush.clearSilentTime(getActivity());
            return;
        }

        int fh = fromHour.getSelectedItemPosition();
        int fm = fromMinute.getSelectedItemPosition();
        int th = toHour.getSelectedItemPosition();
        int tm = toMinute.getSelectedItemPosition();

        DroiPush.setSilentTime(getActivity(), fh, fm, th, tm);
    }

    @OnClick(R.id.add_tag_button)
    public void onAddTagClick() {
        DialogFragment fragment = new InputTagFragment();
        fragment.setTargetFragment(this, Constants.REQUEST_ADD_TAG);

        fragment.show(getFragmentManager(), "dialog");
    }

    @OnClick(R.id.remove_tag_button)
    public void onRemoveTagClick() {
        int selected = tagList.getCheckedItemPosition();
        if (selected < 0)
            return;

        final String tag = tagsAdapter.getItem(selected);

        DroiPush.deleteTagsInBackground(getActivity(), tag, new DroiCallback() {
            @Override
            public void result(Object o, DroiError droiError) {
                if (!droiError.isOk()) {
                    Log.e("PUSH", "delete tag fail. " + droiError.toString());
                    return;
                }

                TaskDispatcher.getDispatcher(TaskDispatcher.MainThreadName).enqueueTask(new Runnable() {
                    @Override
                    public void run() {
                        tagsAdapter.remove(tag);
                        tagsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

}
