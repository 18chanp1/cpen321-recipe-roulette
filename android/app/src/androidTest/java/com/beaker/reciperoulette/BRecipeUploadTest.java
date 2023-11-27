package com.beaker.reciperoulette;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.widget.EditText;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BRecipeUploadTest {

    @Rule
    public ActivityScenarioRule<TakePhoto> mActivityScenarioRule =
            new ActivityScenarioRule<>(TakePhoto.class);

    @Test
    public void checkCameraButtonVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.capture_image_btn))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkGalleryButtonVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.select_image_btn))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkManualEntryButtonVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.manual_entry_btn))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkSendImageButtonNotVisibleInitially() {
        Espresso.onView(ViewMatchers.withId(R.id.send_image_btn))
                .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void captureImageAndCheckSendButton() {
        // Capture Image
        Espresso.onView(ViewMatchers.withId(R.id.capture_image_btn))
                .perform(ViewActions.click());

        // NOTE: Manual Accept Permissions and Take Photo with Camera

        // Check if send button is visible
        Espresso.onView(ViewMatchers.withId(R.id.send_image_btn))
                .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void selectImageFromGalleryAndCheckSendButton() {
        // Gallery Image
        Espresso.onView(ViewMatchers.withId(R.id.select_image_btn))
                .perform(ViewActions.click());

        // NOTE: Manual Accept Permissions and Select Photo from Gallery

        // Check if send button is visible
        Espresso.onView(ViewMatchers.withId(R.id.send_image_btn))
                .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void enterGroceryItemInManualEntryDialog() {
        // Manual Entry
        Espresso.onView(ViewMatchers.withId(R.id.manual_entry_btn))
                .perform(ViewActions.click());

        Espresso.onView(withText("Enter Grocery Item"))
                .check(matches(isDisplayed()));

        Espresso.onView(ViewMatchers.withClassName(Matchers.equalTo(EditText.class.getName())))
                .perform(ViewActions.typeText("Chicken"), ViewActions.closeSoftKeyboard());

        Espresso.onView(withText("Submit"))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.send_image_btn))
                .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void cancelManualEntryDialog() {
        // Cancelled Manual Entry
        Espresso.onView(ViewMatchers.withId(R.id.manual_entry_btn))
                .perform(ViewActions.click());

        Espresso.onView(withText("Enter Grocery Item"))
                .check(matches(isDisplayed()));

        Espresso.onView(withText("Cancel"))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.send_image_btn))
                .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }
}
