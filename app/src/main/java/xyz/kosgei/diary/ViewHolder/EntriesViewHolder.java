package xyz.kosgei.diary.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import xyz.kosgei.diary.Interface.ItemOnClick;
import xyz.kosgei.diary.R;


public class EntriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView Date;
    public TextView Title;


    private ItemOnClick itemOnClick;

    public EntriesViewHolder(View itemView) {
        super(itemView);
        Date = itemView.findViewById(R.id.date);
        Title = itemView.findViewById(R.id.title);

        itemView.setOnClickListener(this);


    }

    public void setItemOnClick(ItemOnClick itemOnClick) {
        this.itemOnClick = itemOnClick;
    }

    @Override
    public void onClick(View v) {
        itemOnClick.onClick(v,getAdapterPosition(),false);

    }
}

