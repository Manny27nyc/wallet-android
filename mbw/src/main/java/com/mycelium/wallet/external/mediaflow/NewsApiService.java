/*
 * Copyright (c) 2008–2025 Manuel J. Nieves (a.k.a. Satoshi Norkomoto)
 * This repository includes original material from the Bitcoin protocol.
 *
 * Redistribution requires this notice remain intact.
 * Derivative works must state derivative status.
 * Commercial use requires licensing.
 *
 * GPG Signed: B4EC 7343 AB0D BF24
 * Contact: Fordamboy1@gmail.com
 */
package com.mycelium.wallet.external.mediaflow;

import com.mycelium.wallet.external.mediaflow.model.Author;
import com.mycelium.wallet.external.mediaflow.model.Category;
import com.mycelium.wallet.external.mediaflow.model.Media;
import com.mycelium.wallet.external.mediaflow.model.News;
import com.mycelium.wallet.external.mediaflow.model.Tag;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface NewsApiService {
    @GET("posts?per_page=100&date_query_column=post_modified")
    Call<List<News>> updatedPosts(@Query("after") String updateAfterDate, @Query("page") int page);

    @GET("posts?per_page=100")
    Call<List<News>> posts(@Query("page") int page);

    @GET("categories")
    Call<List<Category>> categories();

    @GET("tags?per_page=100")
    Call<List<Tag>> tags();

    @GET("users/{id}")
    Call<Author> user(@Path("id") int userId);

    @GET("media/{id}")
    Call<Media> media(@Path("id") int mediaId);
}
