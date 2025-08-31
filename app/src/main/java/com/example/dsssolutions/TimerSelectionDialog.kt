package com.example.dsssolutions

import android.app.AlertDialog
import android.content.Context
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.LinearLayout
import android.graphics.Color

class TimerSelectionDialog(
    private val context: Context,
    private val onTimerSelected: (Int) -> Unit
) {

    fun show() {
        val radioGroup = RadioGroup(context)
        val timerOptions = listOf(1,5, 10, 15)

        timerOptions.forEachIndexed { index, minutes ->
            val radioButton = RadioButton(context).apply {
                text = "$minutes minutes"
                id = index
                textSize = 16f
                setTextColor(Color.BLACK)
                setPadding(0, 20, 0, 20)
            }
            radioGroup.addView(radioButton)

            if (index == 0) {
                radioButton.isChecked = true
            } }

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
            addView(radioGroup)
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle("Select Call Duration")
            .setMessage("Choose how long you want the call to last")
            .setView(layout)
            .setPositiveButton("Start Call") { _, _ ->
                val selectedId = radioGroup.checkedRadioButtonId
                if (selectedId != -1) {
                    val selectedMinutes = timerOptions[selectedId]
                    onTimerSelected(selectedMinutes)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .create()

        dialog.show() } }
