package com.cursoandroid.olx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cursoandroid.olx.R;
import com.cursoandroid.olx.adapter.AnuncioAdapter;
import com.cursoandroid.olx.config.FirebaseConfig;
import com.cursoandroid.olx.dialog.ItemFilterDialog;
import com.cursoandroid.olx.dialog.ProgressBarDialog;
import com.cursoandroid.olx.model.Anuncio;
import com.cursoandroid.olx.service.AnuncioService;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AnunciosActivity extends AppCompatActivity {

    private final AnuncioService anuncioService = new AnuncioService();
    private final FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    RecyclerView rvAnunciosPublicos;
    Button btnRegion, btnCategory;
    AlertDialog dialog;
    private AnuncioAdapter anuncioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);
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

    @Override
    public boolean onCreateOptionsMenu(@NonNull @NotNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_anuncios, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_acesso) {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        } else if (itemId == R.id.menu_sair) {
            auth.signOut();
            anuncioService.removeUidReference();
            invalidateOptionsMenu();
        } else if (itemId == R.id.menu_anuncios) {
            startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        boolean isSignIn = auth.getCurrentUser() != null;
        menu.setGroupVisible(R.id.group_sign_in, isSignIn);
        menu.setGroupVisible(R.id.group_sign_out, !isSignIn);

        return super.onPrepareOptionsMenu(menu);
    }

    private void inicializarComponentes() {
        rvAnunciosPublicos = findViewById(R.id.rvAnunciosPublicos);
        btnCategory = findViewById(R.id.btnCategory);
        btnRegion = findViewById(R.id.btnRegion);
        dialog = ProgressBarDialog.newDialog(this, getString(R.string.load));
        anuncioAdapter = new AnuncioAdapter(this, new ArrayList<>());
        anuncioAdapter.setOnItemClickListener(anuncio->{
            Intent i = new Intent(this, DetalhesProdutoActivity.class);
            i.putExtra("ANUNCIO_SELECIONADO",anuncio);
            startActivity(i);
        });
        rvAnunciosPublicos.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setReverseLayout(true);
        linearLayout.setStackFromEnd(true);
        rvAnunciosPublicos.setLayoutManager(linearLayout);
        rvAnunciosPublicos.setAdapter(anuncioAdapter);
    }

    public void estadoFilter(View view) {
        String[] estados = getResources().getStringArray(R.array.estados);
        AlertDialog dialog = ItemFilterDialog.newDialog(this, getString(R.string.region), estados,
                estado -> anuncioAdapter.filterAsEstado().getFilter().filter(estado));
        dialog.show();
        clearFilterRecyclerView();
    }

    public void categoriaFilter(View view) {
        String[] categorias = getResources().getStringArray(R.array.categorias);
        AlertDialog dialog = ItemFilterDialog.newDialog(this, getString(R.string.category), categorias,
                categoria -> anuncioAdapter.filterAsCategoria().getFilter().filter(categoria));
        dialog.show();
        clearFilterRecyclerView();
    }

    private void clearFilterRecyclerView() {
        Snackbar.make(rvAnunciosPublicos, R.string.applied_filter, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.clear, v -> anuncioAdapter.clearFilter()).show();
    }

    private void recuperarAnuncios() {
        dialog.show();
        anuncioService.anunciosEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                List<Anuncio> anuncioList = new ArrayList<>();
                for (DataSnapshot estados : snapshot.getChildren()) {
                    for (DataSnapshot categorias : estados.getChildren()) {
                        for (DataSnapshot a : categorias.getChildren()) {
                            Anuncio anuncio = a.getValue(Anuncio.class);
                            anuncioList.add(anuncio);
                        }
                    }
                }
                anuncioAdapter.setAnuncioList(anuncioList);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
}