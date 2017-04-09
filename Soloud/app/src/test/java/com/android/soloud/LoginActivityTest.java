/*
package com.android.soloud;

import com.android.soloud.apiCalls.LoginService;
import com.android.soloud.models.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

*/
/**
 * Created by f.stamopoulos on 20/3/2017.
 *//*


public class LoginActivityTest {

    @Test
    public void test() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                //TODO Add your Retrofit parameters here
                .build();

        MockResponse response = new MockResponse()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .setBody("{\"access_token\":\"--hSuWsz_gqI5h5aZSqzJXviE9vukAT_luUQgAhJJiW_wr2GDBOXCXQG0CXFdLD1WSzg-jid4sjbvQMaDqZ9K53a50i4amJQmvfS0oOULPO3PSo-yOdXpO_1t3ME5p9ze7vC5Qe80n0kuOq-tkFkVDv2-gESiV7K3xEexA2m76ZuqldtAWIVIyeRFY3HL8TWVII-AgSuT5IH841lNmoj3eoIvH1WOD4PeNx1WgdcDSyz-wv2MsfYoK_t87yAbn9GrqdbSJkMThGrcH6lkFiyQfiEhj812Lfe-E-jOxYfalBI98_zMprCmPZkvaZC66uuZQ_AA4wFd62dH5NiWJBmw9wdeqab3dG3OmctWnNlh8ozPQYi6OWJNUIsxiPewto-9nj5_zLLqYUd5S7G1dO13l2PMQsmBezC8QTIQPhqn7wGYbgUrpqRT1hhHyY2PdyCn_6iGcXZFnRkcHNU7OIVvWcSOrczoi9Rsg7e6o89l-hevAeVp5uIt0Iw4hXQg9X_8r1yl2Jnc3k2bRHoxl_BLXcE7i03hxkrB3kllgxjiNXSZvxfA2RPUsk5jSCh_KK_N9jEGWa4eDycbkjZ7L5-D6yx-PfabH76Ja8AN6lQ_x9L2W7oEgFYcGhJONBOHqKYyzvIndgYHGO8__ZuMP9L0406qNfpcZMZHD3EWSq7ltdOCy6z7NvINFwIziHuwl04eiBIIsddkMxpQmpwOml6UBuvfJWKXzbQLj9CiNKwUnlUXKNWjytGU0wBrKAF6AuoXTGytSynoc0vr7KtuzWLX5Ood_ih6wSNgHmEGIS0KMTwWr-rR2Ut3lDTcC6oeZgtWBDK771fk6-IEof5g05hSXFgywcC3wD2bLNSxUsKMYwFwQ6qrPovfoGNEWw9dKsUw2qxb-jXr6m7_RRHgxjNoQ\",\"token_type\":\"bearer\",\"expires_in\":2591999,\"User\":\"{\\\"Claims\\\":[],\\\"Logins\\\":[],\\\"Posts\\\":[],\\\"Roles\\\":[{\\\"UserId\\\":\\\"29e48619-45e3-4494-a177-dc7dded9cefc\\\",\\\"RoleId\\\":\\\"7b925814-50d3-40de-8b5b-f65fcb5d0362\\\"}],\\\"Hometown\\\":null,\\\"Email\\\":\\\"icsd04090@hotmail.com\\\",\\\"EmailConfirmed\\\":false,\\\"PasswordHash\\\":null,\\\"SecurityStamp\\\":\\\"c29363c6-aef7-4e79-aa1b-b53417b062fb\\\",\\\"PhoneNumber\\\":null,\\\"PhoneNumberConfirmed\\\":false,\\\"TwoFactorEnabled\\\":false,\\\"LockoutEndDateUtc\\\":null,\\\"LockoutEnabled\\\":true,\\\"AccessFailedCount\\\":0,\\\"Id\\\":\\\"29e48619-45e3-4494-a177-dc7dded9cefc\\\",\\\"UserName\\\":\\\"icsd04090@hotmail.com\\\"}\",\".issued\":\"Mon, 20 Mar 2017 20:36:51 GMT\",\".expires\":\"Wed, 19 Apr 2017 20:36:51 GMT\"}");
        response.throttleBody(1024, 1, TimeUnit.SECONDS);

        //Set a response for retrofit to handle. You can copy a sample
        //response from your server to simulate a correct result or an error.
        //MockResponse can also be customized with different parameters
        //to match your test needs
        mockWebServer.enqueue(response);

        LoginService service = retrofit.create(LoginService.class);

        //With your service created you can now call its method that should
        //consume the MockResponse above. You can then use the desired
        //assertion to check if the result is as expected. For example:
        Call<User> call = service.login("facebook","testToken","password");
        assertTrue(call.execute() != null);
        assertTrue("Call was executed", call.isExecuted());
        assertFalse("Call was cancelled", call.isCanceled());

        //Finish web server
        mockWebServer.shutdown();
    }
}
*/
