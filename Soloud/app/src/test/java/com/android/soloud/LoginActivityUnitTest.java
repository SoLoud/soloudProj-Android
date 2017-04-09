/*
package com.android.soloud;

import android.os.Build;

import com.android.soloud.activities.LoginActivity;
import com.android.soloud.apiCalls.LoginService;
import com.android.soloud.models.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import retrofit2.Callback;

import static org.junit.Assert.assertThat;

*/
/**
 * Created by f.stamopoulos on 20/3/2017.
 *//*


@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(CustomTestRunner.class)
public class LoginActivityUnitTest {

    private LoginActivity activity;

    @Mock
    private LoginService mockApi;

    @Captor
    private ArgumentCaptor<Callback<User>> cb;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class);
        activity = controller.get();
         //activity.setApi(mockApi);
        mockApi = ServiceGenerator.createService(LoginService.class);

        controller.create();
    }

    @Test
    public void shouldFillAdapterWithReposFromApi() throws Exception {
        //Mockito.verify(mockApi).login(Mockito.anyString(), cb.capture());

        */
/*List<Repository> testRepos = new ArrayList<Repository>();
        testRepos.add(new Repository("rails", "ruby", new Owner("dhh")));
        testRepos.add(new Repository("android", "java", new Owner("google")));

        cb.getValue().success(testRepos, null);*//*


        //assertThat(activity.getListAdapter()).hasCount(2);
    }

    */
/*@Test
    public void shouldToastSadMessageIfNoRepos() throws Exception {
        Mockito.verify(mockApi).repositories(Mockito.anyString(), cb.capture());

        List<Repository> noRepos = new ArrayList<Repository>();

        cb.getValue().success(noRepos, null);

        assertThat(ShadowToast.getTextOfLatestToast()).contains("No repos :(");
        assertThat(activity.getListAdapter()).isEmpty();
    }

    @Test
    public void shouldToastIfApiError() throws Exception {
        Mockito.verify(mockApi).repositories(Mockito.anyString(), cb.capture());

        cb.getValue().failure(null);

        assertThat(ShadowToast.getTextOfLatestToast()).contains("Failed");
    }*//*



}
*/
