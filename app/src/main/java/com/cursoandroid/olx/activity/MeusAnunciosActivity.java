package com.cursoandroid.olx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cursoandroid.olx.R;
import com.cursoandroid.olx.adapter.AnuncioAdapter;
import com.cursoandroid.olx.dialog.ProgressBarDialog;
import com.cursoandroid.olx.model.Anuncio;
import com.cursoandroid.olx.service.AnuncioService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MeusAnunciosActivity extends AppCompatActivity {
    private final AnuncioService anuncioService = new AnuncioService();
    private AlertDialog dialog;
    private AnuncioAdapter anuncioAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.app_name);
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(l->startActivity(new Intent(getApplicationContext(),CadastrarAnuncioActivity.class)));

        inicializarComponentes();

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarAnuncios();
        anuncioService.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        anuncioService.stop();
    }

    private void inicializarComponentes(){
        dialog = ProgressBarDialog.newDialog(this,getString(R.string.load));
        anuncioAdapter = new AnuncioAdapter(this, new ArrayList<>());
        anuncioAdapter.setOnItemClickListener(anuncio->{
            Intent i = new Intent(this, DetalhesProdutoActivity.class);
            i.putExtra("ANUNCIO_SELECIONADO",anuncio);
            i.putExtra("CALL_PHONE", View.INVISIBLE);
            startActivity(i);
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setReverseLayout(true);
        linearLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayout);
        recyclerView.setAdapter(anuncioAdapter);
        swipe(recyclerView);
    }

    private void recuperarAnuncios(){
        dialog.show();
        anuncioService.setUidEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                List<Anuncio> anuncios = new ArrayList<>();
                for(DataSnapshot ds: snapshot.getChildren()){
                    anuncios.add(ds.getValue(Anuncio.class));
                }
                anuncioAdapter.setAnuncioList(anuncios);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }

    private void swipe(RecyclerView recyclerView) {
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            boolean isRemoved;
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                Anuncio anuncio = anuncioAdapter.getAnuncioList().get(position);
                anuncioAdapter.removeItem(position);
                isRemoved = true;
                Snackbar.make(recyclerView, R.string.ads_has_been_deleted,Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo,l-> {
                            anuncioAdapter.addItem(position, anuncio);
                            isRemoved = false;
                        }).show();

                new Handler(Looper.getMainLooper()).postDelayed(() ->{
                    if(isRemoved) anuncioService.delete(anuncio);
                } , 3000);

            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }
}