package com.droi.sample.push.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droi.sample.push.MainActivity;
import com.droi.sample.push.models.MyUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.karim.MaterialTabs;

import com.droi.sample.push.FragmentNavigator;
import com.droi.sample.push.R;

import java.util.ArrayList;

/**
 * Created by skyer on 2017/9/4.
 */

public class MainFragment extends Fragment {

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager.setAdapter(new ViewPagerAdapter(getFragmentManager()));

        // Bind the tabs to the ViewPager
        MaterialTabs tabs = (MaterialTabs) getActivity().findViewById(R.id.material_tabs);
        tabs.setViewPager(viewPager);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter implements MaterialTabs.CustomTabProvider {
        private ArrayList<Fragment> list;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

            list = new ArrayList<>();
            list.add(new PushMainFragment());
            list.add(new PushSetupFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public View getCustomTabView(ViewGroup parent, int position) {
            int textId = (position == 0) ? R.string.main : R.string.setup;
            TextView view = new TextView(parent.getContext());
            view.setText(textId);
            view.setGravity(Gravity.CENTER);
            view.setTextColor(Color.WHITE);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            view.setTypeface(null, Typeface.BOLD);
            return view;
        }

        @Override
        public void onCustomTabViewSelected(View view, int position, boolean alreadySelected) {
        }

        @Override
        public void onCustomTabViewUnselected(View view, int position, boolean alreadyUnselected) {
        }
    }
}
