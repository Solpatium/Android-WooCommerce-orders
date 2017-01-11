package com.example.kuba.wcorders.fragments;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.kuba.wcorders.R;
import com.example.kuba.wcorders.WcOrders;
import com.example.kuba.wcorders.views.RecyclerListElement;
import com.example.kuba.wcorders.views.PostRecyclerAdapter;
import com.example.kuba.wcorders.woocommerce.data.Order;
import com.example.kuba.wcorders.woocommerce.data.PostId;
import com.example.kuba.wcorders.woocommerce.data.Product;
import com.example.kuba.wcorders.woocommerce.data.ProductOrdered;
import java.util.List;

/*
    Displays order details.
 */
public class OrderDetailFragment extends Fragment {
    private Order order;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        order = WcOrders.getWoocommerce().getOrder(new PostId(getArguments().getInt("id")));

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(order.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.order_detail, container, false);

        // Set total
        ((TextView) view.findViewById(R.id.order_total)).setText(order.getTotal());

        // Set billing and shipping fields
        LinearLayout appendTo = (LinearLayout) view.findViewById(R.id.billing);
        appendView(appendTo);
        appendTo = (LinearLayout) view.findViewById(R.id.shipping);
        appendView(appendTo);

        setupRecycler();

        return view;
    }

    private void appendView(LinearLayout view ) {
        final String fields[] = {"first_name", "last_name", "company", "address_1", "address_2", "city", "state", "postcode", "country"};
        TextView textView;
        for( String field : fields ) {
            if( order.billing.containsKey(field) && !order.billing.get(field).isEmpty()) {
                textView = new TextView(getActivity());
                textView.setText(order.billing.get(field));
                view.addView(textView);
            }
        }
    }

    private void setupRecycler() {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.product_list);
        new PostRecyclerAdapter<ProductOrdered>(recyclerView, order.products, getActivity()) {
            @Override
            public RecyclerListElement<ProductOrdered> onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.product_list_element, parent, false);
                return createElement(view);
            }

            private RecyclerListElement<ProductOrdered> createElement(View elementView) {
                return new RecyclerListElement<ProductOrdered>(elementView) {
                    @Override
                    public void setPost(ProductOrdered product) {
                        ((TextView) view.findViewById(R.id.product_id)).setText(product.id.toString());
                        ((TextView) view.findViewById(R.id.product_name)).setText(product.name);
                        ((TextView) view.findViewById(R.id.product_count)).setText(String.valueOf(product.quantity));
                        ((TextView) view.findViewById(R.id.product_price)).setText(product.getPrice());
                        if( !product.getMeta().isEmpty() ) {
                            setMeta(product.getMeta());
                        }
                    }

                    private void setMeta(List<Product.MetaElement> meta) {
                        LinearLayout productMeta = ((LinearLayout) view.findViewById(R.id.product_meta));
                        productMeta.setVisibility(View.VISIBLE);
                        TextView textView;
                        for(Product.MetaElement metaElement : meta) {
                            textView = new TextView(getContext());
                            textView.setText(metaElement.toSting());
                            productMeta.addView(textView);
                        }
                    }
                };
            }
        };
    }
}
