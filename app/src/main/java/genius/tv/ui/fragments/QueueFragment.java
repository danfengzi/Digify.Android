package genius.tv.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.gotev.speech.Speech;

import javax.inject.Inject;

import genius.tv.GeniusApp;
import genius.tv.R;
import genius.tv.api.models.CustomerModel;
import genius.tv.core.CustomerProcessor;
import genius.tv.core.PreferenceManager;


public class QueueFragment extends Fragment {

    private int mColumnCount = 1;

    @Inject
    DatabaseReference db;
    @Inject
    PreferenceManager preferenceManager;
    private FirebaseRecyclerAdapter adapter;
    @Inject
    CustomerProcessor customerProcessor;

    public QueueFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeniusApp.get(getActivity()).getComponent().inject(this);
        Speech.getInstance();
        customerChanges();
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

            adapter = new QueueAdapter(getActivity(), CustomerModel.class, R.layout.fragment_customer, QueueAdapter.CustomerHolder.class, getCustomersQuery());
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    public Query getCustomersQuery() {
        return db
                .child(preferenceManager.getCode().toLowerCase())
                .limitToFirst(5);
    }

    public void customerChanges() {
        getCustomersQuery().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customerProcessor.process(dataSnapshot);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
