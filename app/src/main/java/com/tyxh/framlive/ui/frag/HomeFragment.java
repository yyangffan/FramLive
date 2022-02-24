package com.tyxh.framlive.ui.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tyxh.framlive.R;
import com.tyxh.framlive.base.LiveBaseFragment;
import com.tyxh.framlive.ui.LiveAnchorActivity;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends LiveBaseFragment {

    @BindView(R.id.home_startlive)
    TextView mHomeStartlive;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void init() {

    }

    @OnClick(R.id.home_startlive)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_startlive:
                statActivity(LiveAnchorActivity.class);
                break;

        }
    }
}