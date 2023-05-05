package com.cursoandroid.olx.activity.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.cursoandroid.olx.R;

public class CadastrarAnuncioViewModel extends ViewModel {

    private final MutableLiveData<CadastrarAnuncioState> casLiveData = new MutableLiveData<>();
    public CadastrarAnuncioViewModel(){

    }

    public MutableLiveData<CadastrarAnuncioState> getCasLiveData() {
        return casLiveData;
    }

    public void casDataChanged(String estado, String categorias, String title, String values,
                               String tel, String description, Integer numPhoto){
        if(!isEstadoValid(estado)){
            casLiveData.setValue(new CadastrarAnuncioState(R.string.invalid_estado,null,null,null,null,null,null));
        } else if (!isCategoriasValid(categorias)) {
            casLiveData.setValue(new CadastrarAnuncioState(null, R.string.invalid_category,null,null,null,null,null));
        }else if (!isTitleValid(title)) {
            casLiveData.setValue(new CadastrarAnuncioState(null, null,R.string.invalid_title,null,null,null,null));
        }else if (!isValuesValid(values)) {
            casLiveData.setValue(new CadastrarAnuncioState(null, null,null,R.string.invalid_values,null,null,null));
        }else if (!isTelValid(tel)) {
            casLiveData.setValue(new CadastrarAnuncioState(null,null,null,null,R.string.invalid_tel,null,null));
        }else if (!isDescriptionValid(description)) {
            casLiveData.setValue(new CadastrarAnuncioState(null,null,null,null,null,R.string.invalid_description,null));
        }else if (!isNumPhotoValid(numPhoto)) {
            casLiveData.setValue(new CadastrarAnuncioState(null,null,null,null,null,null,R.string.invalid_num_photo));
        }else  {
            casLiveData.setValue(new CadastrarAnuncioState(true));
        }
    }

    private boolean isEstadoValid(String estado){
        return estado != null && !estado.trim().isEmpty();
    }

    private boolean isCategoriasValid(String categorias){
        return categorias != null && !categorias.trim().isEmpty();
    }

    private boolean isTitleValid(String title){
        return title != null && !title.trim().isEmpty();
    }
    private boolean isValuesValid(String values){
        return values != null && !values.trim().isEmpty() && !values.equals("0");
    }
    private boolean isTelValid(String tel){
        return tel != null && !tel.trim().isEmpty() && tel.length() >= 10;
    }
    private boolean isDescriptionValid(String description){
        return description != null && !description.trim().isEmpty();
    }

    private boolean isNumPhotoValid(Integer numPhoto){return numPhoto != null && numPhoto != 0;}


    public static class CadastrarAnuncioViewModelFactory implements ViewModelProvider.Factory {

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(CadastrarAnuncioViewModel.class)) {
                return (T) new CadastrarAnuncioViewModel();
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }
}
