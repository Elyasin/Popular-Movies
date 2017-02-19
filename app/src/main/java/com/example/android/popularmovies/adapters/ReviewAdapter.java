package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.models.Review;

/**
 * Created by Elyas on 19/02/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();


    private Review[] mReviewArray;

    private Context mContext;

    private ReviewAdapter.ReviewAdapterOnClickHandler mClickHandler;

    /**
     * @param context      - the Activity using the ReviewAdapter
     * @param clickHandler - the Activity implementing the ReviewAdapterOnClickHandler
     */
    public ReviewAdapter(Context context, ReviewAdapter.ReviewAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_item, parent, false);
        return new ReviewAdapter.ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review review = mReviewArray[position];
        holder.mAuthor.setText(
                String.format(mContext.getString(R.string.review_author_text), review.getAuthor())
        );
        holder.mContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (null == mReviewArray) return 0;
        return mReviewArray.length;
    }

    public void setReviewArray(Review[] newReviewArray) {
        mReviewArray = newReviewArray;
        notifyDataSetChanged();
    }

    /**
     * Interface for onClick method.
     */
    public interface ReviewAdapterOnClickHandler {
        void onClick(Review review);
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mAuthor;
        private TextView mContent;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            //mAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            //mContent = (TextView) itemView.findViewById(R.id.tv_review_content);
            mAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            mContent = (TextView) itemView.findViewById(R.id.tv_review_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(LOG_TAG, ": onClick clicked");
        }
    }


}
