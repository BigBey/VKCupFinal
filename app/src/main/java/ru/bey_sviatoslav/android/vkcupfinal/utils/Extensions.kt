package ru.bey_sviatoslav.android.vkcupfinal.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

fun <T, R> LiveData<T>.map(transformation: (T) -> R): LiveData<R> =
    Transformations.map(this, transformation)

fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> =
    Transformations.distinctUntilChanged(this)