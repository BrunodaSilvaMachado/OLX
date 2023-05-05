package com.cursoandroid.olx.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blackcat.currencyedittext.CurrencyEditText;
import com.cursoandroid.olx.R;
import com.cursoandroid.olx.activity.ui.CadastrarAnuncioViewModel;
import com.cursoandroid.olx.adapter.CadastroAdapter;
import com.cursoandroid.olx.config.FirebaseConfig;
import com.cursoandroid.olx.helper.ImageCapture;
import com.cursoandroid.olx.helper.Permissoes;
import com.cursoandroid.olx.dialog.ProgressBarDialog;
import com.cursoandroid.olx.helper.UploadPhoto;
import com.cursoandroid.olx.model.Anuncio;
import com.cursoandroid.olx.service.AnuncioService;
import com.google.firebase.storage.StorageReference;
import com.santalu.maskedittext.MaskEditText;

import java.util.*;

public class CadastrarAnuncioActivity extends AppCompatActivity {
    private final List<Bitmap> fotoList = new ArrayList<>();
    private final AnuncioService anuncioService = new AnuncioService();
    private Spinner spinnerEstado, spinnerCategoria;
    private EditText etTitulo, etDescricao;
    private MaskEditText etTelefone;
    private CurrencyEditText etValor;
    private RecyclerView recyclerView;
    private CadastroAdapter adapter;
    private final ActivityResultLauncher<Intent> imageFromCameraLauncher = ImageCapture.registerCameraForActivityResult(this, this::setIvCadastro);
    private final ActivityResultLauncher<String> imageFromGalleryLaucher = ImageCapture.registerGalleryForActivityResult(this, this::setIvCadastro);
    private Button btnCadastrar;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);
        inicializarComponentes();
        inicializarPermissoes();
        configurarRecyclerView();
        carregarSpinner();
        inicializarFormulario();

    }

    private void inicializarComponentes() {
        etTitulo = findViewById(R.id.etTitle);
        etDescricao = findViewById(R.id.etDescription);
        etValor = findViewById(R.id.etValue);
        etTelefone = findViewById(R.id.etPhoneNumber);
        recyclerView = findViewById(R.id.recyclerView);
        ImageView ivCamera = findViewById(R.id.ivCamera);
        ImageView ivGallery = findViewById(R.id.ivGallery);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerEstado = findViewById(R.id.spinnerEstados);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        etValor.setLocale(Locale.getDefault());
        ivGallery.setOnClickListener(v -> imageFromGalleryLaucher.launch("image/*"));
        ivCamera.setOnClickListener(v -> imageFromCameraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)));
        dialog = ProgressBarDialog.newDialog(this, getString(R.string.save));
    }

    private void inicializarPermissoes() {
        if (Build.VERSION.SDK_INT < 23)
            return;
        String[] permissoes = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        Permissoes.validarPermissoes(this).launch(permissoes);
    }

    public void salvarAnuncio(View v) {
        final StorageReference storageReference = FirebaseConfig.getStorageReference()
                .child("imagens").child("anuncios");
        final Anuncio anuncio = recupererarAnuncio();
        anuncio.setId(anuncioService.gerarUID());
        final List<String> urlFotosList = new ArrayList<>();
        final int FOTO_LIST_SIZE = fotoList.size();
        dialog.show();
        //Salvar imagem no firebase
        for (Bitmap foto : fotoList) {
            StorageReference imageRef = storageReference.child(anuncio.getId()).child(UUID.randomUUID().toString());
            UploadPhoto.upload(this, imageRef,
                    foto, task -> {
                        urlFotosList.add(task.getResult().toString());
                        if (urlFotosList.size() == FOTO_LIST_SIZE){
                            anuncio.setFotos(urlFotosList);
                            anuncioService.save(anuncio);
                            dialog.dismiss();
                            finish();
                        }
                    });
        }
    }

    @NonNull
    private Anuncio recupererarAnuncio() {
        String estado = spinnerEstado.getSelectedItem().toString();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String titulo = etTitulo.getText().toString();
        String valor = etValor.getText().toString();
        String tel = Objects.requireNonNull(etTelefone.getText()).toString();
        String descricao = etDescricao.getText().toString();

        return new Anuncio(estado, categoria, titulo, valor, tel, descricao);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setIvCadastro(Bitmap bitmap) {
        if (bitmap != null) {
            fotoList.add(bitmap);
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void configurarRecyclerView() {
        adapter = new CadastroAdapter(fotoList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void carregarSpinner() {
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> estadosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);
        estadosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(estadosAdapter);
    }

    private void formDataChange(@NonNull CadastrarAnuncioViewModel casViewModel) {
        Anuncio anuncio = recupererarAnuncio();
        casViewModel.casDataChanged(anuncio.getEstado(), anuncio.getCategoria(), anuncio.getTitulo(),
                String.valueOf(etValor.getRawValue()), anuncio.getTelefone(), anuncio.getDescricao(), fotoList.size());
    }

    private void inicializarFormulario() {
        final CadastrarAnuncioViewModel casViewModel = new ViewModelProvider(this, new CadastrarAnuncioViewModel.CadastrarAnuncioViewModelFactory())
                .get(CadastrarAnuncioViewModel.class);
        casViewModel.getCasLiveData().observe(this, cas -> {
            if (cas == null) {
                return;
            }
            btnCadastrar.setEnabled(cas.isDataValid());
            if (cas.getNumPhotoError() != null) {
                Toast.makeText(this, cas.getNumPhotoError(), Toast.LENGTH_SHORT).show();
            }
            if (cas.getCategoriasError() != null) {
                Toast.makeText(this, cas.getCategoriasError(), Toast.LENGTH_SHORT).show();
            }
            if (cas.getEstadoError() != null) {
                Toast.makeText(this, cas.getEstadoError(), Toast.LENGTH_SHORT).show();
            }
            if (cas.getDescriptionError() != null) {
                etDescricao.setError(getString(cas.getDescriptionError()));
            }
            if (cas.getTelError() != null) {
                etTelefone.setError(getString(cas.getTelError()));
            }
            if (cas.getTitleError() != null) {
                etTitulo.setError(getString(cas.getTitleError()));
            }
            if (cas.getValuesError() != null) {
                etValor.setError(getString(cas.getValuesError()));
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                formDataChange(casViewModel);
            }
        };

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                formDataChange(casViewModel);
            }
        });
        etValor.addTextChangedListener(afterTextChangedListener);
        etTitulo.addTextChangedListener(afterTextChangedListener);
        etTelefone.addTextChangedListener(afterTextChangedListener);
        etDescricao.addTextChangedListener(afterTextChangedListener);
    }
}