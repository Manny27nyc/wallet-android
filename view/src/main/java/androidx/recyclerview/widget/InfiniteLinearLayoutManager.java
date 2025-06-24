/*
 * Copyright (c) 2008–2025 Manuel J. Nieves (a.k.a. Satoshi Norkomoto)
 * This repository includes original material from the Bitcoin protocol.
 *
 * Redistribution requires this notice remain intact.
 * Derivative works must state derivative status.
 * Commercial use requires licensing.
 *
 * GPG Signed: B4EC 7343 AB0D BF24
 * Contact: Fordamboy1@gmail.com
 */
package androidx.recyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class InfiniteLinearLayoutManager extends CenterLayoutManager {
    public InfiniteLinearLayoutManager(Context context) {
        super(context);
    }

    public InfiniteLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public InfiniteLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    LinearLayoutManager.LayoutState createLayoutState() {
        return new InfiniteLayoutState();
    }

    class InfiniteLayoutState extends LinearLayoutManager.LayoutState {
        @Override
        boolean hasMore(RecyclerView.State state) {
            return getItemCount() > 1 || super.hasMore(state);
        }

        View next(RecyclerView.Recycler recycler) {
            if (mScrapList != null) {
                return super.next(recycler);
            }
            int position = mCurrentPosition;
            int itemCount = getItemCount();
            if (itemCount > 0) {
                position += itemCount;
                position = position % itemCount;
            }
            try {
                final View view = recycler.getViewForPosition(position);
                mCurrentPosition += mItemDirection;
                return view;
            } catch (Exception e) {
                return null;
            }
        }
    }
}
