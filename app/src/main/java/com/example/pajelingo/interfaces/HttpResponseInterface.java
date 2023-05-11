package com.example.pajelingo.interfaces;

import retrofit2.Response;

public interface HttpResponseInterface<E> {
    void onSuccess(E e);
    void onError(Response<E> response);
    void onFailure();
}
