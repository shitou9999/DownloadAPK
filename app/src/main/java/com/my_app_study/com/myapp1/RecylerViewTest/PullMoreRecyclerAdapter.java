package com.my_app_study.com.myapp1.RecylerViewTest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.my_app_study.com.myapp1.R;

import java.util.List;

/**
 * Created by Administrator on 2016/7/4.
 */
public class PullMoreRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_NORMAL_ITEM=0;
    private static final int TYPE_FOOTER_ITEM=1;
    //上拉加载更多
    public static final int PULLUP_LOAD_MORE=1;
    //正在加载
    public static final int LOADING_MORE=2;
    //默认为0
    private int load_more_status=0;
    public List<CardInfo> list;
    private OnItemClickListener mClickListener;

    private interface OnItemClickListener{
        void onItemClick(View itemView, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    public PullMoreRecyclerAdapter(List<CardInfo> list) {
        this.list = list;
    }
    @Override //创建新View，被LayoutManager所调用
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         //如果viewType是普通item返回普通的布局，否则是底部布局并返回
        if(viewType==TYPE_NORMAL_ITEM){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recylerview_item_01,parent,false);
            final NormalItmeViewHolder vh=new NormalItmeViewHolder(view);
            if(mClickListener!=null){
                vh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClickListener.onItemClick(vh.itemView,vh.getLayoutPosition());
                    }
                });
            }
            return vh;
        }else{
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_footer_view,parent,false);
            FooterViewHolder footerViewHolder=new FooterViewHolder(view);
            return footerViewHolder;
        }
    }

    @Override  //将数据与界面进行绑定的操作
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NormalItmeViewHolder){
            ((NormalItmeViewHolder) holder).titleTv.setText(list.get(position).getTitle());
            ((NormalItmeViewHolder) holder).contentTv.setText(list.get(position).getContent());
        }else if(holder instanceof FooterViewHolder){
            FooterViewHolder footerViewHolder= (FooterViewHolder) holder;
            switch (load_more_status){
                case PULLUP_LOAD_MORE:
                    footerViewHolder.foot_view_item_tv.setText("上拉加载更多");
                    footerViewHolder.foot_view_item_tv.setVisibility(View.VISIBLE);
                    footerViewHolder.pb.setVisibility(View.GONE);
                    break;
                case LOADING_MORE:
                    footerViewHolder.foot_view_item_tv.setVisibility(View.GONE);
                    footerViewHolder.pb.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }


//    自定义的ViewHolder，持有每个Item的的所有界面元素
     public static class NormalItmeViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTv, contentTv;
        public ImageView iv;
        public NormalItmeViewHolder(View itemView) {
           super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.item_title_tv);
            contentTv = (TextView) itemView.findViewById(R.id.item_content_tv);
            iv = (ImageView) itemView.findViewById(R.id.item_iv);
        }
     }

    public static class FooterViewHolder extends RecyclerView.ViewHolder{
        public TextView foot_view_item_tv;
        public ProgressBar pb;

        public FooterViewHolder(View view) {
            super(view);
            pb = (ProgressBar) view.findViewById(R.id.progress_view);
            foot_view_item_tv = (TextView) view.findViewById(R.id.tv_content);
        }
    }


    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position+1==getItemCount()){
            return TYPE_FOOTER_ITEM;
        }else{
            return TYPE_NORMAL_ITEM;
        }
    }

    public void setMoreStatus(int status){
        load_more_status=status;
        notifyDataSetChanged();
    }


}
