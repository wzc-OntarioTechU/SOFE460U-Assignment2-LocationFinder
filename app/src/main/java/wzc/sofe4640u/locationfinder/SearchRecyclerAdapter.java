package wzc.sofe4640u.locationfinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.SearchViewHolder> {
    private List<String> items = new ArrayList<>();

    public SearchRecyclerAdapter(List<String> items) {
        this.items = items;
    }

    public void updateItems(List<String> items) {
        items.clear();
        Collections.copy(this.items, items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchRecyclerAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecyclerAdapter.SearchViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        private TextView itemText;

        public SearchViewHolder(View itemView) {
            super(itemView);
            itemText = itemView.findViewById(android.R.id.text1);
        }

        public void bind (String value) {
            itemText.setText(value);
        }
    }
}
