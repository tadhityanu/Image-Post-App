package com.example.submissionintermediate.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.submissionintermediate.Api.ApiConfig
import com.example.submissionintermediate.Api.Response.RegisterResponse
import com.example.submissionintermediate.Login.LoginActivity
import com.example.submissionintermediate.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //setViewModel()

        setAction()
        passwordSet()
        emailSet()
        nameSet()
        setAnimation()
    }

    private fun setAction(){
        
        binding.btnSignup.setOnClickListener{
            showLoading(true)
            val name = binding.edtNameSignup.text.toString()
            val email = binding.edtEmailSignup.text.toString()
            val pass = binding.edtPasswordSignup.text.toString()
            when {
                name.isEmpty() -> {
                    showLoading(false)
                    binding.nameSignupLayout.error = "Nama pengguna harus diisi"
                }
                email.isEmpty() -> {
                    showLoading(false)
                    binding.emailSignupLayout.error = "Email harus diisi"
                }
                pass.isEmpty() -> {
                    showLoading(false)
                    binding.passSignupLayout.error = "Password harus diisi"
                }
                else -> {
                    hideError()
                    register(name, email, pass)

                }
            }
        }
    }

    private fun register(name: String, email:String, password: String ) {

        val client = ApiConfig.getApiService().uploadRegister(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {

            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>,
            ) {
                if (response.isSuccessful){
                    showLoading(false)
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error){
                        Toast.makeText(this@SignupActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    showLoading(false)
                    Toast.makeText(this@SignupActivity, "Email is already taken", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@SignupActivity, "connection failed", Toast.LENGTH_SHORT).show()
            }


        })
    }

    private fun passwordSet(){
        val pass = binding.edtPasswordSignup
        val passLayout = binding.passSignupLayout

        pass.addTextChangedListener(object : TextWatcher {
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

    private fun emailSet(){
        val email = binding.edtEmailSignup

        email.addTextChangedListener(object : TextWatcher {
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

    private fun nameSet(){
        val name = binding.edtNameSignup

        name.addTextChangedListener(object : TextWatcher {
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

    private fun hideError(){
        val passLayout = binding.passSignupLayout
        val pass = binding.edtPasswordSignup
        val email = binding.edtEmailSignup
        val emailLayout = binding.emailSignupLayout
        val name = binding.edtNameSignup
        val nameLayout = binding.nameSignupLayout

        pass.error = null
        passLayout.error = null
        email.error = null
        emailLayout.error = null
        name.error = null
        nameLayout.error = null
    }

    private fun showLoading(state: Boolean) {
        if(state) {
            binding.pbMain.visibility = View.VISIBLE
        } else {
            binding.pbMain.visibility = View.GONE
        }
    }

    private fun setAnimation() {
        val titleSignUp = ObjectAnimator.ofFloat(binding.titleSignup, View.ALPHA, 1f).setDuration(700)
        val txtName = ObjectAnimator.ofFloat(binding.txtNameSignup, View.ALPHA, 1f).setDuration(700)
        val name = ObjectAnimator.ofFloat(binding.nameSignupLayout, View.ALPHA, 1f).setDuration(700)
        val txtEmail = ObjectAnimator.ofFloat(binding.txtSignupEmail, View.ALPHA, 1f).setDuration(700)
        val email = ObjectAnimator.ofFloat(binding.emailSignupLayout, View.ALPHA, 1f).setDuration(700)
        val txtPass = ObjectAnimator.ofFloat(binding.txtSignupPassword, View.ALPHA, 1f).setDuration(700)
        val pass = ObjectAnimator.ofFloat(binding.passSignupLayout, View.ALPHA, 1f).setDuration(700)
        val btnSignup = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(700)

        val textAnim = AnimatorSet().apply {
            playTogether(txtName, txtEmail, txtPass)
        }
        val layoutAnim = AnimatorSet().apply {
            playTogether(name, email, pass)
        }

        AnimatorSet().apply {
            playSequentially(
                titleSignUp,
                textAnim,
                layoutAnim,
                btnSignup
            )
            start()
        }
    }


}