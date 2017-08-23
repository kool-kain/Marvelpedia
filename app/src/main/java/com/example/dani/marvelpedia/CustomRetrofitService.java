package com.example.dani.marvelpedia;


import model.RetrofitMarvelResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Dani on 30/12/16.
 */

public interface CustomRetrofitService{
    @GET("/v1/public/characters")
    Call<RetrofitMarvelResponse> getCharacterByName(@Query("nameStartsWith") String nameStartsWith,
                                                    @Query("apikey") String api_key,
                                                    @Query("ts") String ts,
                                                    @Query("hash") String hash);
    @GET("/v1/public/comics")
    Call<RetrofitMarvelResponse> getComicByTitle(@Query("titleStartsWith") String titleStartsWith,
                                                    @Query("apikey") String api_key,
                                                    @Query("ts") String ts,
                                                    @Query("hash") String hash);
    @GET("/v1/public/characters/{characterId}/comics")
    Call<RetrofitMarvelResponse> getComicsByIdCharacter(@Path("characterId") int characterId,
                                                  @Query("apikey") String api_key,
                                                  @Query("ts") String ts,
                                                  @Query("hash") String hash);
    @GET("/v1/public/comics/{comicId}/characters")
    Call<RetrofitMarvelResponse> getCharactersByIdComic(@Path("comicId") int comicId,
                                                  @Query("apikey") String api_key,
                                                  @Query("ts") String ts,
                                                  @Query("hash") String hash);
}
