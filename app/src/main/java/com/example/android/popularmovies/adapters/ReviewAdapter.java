/*
  MIT License

  Copyright (c) 2017 Elyasin Shaladi

  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
  associated documentation files (the "Software"), to deal in the Software without restriction,
  including without limitation the rights to use, copy, modify, merge, publish, distribute,
  sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all copies or
  substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
  NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.models.Review;

/**
 * ReviewAdapter holds an array of Review objects.
 * A ReviewAdapter object must have a Context and a ReviewAdapterOnClickHandler.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();


    private Review[] mReviewArray;

    private Context mContext;

    private ReviewAdapterOnClickHandler mClickHandler;

    /**
     * Keeps references to context and listener. Listener are informed in onPreExecute and
     * onPostExecute.
     *
     * @param context      The Activity using the ReviewAdapter
     * @param clickHandler The Activity implementing the ReviewAdapterOnClickHandler
     */
    public ReviewAdapter(Context context, ReviewAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    /**
     * Inflate the review item into the layout.
     *
     * @param parent   The parent's view.
     * @param viewType The type of the new View.
     * @return ReviewAdapterViewHolder with inflated review item.
     */
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_item, parent, false);
        return new ReviewAdapter.ReviewAdapterViewHolder(view);
    }

    /**
     * Display the author and content of the specified position (of the review array).
     *
     * @param holder   ReviewAdapterViewHolder, which has a reference to the TextViews that are to
     *                 be updated.
     * @param position Position of the review (in the Review[] array).
     */
    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review review = mReviewArray[position];
        holder.mAuthor.setText(
                String.format(mContext.getString(R.string.review_author_text), review.getAuthor())
        );
        holder.mContent.setText(review.getContent());
    }

    /**
     * Number of reviews managed in this adapter.
     *
     * @return Total number of reviews in Review array.
     */
    @Override
    public int getItemCount() {
        if (null == mReviewArray) return 0;
        return mReviewArray.length;
    }


    /**
     * Sets new reivew array and notifies of data changes.
     *
     * @param newReviewArray Review array to be set for this ReviewAdapter.
     */
    public void setReviewArray(Review[] newReviewArray) {
        mReviewArray = newReviewArray;
        notifyDataSetChanged();
    }

    /**
     * Interface for onClick(Review) method.
     */
    public interface ReviewAdapterOnClickHandler {

        /**
         * On click on a review view item the corresponding review object can be dealt with here.
         *
         * @param review The review object that was clicked on.
         */
        void onClick(Review review);
    }

    /**
     * ViewHolder for ReviewAdapter implementing an OnClickListener.
     * When a review is clicked on, the onClick is delegated to the ReviewAdapterOnClickHandler.
     *
     * @see ReviewAdapterOnClickHandler
     */
    public class ReviewAdapterViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mAuthor;
        private TextView mContent;

        /**
         * Author/content reference for the review and register as listener to the view holder.
         *
         * @param itemView - view holder at which ReviewAdapterViewHolder is registered as listener.
         */
        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            mContent = (TextView) itemView.findViewById(R.id.tv_review_content);
            itemView.setOnClickListener(this);
        }

        /**
         * Delegate a click on a review to the {@link ReviewAdapterOnClickHandler}
         *
         * @param view - view holding the selected review.
         */
        @Override
        public void onClick(View view) {
            Review review = mReviewArray[getAdapterPosition()];
            mClickHandler.onClick(review);
        }
    }


}
