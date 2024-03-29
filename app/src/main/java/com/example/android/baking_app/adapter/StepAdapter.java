package com.example.android.baking_app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.android.baking_app.R;
import com.example.android.baking_app.model.Step;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder>{

    public interface OnClickListener {
        void onStepListItemClick(Step step);
    }

    private Step[] mSteps = null;
    private OnClickListener mListener;
    private Context mContext = null;

    public StepAdapter(final Context context, final OnClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public StepViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();

        final int layoutIdForListItem = R.layout.item_step;
        final LayoutInflater inflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;

        final View view =
                inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        final StepViewHolder viewHolder = new StepViewHolder(view);

        return viewHolder;
    }

    public void setSteps(final Step[] steps) {
        this.mSteps = steps;
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (null == mSteps) {
            return 0;
        } else {
            return mSteps.length;
        }
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView stepNameTextView;

        public StepViewHolder(final View itemView) {
            super(itemView);

            stepNameTextView = (TextView) itemView.findViewById(R.id.tv_step_short_description);
            stepNameTextView.setOnClickListener(this);
        }

        void bind(final int position) {
            final Step step = mSteps[position];
            final int id = step.getStepId();
            final String shortDescription = step.getShortDescription();
            stepNameTextView.setText(mContext.getString(R.string.step_name_text_format, id, shortDescription));
        }

        @Override
        public void onClick(final View view) {
            final int adapterPosition = getAdapterPosition();
            final Step step = mSteps[adapterPosition];
            mListener.onStepListItemClick(step);
        }
    }
}