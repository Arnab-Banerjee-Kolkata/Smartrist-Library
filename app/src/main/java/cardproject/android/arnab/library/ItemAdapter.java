package cardproject.android.arnab.library;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.itemViewHolder> {
    private ArrayList<Item> itemArrayList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public  void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }
    public static class itemViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView1;
        public TextView textView2;
        public itemViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageView=itemView.findViewById(R.id.icon);
            textView1=itemView.findViewById(R.id.text);
            textView2=itemView.findViewById(R.id.text2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION)
                            listener.onItemClick(position);
                    }
                }
            });
        }
    }
    public ItemAdapter(ArrayList<Item> itemlist){
        itemArrayList=itemlist;
    }
    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout,viewGroup,false);
        itemViewHolder ivh=new itemViewHolder(v,mListener);
        return  ivh;
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder itemViewHolder, int i) {
        Item currentItem=itemArrayList.get(i);
        itemViewHolder.imageView.setImageResource(currentItem.getMimageres());
        itemViewHolder.textView1.setText(currentItem.getMtxt1());
        itemViewHolder.textView2.setText(currentItem.getMtxt2());
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }
}
