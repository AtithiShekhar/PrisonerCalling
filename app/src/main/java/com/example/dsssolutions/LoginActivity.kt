package com.example.dsssolutions

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    lateinit var userIdInput: EditText
    lateinit var passwordInput: EditText
    lateinit var receiverNameInput: EditText
    lateinit var relationInput: EditText
    lateinit var loginBtn: Button
    lateinit var rbPrisoner: RadioButton
    lateinit var rbReceiver: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userIdInput = findViewById(R.id.UserInput)
        passwordInput = findViewById(R.id.PasswordInput)
        receiverNameInput = findViewById(R.id.ReceiverNameInput)
        relationInput = findViewById(R.id.RelationInput)
        loginBtn = findViewById(R.id.LoginBtn)
        rbPrisoner = findViewById(R.id.rbPrisoner)
        rbReceiver = findViewById(R.id.rbReceiver)

        rbPrisoner.setOnClickListener { toggleFields() }
        rbReceiver.setOnClickListener { toggleFields() }

        loginBtn.setOnClickListener {
            val id = userIdInput.text.toString()
            val password = passwordInput.text.toString()
            val receiverName = receiverNameInput.text.toString()
            val relation = relationInput.text.toString()

            if (rbPrisoner.isChecked) {
                if (id == DemoUsers.prisonerId && password == DemoUsers.prisonerPassword) {
                    goToMain("prisoner", id)
                } else {
                    Toast.makeText(this,"Invalid Prisoner ID/Password", Toast.LENGTH_SHORT).show()
                }
            } else if (rbReceiver.isChecked) {
                if (id == DemoUsers.receiverId && password == DemoUsers.receiverPassword &&
                    receiverName.isNotEmpty() && relation.isNotEmpty()) {
                    goToMain("receiver", receiverName)
                } else {
                    Toast.makeText(this,"Invalid info for Receiver", Toast.LENGTH_SHORT).show() } } } }

    private fun toggleFields() {
        if (rbReceiver.isChecked) {
            receiverNameInput.visibility = View.VISIBLE
            relationInput.visibility = View.VISIBLE
        } else {
            receiverNameInput.visibility = View.GONE
            relationInput.visibility = View.GONE
        } }
    private fun goToMain(role: String, username: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("role", role)
        intent.putExtra("username", username)
        startActivity(intent)
    } }
