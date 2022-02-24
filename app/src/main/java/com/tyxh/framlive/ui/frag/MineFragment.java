package com.tyxh.framlive.ui.frag;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tyxh.framlive.R;
import com.tyxh.framlive.base.LiveBaseFragment;
import com.tyxh.framlive.ui.LiveAudienceActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MineFragment extends LiveBaseFragment {

    @BindView(R.id.mine_tv)
    TextView mMineTv;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void init() {

    }

    @OnClick(R.id.mine_tv)
    public void onClick() {
        startActivity(new Intent(getActivity(), LiveAudienceActivity.class));
    }
}