package com.diegogmd.filmfollower.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

class LanguageInterceptor(
    private val localeProvider: () -> Locale = { Locale.getDefault() }
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Let an explicit per-call override win
        if (request.url.queryParameter("language") != null) {
            return chain.proceed(request)
        }

        val locale = localeProvider()
        val tag = if (locale.country.isNotEmpty()) {
            "${locale.language}-${locale.country}"
        } else {
            locale.language
        }

        val url = request.url.newBuilder()
            .addQueryParameter("language", tag)
            .build()

        return chain.proceed(request.newBuilder().url(url).build())
    }
}