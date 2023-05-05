package com.cursoandroid.olx.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cursoandroid.olx.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CadastroAdapter extends RecyclerView.Adapter<CadastroAdapter.CadastroViewHolder> {

    private final List<Bitmap> imagemAnuncio;
    private OnItemClickListener<Bitmap> itemClickListener;

    public CadastroAdapter(List<Bitmap> imagemAnuncio) {
        this.imagemAnuncio = imagemAnuncio;
    }

    @NonNull
    @NotNull
    @Override
    public CadastroViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new CadastroViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_cadastro,parent,false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull @NotNull CadastroViewHolder holder, int position) {
        Bitmap bitmap = imagemAnuncio.get(position);
        holder.itemView.setOnClickListener(v->itemClickListener.onItemClick(bitmap));
        if (bitmap != null){
            holder.ivSample.setImageBitmap(bitmap);
        }else {
            holder.ivSample.setImageResource(R.drawable.padrao);
        }
        holder.ivClose.setOnClickListener(v->{
            imagemAnuncio.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return imagemAnuncio.size();
    }

    public void setItemClickListener(OnItemClickListener<Bitmap> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    static class CadastroViewHolder extends RecyclerView.ViewHolder{
        ImageView ivSample, ivClose;
        public CadastroViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivSample = itemView.findViewById(R.id.ivSample);
            ivClose = itemView.findViewById(R.id.ivClose);
        }
    }
}
