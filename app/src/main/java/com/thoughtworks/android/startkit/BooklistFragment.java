package com.thoughtworks.android.startkit;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtworks.android.startkit.retrofit.DouBanResponseData;
import com.thoughtworks.android.startkit.retrofit.DouBanDataTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BooklistFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "BooklistFragment";
    int DATA_INITIAL_START = 0;


    @BindView(android.R.id.list)
    RecyclerView mListView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.view_loading_more)
    View loadingView;

    private BooklistAdapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;

    private boolean isLoading;
    private boolean hasMoreItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        ButterKnife.bind(this,view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mListView.setHasFixedSize(true);


        final int columns = getResources().getInteger(R.integer.gallery_columns);
        mLayoutManager = new GridLayoutManager(getContext(), columns);
        mListView.setLayoutManager(mLayoutManager);

        mAdapter = new BooklistAdapter();
        mListView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int firstVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();

                if (totalItemCount > 0) {
                    int lastVisibleItem = firstVisibleItem + visibleItemCount;
                    if (!isLoading && hasMoreItems && (lastVisibleItem == totalItemCount)) {
                        doLoadMoreData();
                    }
                }
            }
        });

        doRefreshData();

        return view;
    }

    @Override
    public void onRefresh() {
        doRefreshData();
    }

    private void doRefreshData() {
        new DouBanDataTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isLoading = true;
                if (!swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            protected void onPostExecute(BookData data) {
                super.onPostExecute(data);
                isLoading = false;
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                hasMoreItems = data.getTotal() - (data.getStart() + data.getCount()) > 0;
                mAdapter.clearAll();
                mAdapter.addAll(data.getBooks());
            }
        }.execute(DATA_INITIAL_START);
    }

    private void doLoadMoreData() {
        Log.d(TAG, "load more data for ListView");

        new DouBanDataTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingMore();
                isLoading = true;
            }

            @Override
            protected void onPostExecute(BookData data) {
                super.onPostExecute(data);
                isLoading = false;
                hasMoreItems = data.getTotal() - (data.getStart() + data.getCount()) > 0;
                hideLoadingMore();
                mAdapter.addAll(data.getBooks());
            }
        }.execute(mAdapter.getItemCount());

    }

    private void showLoadingMore() {
        loadingView.setVisibility(VISIBLE);
    }

    private void hideLoadingMore() {
        loadingView.setVisibility(GONE);
    }
}
