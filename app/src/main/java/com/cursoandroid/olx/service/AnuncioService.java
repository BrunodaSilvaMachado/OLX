package com.cursoandroid.olx.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cursoandroid.olx.config.FirebaseConfig;
import com.cursoandroid.olx.model.Anuncio;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

public class AnuncioService extends DatabaseService<Anuncio> {
    private static DatabaseReference baseRef = null;
    private static DatabaseReference uidRef = null;
    private static DatabaseReference anuncioRef = null;
    private ValueEventListener eventListener = null;

    public AnuncioService(){
        if (anuncioRef == null) {
            anuncioRef = FirebaseConfig.getDatabaseReference().child("anuncios");
        }
    }
    @NonNull
    @NotNull
    @Override
    protected DatabaseReference newReference() {
        if (baseRef == null) {
            baseRef = FirebaseConfig.getDatabaseReference().child("meus_anuncios");
        }
        return baseRef;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    protected DatabaseReference newUidReference() {
        String authId = getCurrenteAuthId();
        if (uidRef == null && authId != null){
            uidRef = FirebaseConfig.getDatabaseReference().child("meus_anuncios").child(authId);
        }
        return uidRef;
    }

    @Override
    public void finish() {
        baseRef = null;
        uidRef = null;
        anuncioRef = null;
    }

    @Override
    public void start() {
        super.start();
        if (anuncioRef != null && eventListener != null){
            anuncioRef.addValueEventListener(eventListener);
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (anuncioRef != null && eventListener != null){
            anuncioRef.removeEventListener(eventListener);
        }
    }

    public String gerarUID(){
        return baseRef.push().getKey();
    }

    public void removeUidReference(){uidRef = null;}

    public void save(@NonNull @NotNull Anuncio anuncio) {
        super.save(uidRef, anuncio);
        anuncioRef.child(anuncio.getEstado()).child(anuncio.getCategoria()).child(anuncio.getId()).setValue(anuncio);
    }

    public void delete(@NonNull Anuncio anuncio){
        super.delete(uidRef, anuncio);
        anuncioRef.child(anuncio.getEstado()).child(anuncio.getCategoria()).child(anuncio.getId()).removeValue();
    }

    public void anunciosEventListener(ValueEventListener eventListener){
        this.eventListener = eventListener;
    }
}
