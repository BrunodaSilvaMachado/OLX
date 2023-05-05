package com.cursoandroid.olx.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.cursoandroid.olx.R;
import com.cursoandroid.olx.helper.Permissoes;
import com.cursoandroid.olx.model.Anuncio;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private Anuncio anuncio = new Anuncio();
    private int callPhoneVisible = View.VISIBLE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_activity_detalhes_produto);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        }
        if(getIntent() != null){
            Bundle bundle = getIntent().getExtras();
            if (bundle != null){
                anuncio =  bundle.getParcelable("ANUNCIO_SELECIONADO");
                callPhoneVisible = bundle.getInt("CALL_PHONE", View.VISIBLE);
            }
        }

        inicializarComponentes();
        if (Build.VERSION.SDK_INT >= 23){
            Permissoes.validarPermissoes(this).launch(new String[]{Manifest.permission.CALL_PHONE});
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void inicializarComponentes(){
        TextView titulo = findViewById(R.id.tvTituloDetalhe);
        TextView preco = findViewById(R.id.tvPrecoDetalhe);
        TextView regiao = findViewById(R.id.tvRegiaoDetalhe);
        TextView descricao = findViewById(R.id.tvDescricaoDetalhe);
        Button callPhone = findViewById(R.id.btnCallPhone);
        CarouselView carouselView = findViewById(R.id.carouselView);

        titulo.setText(anuncio.getTitulo());
        preco.setText(anuncio.getValor());
        regiao.setText(anuncio.getEstado());
        descricao.setText(anuncio.getDescricao());
        callPhone.setVisibility(callPhoneVisible);
        callPhone.setOnClickListener(l->startActivity(new Intent(Intent.ACTION_DIAL,
                Uri.fromParts("tel",anuncio.getTelefone(),null))));
        carouselView.setPageCount(anuncio.getFotos().size());
        carouselView.setImageListener((position, imageView) -> Picasso.get()
                .load(anuncio.getFotos().get(position)).into(imageView));
    }
}