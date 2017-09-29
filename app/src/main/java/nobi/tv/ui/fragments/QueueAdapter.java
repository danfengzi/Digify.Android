package nobi.tv.ui.fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import nobi.tv.R;
import nobi.tv.api.models.CustomerModel;


public class QueueAdapter extends FirebaseRecyclerAdapter<CustomerModel, QueueAdapter.CustomerHolder> {

    private Activity activity;

    public QueueAdapter(Activity activity, Class modelClass, int modelLayout, Class viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.activity = activity;
    }


    @Override
    public CustomerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_customer, parent, false);
        return new CustomerHolder(view);
    }

    @Override
    protected void populateViewHolder(CustomerHolder viewHolder, CustomerModel model, int position) {
        String name = String.valueOf(position + 1) + "." + model.getFirstName() + " " + model.getLastName();

        if (model.getServing()) {
            viewHolder.name.setVisibility(View.GONE);
            viewHolder.nameServing.setText(name);
            viewHolder.nameServing.setTypeface(null, Typeface.BOLD);
            viewHolder.serving.setVisibility(View.VISIBLE);
        } else {
            viewHolder.name.setVisibility(View.VISIBLE);
            viewHolder.name.setTypeface(null, Typeface.BOLD);
            viewHolder.name.setText(name);
            viewHolder.serving.setVisibility(View.GONE);
        }
    }


    public class CustomerHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView nameServing;
        public final TextView name;
        public final View serving;

        public CustomerHolder(View view) {
            super(view);
            mView = view;
            name = (TextView) view.findViewById(R.id.name);
            nameServing = (TextView) view.findViewById(R.id.name_serving);
            serving = view.findViewById(R.id.serving_layout);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }
}
