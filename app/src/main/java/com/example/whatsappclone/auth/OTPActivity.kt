package com.example.whatsappclone.auth

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.example.whatsappclone.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_o_t_p.*
import java.util.concurrent.TimeUnit



const val PHONE_NUMBER = "phoneNumber"

class OTPActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var phoneNumber: String? = null
    var mVerificationCode: String? = null
    var mResendCode: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var progressDialog: ProgressDialog
    private var mCounterDown : CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p)
        initViews()
        startVerification()

    }

    private fun startVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber!!,
            60,
            TimeUnit.SECONDS,
            this,
            callbacks,
        )
        showCountDownTimer(60000)
        progressDialog = createProgressDialog("sending a verification code", true)
        progressDialog.show()
    }

    private fun showCountDownTimer(milliSecInFuture: Long) {
        resendBtn.isEnabled = false
        mCounterDown = object : CountDownTimer(milliSecInFuture, 1000){

            override fun onTick(miliSecUntilFinish: Long) {
                counterTv.text = getString(R.string.seconds_remaining, miliSecUntilFinish/1000)
            }

            override fun onFinish() {
                counterTv.text = ""
                resendBtn.isEnabled = true
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mCounterDown != null){
            mCounterDown!!.cancel()
        }
    }


    private fun initViews() {
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        verifyTv.text = getString(R.string.verfiy_number, phoneNumber)
        setSpannableString()

        verificationBtn.setOnClickListener(this)
        resendBtn.setOnClickListener(this)


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                if(::progressDialog.isInitialized){
                    progressDialog.dismiss()
                }

                val smsCode = credential.smsCode
                if(!smsCode.isNullOrBlank()) {
                    sentcodeEt.setText(smsCode)
                }
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                if (e is FirebaseAuthInvalidCredentialsException) {

                } else if (e is FirebaseTooManyRequestsException) {

                }

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                mVerificationCode = verificationId
                mResendCode = token

            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    startActivity(
                        Intent(this, SignUpActivity::class.java)
                    )
                }else{
                    notifyUserAndRetry("Your Phone Number Verification is failed.Retry again!")
                }
            }
    }

    private fun setSpannableString() {
        val span = SpannableString("Waiting to automatically detect an SMS sent to\\n ${phoneNumber} Wrong Number?")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startLoginActivity()
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ds.linkColor
            }
        }
        span.setSpan(clickableSpan, span.length - 13, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        waitingTv.movementMethod = LinkMovementMethod.getInstance()
        waitingTv.text = span
    }

    private fun notifyUserAndRetry(message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("Ok") { _, _ ->
                startLoginActivity()
            }

            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            setCancelable(false)
            create()
            show()
        }
    }

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }

    override fun onBackPressed() {}

    override fun onClick(v: View) {
        when(v){
            verificationBtn -> {
                val code = sentcodeEt.text.toString()
                if(code.isNotEmpty() && !mVerificationCode.isNullOrEmpty()){

                    progressDialog = createProgressDialog("Please Wait", false)
                    progressDialog.show()

                    val credential = PhoneAuthProvider.getCredential(mVerificationCode!!, code)
                    signInWithPhoneAuthCredential(credential)
                    
                }
            }

            resendBtn -> {

                val code = sentcodeEt.text.toString()
                if(mResendCode != null){
                    showCountDownTimer(60000)

                    progressDialog = createProgressDialog("Sending a Verification Code", false)
                    progressDialog.show()

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber!!,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        callbacks,
                        mResendCode
                    )
                }
            }
        }
    }
}

fun Context.createProgressDialog(message: String, isCancelable: Boolean): ProgressDialog {
    return ProgressDialog(this).apply {
        setCancelable(isCancelable)
        setCanceledOnTouchOutside(false)
        setMessage(message)
    }
}