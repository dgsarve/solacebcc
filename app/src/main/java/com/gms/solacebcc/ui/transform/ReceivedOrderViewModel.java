package com.gms.solacebcc.ui.transform;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReceivedOrderViewModel extends ViewModel {

    private final MutableLiveData<List<String>> mTexts;

    public ReceivedOrderViewModel() {
        mTexts = new MutableLiveData<>();
        List<String> texts = new ArrayList<>();
        mTexts.setValue(texts);
    }

    public LiveData<List<String>> getTexts() {
        return mTexts;
    }
    public void updateTexts(List<String> newTexts) {
        mTexts.postValue(newTexts);
    }
}