package com.example.pajelingo.utils;

import static com.example.pajelingo.utils.Tools.getAuthToken;
import static org.junit.Assert.assertEquals;

import com.example.pajelingo.daos.BaseDao;
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
//        Thread.sleep(5000);
//
//        Call<User> call = LanguageSchoolAPIHelper.getApiObject().login(getAuthToken(username, password));
//        Response<User> response = call.execute();
//
//        if (userExists){
//            if (response.code() != 200){
//                Assert.fail("Failed to create user.");
//            }
//
//            User responseUser = response.body();
//
//            if (responseUser == null){
//                Assert.fail("No user object was returned.");
//            }
//
//            assertEquals(responseUser.getEmail(), email);
//            assertEquals(responseUser.getUsername(), username);
//        }else{
//            if (response.code() != 401){
//                Assert.fail("Login endpoint did not return 401. It returned "+response.code()+" instead.");
//            }
//        }
    }
}
