package io.thoughtbox.hamdan.utls;

public class MutableEventLiveData<T> extends EventLiveData<T> {

    @Override
    public void postValue(T value) {
        super.postValue(value);
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
    }
}