/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.providers.media.photopicker.espresso;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.android.providers.media.photopicker.espresso.RecyclerViewMatcher.withRecyclerView;
import static com.android.providers.media.photopicker.espresso.RecyclerViewTestUtils.assertItemDisplayed;
import static com.android.providers.media.photopicker.espresso.RecyclerViewTestUtils.assertItemNotDisplayed;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.android.providers.media.R;
import com.android.providers.media.photopicker.util.DateTimeUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class PhotosTabTest extends PhotoPickerBaseTest {
    private static final int ICON_THUMBNAIL_ID = R.id.icon_thumbnail;
    private static final int ICON_GIF_ID = R.id.icon_gif;
    private static final int VIDEO_CONTAINER_ID = R.id.video_container;
    private static final int ICON_CHECK_ID = R.id.icon_check;
    private static final int OVERLAY_GRADIENT_ID = R.id.overlay_gradient;

    @Rule
    public final ActivityScenarioRule<PhotoPickerTestActivity> mRule
            = new ActivityScenarioRule<>(PhotoPickerBaseTest.getSingleSelectionIntent());

    @Test
    public void testPhotoGridLayout_photoGrid() {
        onView(withId(PICKER_TAB_RECYCLERVIEW_ID)).check(matches(isDisplayed()));

        // check the count of items
        onView(withId(PICKER_TAB_RECYCLERVIEW_ID)).check(new RecyclerViewItemCountAssertion(4));

        // Verify first item is recent header
        onView(withRecyclerView(PICKER_TAB_RECYCLERVIEW_ID)
                .atPositionOnView(0, R.id.date_header_title))
                .check(matches(withText(R.string.recent)));

        // Verify bottom bar is not displayed
        onView(withId(R.id.picker_bottom_bar)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testPhotoGridLayout_image() {
        onView(withId(PICKER_TAB_RECYCLERVIEW_ID)).check(matches(isDisplayed()));

        // Verify second item in the recycler view is image
        final int position = 1;
        // Verify we have the thumbnail
        assertItemDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_THUMBNAIL_ID);

        // Verify check icon, gif icon and video icon are not displayed
        assertItemNotDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, OVERLAY_GRADIENT_ID);
        assertItemNotDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_CHECK_ID);
        assertItemNotDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_GIF_ID);
        assertItemNotDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, VIDEO_CONTAINER_ID);
    }

    @Test
    public void testPhotoGridLayout_gif() {
        onView(withId(PICKER_TAB_RECYCLERVIEW_ID)).check(matches(isDisplayed()));

        // Verify third item in the recycler view is video
        final int position = 2;
        // Verify we have the thumbnail
        assertItemDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_THUMBNAIL_ID);
        // Verify gif icon is displayed
        assertItemDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, OVERLAY_GRADIENT_ID);
        assertItemDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_GIF_ID);

        // Verify check icon and video icon are not displayed
        assertItemNotDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_CHECK_ID);
        assertItemNotDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, VIDEO_CONTAINER_ID);
    }

    @Test
    public void testPhotoGridLayout_video() {
        onView(withId(PICKER_TAB_RECYCLERVIEW_ID)).check(matches(isDisplayed()));

        // Verify fourth item in the recycler view is video
        final int position = 3;
        // Verify we have the thumbnail
        assertItemDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_THUMBNAIL_ID);

        // Verify video icon and duration are displayed
        assertItemDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, OVERLAY_GRADIENT_ID);
        assertItemDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, VIDEO_CONTAINER_ID);
        assertItemDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, R.id.video_duration);
        assertItemDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, R.id.icon_video);
        onView(withRecyclerView(PICKER_TAB_RECYCLERVIEW_ID)
                .atPositionOnView(position, R.id.video_duration))
                .check(matches(withText(containsString("0"))));

        // Verify check icon and gif icon are not displayed
        assertItemNotDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_CHECK_ID);
        assertItemNotDisplayed(PICKER_TAB_RECYCLERVIEW_ID, position, ICON_GIF_ID);
    }

    @Test
    public void testPhotoGrid_albumPhotos() {
        // Navigate to Albums tab
        onView(allOf(withText(PICKER_ALBUMS_STRING_ID), withParent(withId(CHIP_CONTAINER_ID))))
                .perform(click());

        final int cameraStringId = R.string.picker_category_camera;
        // Navigate to photos in Camera album
        onView(allOf(withText(cameraStringId),
                isDescendantOfA(withId(PICKER_TAB_RECYCLERVIEW_ID)))).perform(click());

        final int dateHeaderTitleId = R.id.date_header_title;
        final int recentHeaderPosition = 0;
        // Verify that first item is not a recent header
        assertItemDisplayed(PICKER_TAB_RECYCLERVIEW_ID, recentHeaderPosition, dateHeaderTitleId);
        onView(withRecyclerView(PICKER_TAB_RECYCLERVIEW_ID)
                .atPositionOnView(recentHeaderPosition, dateHeaderTitleId))
                .check(matches(not(withText(R.string.recent))));

        // Verify that first item is TODAY
        onView(withRecyclerView(PICKER_TAB_RECYCLERVIEW_ID)
                .atPositionOnView(0, dateHeaderTitleId))
                .check(matches(withText(DateTimeUtils.getDateTimeString(getTargetContext(),
                        System.currentTimeMillis()))));

        final int photoItemPosition = 1;
        // Verify first item is image and has no other icons other than thumbnail
        assertItemDisplayed(PICKER_TAB_RECYCLERVIEW_ID, photoItemPosition, ICON_THUMBNAIL_ID);

        // Verify check icon, gif icon and video icon are not displayed
        assertItemNotDisplayed(PICKER_TAB_RECYCLERVIEW_ID, photoItemPosition, OVERLAY_GRADIENT_ID);
        assertItemNotDisplayed(PICKER_TAB_RECYCLERVIEW_ID, photoItemPosition, ICON_CHECK_ID);
        assertItemNotDisplayed(PICKER_TAB_RECYCLERVIEW_ID, photoItemPosition, ICON_GIF_ID);
        assertItemNotDisplayed(PICKER_TAB_RECYCLERVIEW_ID, photoItemPosition, VIDEO_CONTAINER_ID);

        // Verify that toolbar has the title as category name
        onView(allOf(withText(cameraStringId), withParent(withId(R.id.toolbar))))
                .check(matches(isDisplayed()));

        // Verify that tab chips are not shown on the toolbar
        onView(withId(CHIP_CONTAINER_ID)).check(matches(not(isDisplayed())));

        // Click back button
        onView(withContentDescription("Navigate up")).perform(click());

        // on clicking back button we are back to Album grid
        onView(allOf(withText(PICKER_ALBUMS_STRING_ID), withParent(withId(CHIP_CONTAINER_ID))))
                .check(matches(isSelected()));
        onView(allOf(withText(cameraStringId),
                isDescendantOfA(withId(PICKER_TAB_RECYCLERVIEW_ID)))).check(matches(isDisplayed()));
    }
}