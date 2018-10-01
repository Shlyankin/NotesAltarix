package com.example.user.notesaltarix.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.example.user.notesaltarix.ImageViewerActivity;
import com.example.user.notesaltarix.NoteActivity;
import com.example.user.notesaltarix.R;

import java.util.List;

/**
 * Адаптер Recycler View для класса Note. Реализован стандартные методы адаптера для Recycler View.
 * @author Николай Шлянкин
 * @version 1
 */
public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.NoteViewHolder> {

    private List<Note> notes;
    private Context context;

    /**Конструктор для адаптера
     * @param notes ссылка на структуру, где хранятся заметки
     * @param context context из которого вызывается
     */
    public NoteRecyclerAdapter(List<Note> notes, Context context) {
        this.notes = notes;
        this.context = context;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        NoteViewHolder noteViewHolder = new NoteViewHolder(v);
        return noteViewHolder;
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, final int position) {
        String uri = notes.get(position).getUri();
        if (uri != "")
        {
            Glide.with(context)
                    .load(uri)
                    .into(holder.imageView);

        } else {
            holder.imageView.setImageDrawable(null);
        }
        holder.tittleView.setText(notes.get(position).getTittle());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NoteActivity.class);
                intent.putExtra("id", notes.get(position).getId());
                context.startActivity(intent);
            }
        });
        switch (notes.get(position).getImportant()) {
            case 1:
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.green));
                break;
            case 2:
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.yellow));
                break;
            case 3:
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.red));
                break;
            default:
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                break;
        }
    }

    /**@return количество элементов в адаптере*/
    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView tittleView;
        ImageView imageView;


        public NoteViewHolder(View itemView) {
            super(itemView);
            tittleView = (TextView)itemView.findViewById(R.id.tittleViewItem);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            imageView = (ImageView)itemView.findViewById(R.id.imageView0);
        }
    }
}
