package net.labormc.core.result;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCore
 * Author  : Ryixz (Dominik Auer)
 */
public interface APIResultCallback<T> {

    void onResult(T result, Throwable throwable);

}
