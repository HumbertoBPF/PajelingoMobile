package com.example.pajelingo.utils;

import static org.junit.Assert.assertEquals;

import com.example.pajelingo.daos.BaseDao;
import com.example.pajelingo.models.Token;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import org.junit.Assert;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RetrofitTools {
    /**
     * Saves the entities (list of records of a database) retrieved by an API call.
     * @param call Retrofit call object
     * @param dao Room Database DAO (Data Access Object)
     * @param <E> Entity class
     * @throws IOException thrown when some error related with HTTP communication occurs
     */
    public static <E> void saveEntitiesFromAPI(Call<List<E>> call, BaseDao<E> dao) throws IOException {
        Response<List<E>> response = call.execute();
        List<E> entities = response.body();
        dao.save(entities);
    }

    /**
     * Asserts the existence of a user in the Django App.
     * @param email user email
     * @param username username
     * @param password user password
     * @param userExists boolean indicating if the assertion must check if the user exists or not
     * @throws IOException thrown when some error related with HTTP communication occurs
     * @throws InterruptedException thrown when some error related with main thread manipulation occurs
     */
    public static void assertUserExistsInDjangoApp(String email, String username, String password, boolean userExists) throws IOException, InterruptedException {
        Thread.sleep(5000);

        Call<Token> tokenCall = LanguageSchoolAPIHelper.getApiObject().getToken(new User("", username, password, null));
        Response<Token> tokenResponse = tokenCall.execute();
        Token token = tokenResponse.body();

        if (userExists){
            if (token == null) {
                Assert.fail("Failed to get user token");
            }

            Call<User> userCall = LanguageSchoolAPIHelper.getApiObject().login("Token " + token.getToken());
            Response<User> userResponse = userCall.execute();

            if (userResponse.code() != 200){
                Assert.fail("Failed to create user.");
            }

            User responseUser = userResponse.body();

            if (responseUser == null){
                Assert.fail("No user object was returned.");
            }

            assertEquals(responseUser.getEmail(), email);
            assertEquals(responseUser.getUsername(), username);
        }else{
            if (tokenResponse.code() != 400){
                Assert.fail("Login endpoint did not return 400. It returned "+tokenResponse.code()+" instead.");
            }
        }
    }
}
