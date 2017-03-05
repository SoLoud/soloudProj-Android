package com.android.soloud;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.android.soloud.activities.LoginActivity;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by f.stamopoulos on 5/3/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest

public class LoginInstrumentationTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Test
    public void testLoginButton(){

        onView(withId(R.id.login_button)).check(matches(withText("Log in with Facebook")));

        onView(withText("Log in with Facebook")).perform(click());
    }

    @Test
    public void testAdd(){
        Assert.assertEquals("Addition not performed correctly", 5, 3 + 2);
    }

}
