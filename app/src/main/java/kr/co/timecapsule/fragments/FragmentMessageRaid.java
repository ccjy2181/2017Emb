package kr.co.timecapsule.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import kr.co.timecapsule.MainActivity;
import kr.co.timecapsule.R;
import kr.co.timecapsule.adapter.MessagesAdapter;
import kr.co.timecapsule.dto.BoardDTO;
import kr.co.timecapsule.dto.MessageDTO;
import kr.co.timecapsule.helper.RaidRecyclerView;

public class FragmentMessageRaid extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MessagesAdapter.MessageAdapterListener{

    private List<MessageDTO> data;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RaidRecyclerView mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_raid, null, false);

        data = new ArrayList<>();

        getActivity().supportInvalidateOptionsMenu();
        ((MainActivity) getActivity()).changeTitle(R.id.toolbar, "메시지 레이드");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RaidRecyclerView(getContext(), data);
        mRecyclerView.setAdapter (mAdapter);

        return view;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onIconClicked(int position) {

    }

    @Override
    public void onIconImportantClicked(int position) {

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onRowLongClicked(int position) {

    }
}
