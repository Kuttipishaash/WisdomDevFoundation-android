package com.wisdom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NoNetworkFragment extends Fragment {

    Button mRetryButton;

    public NoNetworkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no_network, container, false);

        //TODO: Add toolbar to this fragment

        mRetryButton = view.findViewById(R.id.btn_retry_network);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() instanceof NewsFeedActivity) {
                    startActivity(new Intent(getActivity(), NewsFeedActivity.class));
                }
            }
        });

        return view;
    }

}
