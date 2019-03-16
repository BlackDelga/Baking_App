package com.example.android.baking_app.network;

import com.example.android.baking_app.model.Recipe;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface TheRecipeDbService {
    public static final String URL = "http://go.udacity.com";

    @GET("android-baking-app-json")
    Call<List<Recipe>> getRecipes();
}
