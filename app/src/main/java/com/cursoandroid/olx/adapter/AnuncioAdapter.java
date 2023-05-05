package com.cursoandroid.olx.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cursoandroid.olx.R;
import com.cursoandroid.olx.model.Anuncio;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder> implements Filterable {

    private final Context context;
    private List<Anuncio> anuncioListFull;
    private List<Anuncio> anuncioList;
    private final Filter anuncioCategoriaFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Anuncio> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(anuncioListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Anuncio anuncio : anuncioListFull) {
                    if (anuncio.getCategoria().toLowerCase().contains(filterPattern)) {
                        filteredList.add(anuncio);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            anuncioList.clear();
            anuncioList.addAll((List<Anuncio>) results.values);
            notifyDataSetChanged();
        }
    };
    private final Filter anuncioEstadoFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Anuncio> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(anuncioListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Anuncio anuncio : anuncioListFull) {
                    if (anuncio.getEstado().toLowerCase().contains(filterPattern)) {
                        filteredList.add(anuncio);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            anuncioList.clear();
            anuncioList.addAll((List<Anuncio>) results.values);
            notifyDataSetChanged();
        }
    };
    private Filter anuncioFilter;
    private OnItemClickListener<Anuncio> onItemClickListener;

    public AnuncioAdapter(Context context, List<Anuncio> anuncioList) {
        this.context = context;
        this.anuncioList = anuncioList;
        this.anuncioListFull = new ArrayList<>(anuncioList);
        onItemClickListener = null;
        anuncioFilter = null;
    }

    @NonNull
    @NotNull
    @Override
    public AnuncioViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new AnuncioViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapater_anuncio, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AnuncioViewHolder holder, int position) {
        Anuncio anuncio = anuncioList.get(position);
        String urlCapa = anuncio.getFotos().get(0);
        holder.tvTitulo.setText(anuncio.getTitulo());
        holder.tvPreco.setText(anuncio.getValor());
        Picasso.get().load(urlCapa).into(holder.ivAnuncio);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(l -> onItemClickListener.onItemClick(anuncio));
        }
    }

    @Override
    public int getItemCount() {
        return anuncioList.size();
    }

    public void setOnItemClickListener(OnItemClickListener<Anuncio> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<Anuncio> getAnuncioList() {
        return anuncioList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAnuncioList(List<Anuncio> anuncioList) {
        this.anuncioList = anuncioList;
        anuncioListFull = new ArrayList<>(anuncioList);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        Anuncio a = anuncioList.remove(position);
        anuncioListFull.remove(a);
        notifyItemRemoved(position);
    }

    public void addItem(int position, Anuncio anuncio) {
        anuncioListFull.add(position, anuncio);
        anuncioList.add(position, anuncio);
        notifyItemInserted(position);
    }

    @Override
    public Filter getFilter() {
        return anuncioFilter;
    }

    public AnuncioAdapter filterAsCategoria() {
        anuncioFilter = anuncioCategoriaFilter;
        return this;
    }

    public AnuncioAdapter filterAsEstado() {
        anuncioFilter = anuncioEstadoFilter;
        return this;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearFilter(){
        anuncioList = new ArrayList<>(anuncioListFull);
        anuncioFilter = null;
        notifyDataSetChanged();
    }

    static class AnuncioViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAnuncio;
        TextView tvTitulo, tvPreco;

        public AnuncioViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivAnuncio = itemView.findViewById(R.id.ivAnuncio);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvPreco = itemView.findViewById(R.id.tvPreco);
        }
    }
}
