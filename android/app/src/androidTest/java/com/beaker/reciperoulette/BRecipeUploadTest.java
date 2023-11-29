package com.beaker.reciperoulette;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.widget.EditText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BRecipeUploadTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void waitForMenu() throws InterruptedException {
        Thread.sleep(500);
    }
    @Test
    public void checkCameraButtonVisible() {
        onView(withText(R.string.take_photo)).perform(click());
        onView(ViewMatchers.withId(R.id.capture_image_btn))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkGalleryButtonVisible() {
        onView(withText(R.string.take_photo)).perform(click());

        onView(ViewMatchers.withId(R.id.select_image_btn))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkManualEntryButtonVisible() {
        onView(withText(R.string.take_photo)).perform(click());

        onView(ViewMatchers.withId(R.id.manual_entry_btn))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkSendImageButtonNotVisibleInitially() {
        onView(withText(R.string.take_photo)).perform(click());

        onView(ViewMatchers.withId(R.id.send_image_btn))
                .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void captureImageAndCheckSendButton() {
        onView(withText(R.string.take_photo)).perform(click());

        // Capture Image
        onView(ViewMatchers.withId(R.id.capture_image_btn))
                .perform(click());

        // NOTE: Manual Accept Permissions and Take Photo with Camera

        // Check if send button is visible
        onView(ViewMatchers.withId(R.id.send_image_btn))
                .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void selectImageFromGalleryAndCheckSendButton() {
        onView(withText(R.string.take_photo)).perform(click());

        // Gallery Image
        onView(ViewMatchers.withId(R.id.select_image_btn))
                .perform(click());

        // NOTE: Manual Accept Permissions and Select Photo from Gallery

        // Check if send button is visible
        onView(ViewMatchers.withId(R.id.send_image_btn))
                .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void enterGroceryItemInManualEntryDialog() {
        onView(withText(R.string.take_photo)).perform(click());

        // Manual Entry
        onView(ViewMatchers.withId(R.id.manual_entry_btn))
                .perform(click());

        onView(withText("Enter Grocery Item"))
                .check(matches(isDisplayed()));

        onView(ViewMatchers.withClassName(Matchers.equalTo(EditText.class.getName())))
                .perform(ViewActions.typeText("Chicken"), ViewActions.closeSoftKeyboard());

        onView(withText("Submit"))
                .perform(click());

        onView(ViewMatchers.withId(R.id.send_image_btn))
                .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void cancelManualEntryDialog() {
        onView(withText(R.string.take_photo)).perform(click());

        // Cancelled Manual Entry
        onView(ViewMatchers.withId(R.id.manual_entry_btn))
                .perform(click());

        onView(withText("Enter Grocery Item"))
                .check(matches(isDisplayed()));

        onView(withText("Cancel"))
                .perform(click());

        onView(ViewMatchers.withId(R.id.send_image_btn))
                .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }
}
