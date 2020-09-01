package com.example.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var COUNTRY_CODE: String
    private lateinit var PHONE_NUMBER: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        phoneNumberText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                nextBtn.isEnabled = !(s.isNullOrEmpty() || s.length<10 || s.length>10)
            }
        })


        nextBtn.setOnClickListener {
            checkNumber()
        }
    }

    private fun checkNumber() {
        COUNTRY_CODE = ccp.selectedCountryCodeWithPlus
        PHONE_NUMBER = COUNTRY_CODE + phoneNumberText.text.toString()

        notifyUser()
    }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("  \"We will be verifying the phone number:$PHONE_NUMBER\\n\" +\n" +
                    "\"Is this OK, or would you like to edit the number?\"")
                .setPositiveButton("OK"){_,_ ->
                    showOTPActivity()
                }.setNegativeButton("Edit"){dialog, which ->
                    dialog.dismiss()
                }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showOTPActivity() {

    }

}