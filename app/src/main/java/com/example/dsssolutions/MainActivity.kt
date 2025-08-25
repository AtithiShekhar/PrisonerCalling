package com.example.dsssolutions

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser

class MainActivity : AppCompatActivity() {

    lateinit var currentUserNameTxt: TextView
    lateinit var noCallTxt: TextView
    lateinit var callCardsContainer: LinearLayout

    lateinit var videoCallBtn1: ZegoSendCallInvitationButton
    lateinit var videoCallBtn2: ZegoSendCallInvitationButton
    lateinit var videoCallBtn3: ZegoSendCallInvitationButton

    lateinit var role: String
    lateinit var username: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        role = intent.getStringExtra("role")!!
        username = intent.getStringExtra("username")!!

        currentUserNameTxt = findViewById(R.id.CurrentUserNameTxt)
        noCallTxt = findViewById(R.id.NoCallTxt)
        callCardsContainer = findViewById(R.id.CallCardsContainer)

        videoCallBtn1 = findViewById(R.id.VideoCallBtn1)
        videoCallBtn2 = findViewById(R.id.VideoCallBtn2)
        videoCallBtn3 = findViewById(R.id.VideoCallBtn3)

        currentUserNameTxt.text = "Hello $username"

        val config = ZegoUIKitPrebuiltCallInvitationConfig()
        ZegoUIKitPrebuiltCallService.init(application, Constants.appId, Constants.appSign, username, username, config)

        if(role == "prisoner") {
            callCardsContainer.visibility = View.VISIBLE
            noCallTxt.visibility = View.GONE

            setupCallButton(videoCallBtn1, DemoUsers.receiverId)
            setupCallButton(videoCallBtn2, DemoUsers.receiverId)
            setupCallButton(videoCallBtn3, DemoUsers.receiverId)
        } else {
            callCardsContainer.visibility = View.GONE
            noCallTxt.visibility = View.VISIBLE
        }
    }

    private fun setupCallButton(button: ZegoSendCallInvitationButton, targetUserId: String) {
        button.setIsVideoCall(true)
        button.resourceID = "zego_uikit_call"
        button.setInvitees(listOf(ZegoUIKitUser(targetUserId, targetUserId)))
    }
}
