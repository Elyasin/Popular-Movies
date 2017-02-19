package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.models.Trailer;


public class TrailerAdapter
        extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    private Trailer[] mTrailerArray;

    private Context mContext;

    private TrailerAdapter.TrailerAdapterOnClickHandler mClickHandler;

    /**
     * @param context      - the Activity using the TrailerAdapter
     * @param clickHandler - the Activity implementing the TrailerAdapterOnClickHandler
     */
    public TrailerAdapter(Context context, TrailerAdapter.TrailerAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    /**
     * Interface for onClick method.
     */
    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    /**
     * ViewHolder for MovieAdapter implementing an OnClickListener.
     * When a movie is clicked on the onClick is delegated to the MovieAdapterOnClickHandler.
     *
     * @see MovieAdapter.MovieAdapterOnClickHandler
     */
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTVTrailer;

        /**
         * @param itemView - view holder at which MovieAdapterViewHolder is registered as listener.
         */
        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mTVTrailer = (TextView) itemView.findViewById(R.id.tv_trailer);
            itemView.setOnClickListener(this);
        }

        /**
         * Delegate a click on a movie to the {@link MovieAdapter.MovieAdapterOnClickHandler}
         *
         * @param view - view holding the selected movie.
         */
        @Override
        public void onClick(View view) {
            Trailer trailer = mTrailerArray[getAdapterPosition()];
            mClickHandler.onClick(trailer);
        }
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailerAdapter.TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        holder.mTVTrailer.setText(mTrailerArray[position].getName());
    }

    @Override
    public int getItemCount() {
        if (null == mTrailerArray) return 0;
        return mTrailerArray.length;
    }

    public void setTrailerArray(Trailer[] newTrailerArray) {
        mTrailerArray = newTrailerArray;
        notifyDataSetChanged();
    }
}
