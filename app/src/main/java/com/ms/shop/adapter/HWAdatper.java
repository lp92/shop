package com.ms.shop.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ms.shop.R;
import com.ms.shop.bean.Wares;
import com.ms.shop.utils.CartProvider;
import com.ms.shop.utils.ToastUtils;

import java.util.List;

public class HWAdatper extends SimpleAdapter<Wares> {


    CartProvider provider ;

    public HWAdatper(Context context, List<Wares> datas) {
        super(context, R.layout.template_hot_wares, datas);

        provider = new CartProvider(context);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, final Wares wares) {
        SimpleDraweeView draweeView = (SimpleDraweeView) viewHolder.getView(R.id.drawee_view);
        draweeView.setImageURI(Uri.parse(wares.getImgUrl()));

        viewHolder.getTextView(R.id.text_title).setText(wares.getName());
        viewHolder.getTextView(R.id.text_price).setText("￥ "+wares.getPrice());

        TextView button =viewHolder.getButton(R.id.btn_add);
        if(button !=null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    provider.put(wares);

                    ToastUtils.show(context, "已添加到购物车");
                }
            });
        }

    }




    public void  resetLayout(int layoutId){


        this.layoutResId  = layoutId;

        notifyItemRangeChanged(0,getDatas().size());


    }



}
