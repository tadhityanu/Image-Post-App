package com.example.submissionintermediate

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.submissionintermediate.Login.LoginActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class loginActivityTest {

    private val dummyEmail = "topan@gmail.com"
    private val emailErrorMessage = "email tidak boleh kosong"
    private val emptyText = ""

    @Before
    fun setup(){
        ActivityScenario.launch(LoginActivity::class.java)
    }

    @Test
    fun loginFeature(){
        onView(withId(R.id.edt_email)).check(matches(isDisplayed()))
        onView(withId(R.id.edt_email)).perform(typeText(emptyText), closeSoftKeyboard())

        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).perform(click())

        onView(withId(R.id.email_edit_layout)).check(matches(hasErrorText(emailErrorMessage)))
        onView(withId(R.id.edt_email)).perform(typeText(dummyEmail))
    }

}