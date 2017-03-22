package com.android.soloud.UITests;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.android.soloud.R;
import com.android.soloud.activities.LoginActivity;
import com.android.soloud.models.Category;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webClick;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.object.HasToString.hasToString;

/**
 * Created by f.stamopoulos on 5/3/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest

public class LoginInstrumentationUITest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);


    @Test
    public void assert_ui_Visibility(){
        //Assert.assertEquals("Addition not performed correctly", 5, 3 + 2);
        onView(withId(R.id.soloud_logo)).check(matches(isDisplayed()));

        onView(withId(R.id.promo_TV)).check(matches(isDisplayed()));

        onView(withId(R.id.terms_and_policy_TV)).check(matches(isDisplayed()));

        onView(withId(R.id.login_button)).check(matches(isDisplayed()));

        //onView(withId(R.id.progress_wheel)).check(doesNotExist());
        onView(withId(R.id.progress_wheel)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testLoginButton(){

        onView(withId(R.id.login_button)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.login_button)).check(matches(withText("Log in with Facebook")));

        onView(withText("Log in with Facebook")).perform(click());

        //onView(withText("Cancel")).perform(click());

        /*onWebView()
                .withElement(findElement(Locator.ID,"OK"))
                .perform(webClick());*/

        //onView(withId(R.id.progress_wheel)).check(matches(isDisplayed()));

        //onData(withName("Charity")).check(matches(isDisplayed()));

        //onData(withName("Charity")).perform(click());


    }


    public static Matcher withName(final String name){
        return new TypeSafeMatcher<Category>(){
            /**
             * Generates a description of the object.  The description may be part of a
             * a description of a larger object of which this is just a component, so it
             * should be worded appropriately.
             *
             * @param description The description to be built or appended to.
             */
            @Override
            public void describeTo(Description description) {

            }

            /**
             * Subclasses should implement this. The item will already have been checked for
             * the specific type and will never be null.
             *
             * @param category
             */
            @Override
            protected boolean matchesSafely(Category category) {
                return name.matches(category.getName());
            }
        };
    }

}
