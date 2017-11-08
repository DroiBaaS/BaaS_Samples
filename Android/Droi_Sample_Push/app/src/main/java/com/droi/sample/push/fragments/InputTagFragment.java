package com.droi.sample.push.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.droi.sample.push.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by skyer on 2017/10/16.
 */

public class InputTagFragment extends DialogFragment {

    @BindView(R.id.tag_name)
    EditText tagNameView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_tag_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.ok)
    public void onOkClick() {
        String tag = tagNameView.getText().toString();
        if (tag != null && !tag.isEmpty()) {
            Fragment target = getTargetFragment();
            if (target != null) {
                Intent intent = new Intent();
                intent.putExtra("tag", tag);
                target.onActivityResult(getTargetRequestCode(), 0, intent);
            }
        }
        dismiss();
    }

    @OnClick(R.id.cancel)
    public void onCancelClick() {
        dismiss();
    }
}
