package com.example.submissionintermediate.Login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionintermediate.Api.ApiConfig
import com.example.submissionintermediate.Api.Response.LoginResponse
import com.example.submissionintermediate.Data.User
import com.example.submissionintermediate.Data.UserPreference
import com.example.submissionintermediate.Data.ViewModelFactory
import com.example.submissionintermediate.HomePage.MainActivity
import com.example.submissionintermediate.databinding.ActivityLoginBinding
import com.example.submissionintermediate.signup.SignupActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var loginViewModel : LoginViewModel

    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setAction()
        toSignup()
        emailSet()
        passSet()
        setAnimation()


    }

    private fun setupViewModel(){
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore),this)
        )[LoginViewModel::class.java]
    }

    private fun setAction(){
        val login = binding.btnLogin

        login.setOnClickListener{
            showLoading(true)
            val email =binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()

            when {
                email.isEmpty() ->{
                    showLoading(false)
                    binding.emailEditLayout.error = "email tidak boleh kosong"
                }
                pass.isEmpty() -> {
                    showLoading(false)
                    binding.passEditLayout.error = "password tidak boleh kosong"
                }

                else -> {
                    hideError()
                    login()

                }
            }
        }
    }



    private fun login(){
        val email =  binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        val client = ApiConfig.getApiService().uploadLogin(
            email,
            password
        )
        client.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                when{
                    (response.isSuccessful) -> {

                        showLoading(false)
                        val responseBody = response.body()?.loginResult as User
                        loginViewModel.saveUser(User(
                            responseBody.name,
                            email,
                            password,
                            responseBody.userId,
                            responseBody.token,
                            true
                        ))
                        Toast.makeText(this@LoginActivity, response.body()?.message, Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    response.code() == 400 -> {
                        showLoading(false)
                        Toast.makeText(this@LoginActivity, "email must be valid", Toast.LENGTH_SHORT).show()
                    }
                    response.code() == 401 -> {
                        showLoading(false)
                        Toast.makeText(this@LoginActivity, "user not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }




    private fun emailSet(){
        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                hideError()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                hideError()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun passSet(){
        val pass = binding.edtPassword
        val passLayout = binding.passEditLayout
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                hideError()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                hideError()
            }

            override fun afterTextChanged(e: Editable?) {
                if (e.toString().length < 6){
                    passLayout.setPasswordVisibilityToggleEnabled(false)
                } else {
                    hideError()
                    passLayout.setPasswordVisibilityToggleEnabled(true)
                }
            }
        })
    }

    private fun hideError(){
        val passLayout = binding.passEditLayout
        val pass = binding.edtPassword
        val emailLayout = binding.emailEditLayout
        val email = binding.edtEmail

        pass.error = null
        passLayout.error = null
        email.error = null
        emailLayout.error = null
    }

    private fun toSignup(){

        val signUpButton = binding.btnSignupView
        signUpButton.setOnClickListener{
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }


    }

    private fun showLoading(state: Boolean) {
        if(state) {
            binding.pbMain.visibility = View.VISIBLE
        } else {
            binding.pbMain.visibility = View.GONE
        }
    }

    private fun setAnimation(){
        val loginTitle = ObjectAnimator.ofFloat(binding.titleLogin, View.ALPHA, 1f).setDuration(700)
        val titleEmail = ObjectAnimator.ofFloat(binding.txtEmail, View.ALPHA, 1f).setDuration(700)
        val layoutEmail = ObjectAnimator.ofFloat(binding.emailEditLayout, View.ALPHA, 1f).setDuration(700)
        val titlePass = ObjectAnimator.ofFloat(binding.txtPassword, View.ALPHA, 1f).setDuration(700)
        val layoutPass = ObjectAnimator.ofFloat(binding.passEditLayout, View.ALPHA, 1f).setDuration(700)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(700)
        val titleSignUp = ObjectAnimator.ofFloat(binding.txtToSignup, View.ALPHA, 1f).setDuration(700)
        val toSignUp = ObjectAnimator.ofFloat(binding.btnSignupView, View.ALPHA, 1f).setDuration(700)

        val emailAnim =AnimatorSet().apply {
            playTogether(titleEmail, layoutEmail)
        }
        val passAnim = AnimatorSet().apply {
            playTogether(titlePass, layoutPass)
        }
        val toSignUpAnim = AnimatorSet().apply {
            playTogether(titleSignUp, toSignUp)
        }

        AnimatorSet().apply {
            playSequentially(
                loginTitle,
                emailAnim,
                passAnim,
                btnLogin,
                toSignUpAnim,
            )
            start()
        }

    }
}