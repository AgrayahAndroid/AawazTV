package com.aawaz.tv.data

/**
 * Created by Rinav Gangar <rinav.dev> on 8/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
sealed class ResultData<out T> {

    data class Loading<out T>(val value: T? = null) : ResultData<T>()

    data class Success<out T>(val value: T) : ResultData<T>()

    data class Failure<out T>(val message: String) : ResultData<T>()

    companion object {

        fun <T> loading(value: T?): ResultData<T> =
            Loading(value)

        fun <T> success(value: T): ResultData<T> =
            Success(value)

        fun <T> failure(errorMessage: String): ResultData<T> =
            Failure(errorMessage)
    }
}