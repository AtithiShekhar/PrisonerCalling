package com.example.dsssolutions

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import com.zegocloud.uikit.prebuilt.call.core.invite.ZegoCallInvitationData
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
import com.zegocloud.uikit.prebuilt.call.config.ZegoCallDurationConfig
import com.zegocloud.uikit.prebuilt.call.config.DurationUpdateListener
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    lateinit var currentUserNameTxt: TextView
    lateinit var noCallTxt: TextView
    lateinit var callCardsContainer: LinearLayout

    lateinit var videoCallBtn1: ZegoSendCallInvitationButton
    lateinit var videoCallBtn2: ZegoSendCallInvitationButton
    lateinit var videoCallBtn3: ZegoSendCallInvitationButton

    lateinit var role: String
    lateinit var username: String
    private var selectedCallTarget = ""
    private var selectedCallDuration = 0
    private var currentBalance = 2450
    private var callTimer: Timer? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addWalletCompose()

        role = intent.getStringExtra("role")!!
        username = intent.getStringExtra("username")!!

        currentUserNameTxt = findViewById(R.id.CurrentUserNameTxt)
        noCallTxt = findViewById(R.id.NoCallTxt)
        callCardsContainer = findViewById(R.id.CallCardsContainer)

        videoCallBtn1 = findViewById(R.id.VideoCallBtn1)
        videoCallBtn2 = findViewById(R.id.VideoCallBtn2)
        videoCallBtn3 = findViewById(R.id.VideoCallBtn3)

        currentUserNameTxt.text = "Hello $username"

        val config = ZegoUIKitPrebuiltCallInvitationConfig().apply {
            provider = object : ZegoUIKitPrebuiltCallConfigProvider {
                override fun requireConfig(invitationData: ZegoCallInvitationData): ZegoUIKitPrebuiltCallConfig {
                    val config = if (invitationData.type == ZegoInvitationType.VIDEO_CALL.getValue()) {
                        ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()
                    } else {
                        ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall()
                    }

                    config.durationConfig = ZegoCallDurationConfig().apply {
                        isVisible = true
                        durationUpdateListener = DurationUpdateListener { seconds ->
                            val maxDurationSeconds = selectedCallDuration * 60L
                            if (seconds >= maxDurationSeconds && selectedCallDuration > 0) {
                                runOnUiThread {
                                    ZegoUIKitPrebuiltCallService.endCall()
                                    Toast.makeText(this@MainActivity, "Call time limit reached", Toast.LENGTH_SHORT).show()
                                    callTimer?.cancel()

                                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                        showCallEndDialog()
                                    }, 1000) } } } }
                    return config } } }

        ZegoUIKitPrebuiltCallService.init(application, Constants.appId, Constants.appSign, username, username, config)

        if(role == "prisoner") {
            callCardsContainer.visibility = View.VISIBLE
            noCallTxt.visibility = View.GONE

            setupCallButton(videoCallBtn1, DemoUsers.receiverId)
            setupCallButton(videoCallBtn2, DemoUsers.receiverId)
            setupCallButton(videoCallBtn3, DemoUsers.receiverId)
        } else {
            callCardsContainer.visibility = View.GONE
            noCallTxt.visibility = View.VISIBLE } }
    private fun showTimerSelectionDialog() {
        val dialog = TimerSelectionDialog(this) { selectedMinutes ->
            selectedCallDuration = selectedMinutes
            startCallWithTimer(selectedCallTarget, selectedMinutes)
        }
        dialog.show()
    }

    private fun setupCallButton(button: ZegoSendCallInvitationButton, targetUserId: String) {
        button.setIsVideoCall(true)
        button.resourceID = "zego_uikit_call"
        button.setInvitees(listOf(ZegoUIKitUser(targetUserId, targetUserId)))

        val cardView = button.parent?.parent as? androidx.cardview.widget.CardView
        cardView?.setOnClickListener {
            selectedCallTarget = targetUserId
            showTimerSelectionDialog() } }

    private fun startCallWithTimer(targetUserId: String, durationMinutes: Int) {
        selectedCallDuration = durationMinutes

        val callButton = ZegoSendCallInvitationButton(this).apply {
            setIsVideoCall(true)
            resourceID = "zego_uikit_call"
            setInvitees(listOf(ZegoUIKitUser(targetUserId, targetUserId)))
        }

        callTimer?.cancel()
        callTimer = Timer()
        callTimer?.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    ZegoUIKitPrebuiltCallService.endCall()
                    Toast.makeText(this@MainActivity, "Call time limit reached (Backup Timer)", Toast.LENGTH_SHORT).show()

                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        showCallEndDialog()
                    }, 1000) } }
        }, (durationMinutes * 60 * 1000).toLong())

        callButton.performClick()
        Toast.makeText(this, "Call will end after $durationMinutes minutes", Toast.LENGTH_SHORT).show()
    }

    private fun showCallEndDialog() {
        val dialog = CallEndDialog(
            this@MainActivity,
            selectedCallDuration,
            currentBalance
        ) { newBalance ->
            currentBalance = newBalance
            refreshWalletDisplay()
        }
        dialog.show()
    }

    private fun addWalletCompose() {
        val walletContainer = findViewById<LinearLayout>(R.id.WalletContainer)
        walletContainer.addView(
            androidx.compose.ui.platform.ComposeView(this).apply {
                setContent {
                    WalletDisplay() } }) }

    private fun refreshWalletDisplay() {
        val walletContainer = findViewById<LinearLayout>(R.id.WalletContainer)
        walletContainer.removeAllViews()
        walletContainer.addView(
            androidx.compose.ui.platform.ComposeView(this).apply {
                setContent {
                    WalletDisplay() } }) }

    @Composable
    private fun WalletDisplay() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, top = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = "Wallet",
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "₹$currentBalance",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black) } }

    override fun onDestroy() {
        super.onDestroy()
        callTimer?.cancel()
    }
}
