package digify.tv.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import javax.inject.Inject;

import digify.tv.DigifyApp;
import digify.tv.R;
import digify.tv.api.models.CustomerModel;
import digify.tv.core.PreferenceManager;


public class QueueFragment extends Fragment {

    private int mColumnCount = 1;

    @Inject
    DatabaseReference db;
    @Inject
    PreferenceManager preferenceManager;

    public QueueFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DigifyApp.get(getActivity()).getComponent().inject(this);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new QueueAdapter(CustomerModel.class, R.layout.fragment_customer, QueueAdapter.CustomerHolder.class,getCustomersQuery()));
        }
        return view;
    }

    public Query getCustomersQuery() {
        return db
                .child("joel")
                .limitToFirst(5);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
