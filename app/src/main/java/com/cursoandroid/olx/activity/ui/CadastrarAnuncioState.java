package com.cursoandroid.olx.activity.ui;

import androidx.annotation.Nullable;

public class CadastrarAnuncioState {
    private final boolean isDataValid;
    @Nullable
    private Integer estadoError;
    @Nullable
    private Integer categoriasError;
    @Nullable
    private Integer titleError;
    @Nullable
    private Integer valuesError;
    @Nullable
    private Integer telError;
    @Nullable
    private Integer descriptionError;
    @Nullable
    private Integer numPhotoError;

    public CadastrarAnuncioState(@Nullable Integer estadoError, @Nullable Integer categoriasError,
                                 @Nullable Integer titleError, @Nullable Integer valuesError, @Nullable Integer telError,
                                 @Nullable Integer descriptionError, @Nullable Integer numPhotoError) {
        this.estadoError = estadoError;
        this.categoriasError = categoriasError;
        this.titleError = titleError;
        this.valuesError = valuesError;
        this.telError = telError;
        this.descriptionError = descriptionError;
        this.numPhotoError = numPhotoError;
        this.isDataValid = false;
    }

    public CadastrarAnuncioState(boolean isDataValid) {
        this.estadoError = null;
        this.categoriasError = null;
        this.titleError = null;
        this.valuesError = null;
        this.telError = null;
        this.descriptionError = null;
        this.isDataValid = isDataValid;
    }

    public boolean isDataValid() {
        return isDataValid;
    }

    @Nullable
    public Integer getEstadoError() {
        return estadoError;
    }

    @Nullable
    public Integer getCategoriasError() {
        return categoriasError;
    }

    @Nullable
    public Integer getTitleError() {
        return titleError;
    }

    @Nullable
    public Integer getValuesError() {
        return valuesError;
    }

    @Nullable
    public Integer getTelError() {
        return telError;
    }

    @Nullable
    public Integer getDescriptionError() {
        return descriptionError;
    }

    @Nullable
    public Integer getNumPhotoError() {
        return numPhotoError;
    }
}
