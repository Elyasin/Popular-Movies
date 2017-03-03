/**
 * MIT License
 * <p>
 * Copyright (c) 2017 Elyasin Shaladi
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.models.Trailer;

/**
 * TrailerAdapter holds an array of Trailer objects.
 * A TrailerAdapter object must have a Context and a TrailerAdapterOnClickHandler.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();


    private Trailer[] mTrailerArray;

    private Context mContext;

    private TrailerAdapter.TrailerAdapterOnClickHandler mClickHandler;

    /**
     * Keeps references to context and listener. Listener are informed in onPreExecute and
     * onPostExecute.
     *
     * @param context      The Activity using the TrailerAdapter
     * @param clickHandler The Activity implementing the TrailerAdapterOnClickHandler
     */
    public TrailerAdapter(Context context, TrailerAdapter.TrailerAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    /**
     * Inflate the trailer item into the layout.
     *
     * @param parent   The parent's view.
     * @param viewType The type of the new View.
     * @return TrailerAdapterViewHolder with inflated trailer item.
     */
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailerAdapter.TrailerAdapterViewHolder(view);
    }

    /**
     * Display the trailer of the specified position (of the trailer array).
     *
     * @param holder   TrailerAdapterViewHolder, which has a reference to the TextView that is to
     *                 be updated.
     * @param position Position of the trailer (in the Trailer[] array).
     */
    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        holder.mTVTrailer.setText(mTrailerArray[position].getName());
    }

    /**
     * Number of trailers managed in this adapter.
     *
     * @return Total number of trailers in Trailer array.
     */
    @Override
    public int getItemCount() {
        if (null == mTrailerArray) return 0;
        return mTrailerArray.length;
    }

    /**
     * Sets new trailer array and notifies of data changes.
     *
     * @param newTrailerArray Trailer array to be set for this TrailerAdapter.
     */
    public void setTrailerArray(Trailer[] newTrailerArray) {
        mTrailerArray = newTrailerArray;
        notifyDataSetChanged();
    }

    /**
     * Interface for onClick(Trailer) method.
     */
    public interface TrailerAdapterOnClickHandler {

        /**
         * On click on a trailer view item the corresponding trailer object can be dealt with here.
         *
         * @param trailer The trailer object that was clicked on.
         */
        void onClick(Trailer trailer);
    }

    /**
     * ViewHolder for TrailerAdapter implementing an OnClickListener.
     * When a trailer is clicked on the onClick is delegated to the TrailerAdapterOnClickHandler.
     *
     * @see TrailerAdapterOnClickHandler
     */
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTVTrailer;

        /**
         * Trailer reference for the trailer and register as listener to the view holder.
         *
         * @param itemView - view holder at which TrailerAdapterViewHolder is registered as listener.
         */
        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mTVTrailer = (TextView) itemView.findViewById(R.id.tv_trailer);
            itemView.setOnClickListener(this);
        }

        /**
         * Delegate a click on a trailer to the {@link TrailerAdapterOnClickHandler}
         *
         * @param view - view holding the selected trailer.
         */
        @Override
        public void onClick(View view) {
            Trailer trailer = mTrailerArray[getAdapterPosition()];
            mClickHandler.onClick(trailer);
        }
    }
}
