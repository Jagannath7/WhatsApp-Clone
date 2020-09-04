package com.example.whatsappclone

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
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_o_t_p.*
import java.util.concurrent.TimeUnit


const val PHONE_NUMBER = "phoneNumber"


class OTPActivity : AppCompatActivity() {

    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var phoneNumber: String? = null
    var mVerficationCode: String? = null
    var mResendCode: PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p)
        initViews()
        startVerification()

    }

    private fun startVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber!!, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks
        showCountDownTimer(60000)
    }

    private fun showCountDownTimer(milliSecInFuture: Long) {
        resendBtn.isEnabled = false
        object : CountDownTimer(milliSecInFuture, 1000){

            override fun onTick(miliSecUntilFinish: Long) {
                counterTv.text = getString(R.string.seconds_remaining, miliSecUntilFinish/1000)
            }

            override fun onFinish() {
                counterTv.text = ""
                resendBtn.isEnabled = true
            }
        }.start()
    }

    private fun initViews() {
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        verifyTv.text = getString(R.string.verfiy_number, phoneNumber)
        setSpannableString()

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {


                val smsCode = credential.smsCode
                if(!smsCode.isNullOrBlank()) {
                    sentcodeEt.setText(smsCode)
                }
//                signInWithPhoneAuthCredential(credential)
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
                mVerficationCode = verificationId
                mResendCode = token

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

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }

    override fun onBackPressed() {}
}