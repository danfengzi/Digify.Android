package digify.tv.ui.fragments;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import digify.tv.R;
import digify.tv.api.models.CustomerModel;


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

        viewHolder.name.setText(model.getFirstName()+" "+model.getLastName());
        viewHolder.position.setText(String.valueOf(position+1)+".");


        if(model.getServing())
        {
            viewHolder.background.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.background.setVisibility(View.INVISIBLE);
        }
    }


    public class CustomerHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView position;
        public final TextView name;
        public final View background;

        public CustomerHolder(View view) {
            super(view);
            mView = view;
            position = (TextView) view.findViewById(R.id.position);
            name = (TextView) view.findViewById(R.id.name);
            background = view.findViewById(R.id.background);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }
}
