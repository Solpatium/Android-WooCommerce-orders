package com.example.kuba.wcorders.views;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.kuba.wcorders.R;
import com.example.kuba.wcorders.activities.OrderListActivity;
import com.example.kuba.wcorders.woocommerce.data.Order;
import com.example.kuba.wcorders.woocommerce.data.PostCollection;


/*
    RecyclerView recycler for Orders.
 */
public class OrderRecyclerAdapter extends PostRecyclerAdapter<Order> {
    private boolean displayedDefault = false;
    private RecyclerListElement<Order> activeElement = null;


    public OrderRecyclerAdapter(RecyclerView recyclerView, PostCollection<Order> posts, Activity activity) {
        super(recyclerView, posts, activity);
    }

    @Override
    public RecyclerListElement<Order> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_element, parent, false);
        return createElement(view);
    }

    private RecyclerListElement<Order> createElement(View view) {
        return new RecyclerListElement<Order>(view) {
            @Override
            public void setPost( Order order ) {
                ((TextView)view.findViewById(R.id.orderid)).setText(order.getIdString());
                ((TextView)view.findViewById(R.id.customer)).setText(order.getCustomerFullname());
                setStatus(order.status);
            }

            private void setStatus(Order.Status status) {
                int drawableStatus;
                switch (status) {
                    case pending: drawableStatus = R.drawable.pending; break;
                    case completed: drawableStatus = R.drawable.completed; break;
                    case on_hold: drawableStatus = R.drawable.onhold; break;
                    default: drawableStatus = R.drawable.any;
                }
                ((ImageView)view.findViewById(R.id.status)).setImageResource(drawableStatus);
            }

        };
    }

    private void setActive( RecyclerListElement<Order> element) {
        if( activeElement != null ) {
            activeElement.view.setAlpha(1);
            activeElement.view.invalidate();
        }
        activeElement = element;
        activeElement.view.setAlpha((float)0.5);
        activeElement.view.invalidate();
    }

    @Override
    protected void setupElement( RecyclerListElement<Order> element, int position, Order post ) {
        super.setupElement(element, position, post);

        OrderListActivity parentActivity = (OrderListActivity) super.parentActivity;

        // CLICK LISTENER
        element.view.setOnClickListener((s -> {
                setActive(element);
                parentActivity.openOrderDetail( post );
            })
        );

        // Animate new elements
        setAnimation(element.view, post);

        // If it is the first element displayed in two paned view display it's details
        if( position == 0 && parentActivity.isTwoPane() && !displayedDefault ) {
            setActive(element);
            displayedDefault = true;
            parentActivity.openOrderDetail(post);
        }
    }

    private void setAnimation(View viewToAnimate, Order order)
    {
        Animation animation = AnimationUtils.loadAnimation(parentActivity, android.R.anim.fade_in);
        viewToAnimate.startAnimation(animation);
    }
}
