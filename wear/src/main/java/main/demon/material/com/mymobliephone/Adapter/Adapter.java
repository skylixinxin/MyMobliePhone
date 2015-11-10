package main.demon.material.com.mymobliephone.Adapter;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import main.demon.material.com.mymobliephone.R;

/**
 * Created by lixinxin on 15/11/9.
 */
public class Adapter extends WearableListView.Adapter{

    private String[] mDataset;
    private final Context mContext;
    private final LayoutInflater mInflater;

    public Adapter(Context context,String[] dataset) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataset = dataset;
    }

    public void setDataset(String[] dataset){
        this.mDataset = dataset;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(mInflater.inflate(R.layout.list_item_layout,null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
        TextView view = itemViewHolder.textView;
        view.setText(mDataset[position]);
        ((ItemViewHolder) holder).itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public static class ItemViewHolder extends WearableListView.ViewHolder{
        private TextView textView;
        public ItemViewHolder(View itemView) {
            super(itemView);
// find the text view within the custom item's layout
            textView = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
