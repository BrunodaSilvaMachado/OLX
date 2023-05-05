package com.cursoandroid.olx.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.cursoandroid.olx.service.DatabaseService;
import com.google.firebase.database.Exclude;

public class Usuario implements DatabaseService.ModelId, Parcelable {
    String id;
    String nome;
    String senha;
    String email;
    String foto;
    int seguindo;
    int seguidores;
    int publicacoes;

    public Usuario(){}

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.seguindo = 0;
        this.seguidores = 0;
        this.publicacoes = 0;
    }

    protected Usuario(Parcel in) {
        id = in.readString();
        nome = in.readString();
        senha = in.readString();
        email = in.readString();
        foto = in.readString();
        seguindo = in.readInt();
        seguidores = in.readInt();
        publicacoes = in.readInt();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getPublicacoes() {
        return publicacoes;
    }

    public void setPublicacoes(int publicacoes) {
        this.publicacoes = publicacoes;
    }

    @Exclude
    @Override
    public int describeContents() {
        return 0;
    }

    @Exclude
    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeString(nome);
        parcel.writeString(senha);
        parcel.writeString(email);
        parcel.writeString(foto);
        parcel.writeInt(seguindo);
        parcel.writeInt(seguidores);
        parcel.writeInt(publicacoes);
    }
}
