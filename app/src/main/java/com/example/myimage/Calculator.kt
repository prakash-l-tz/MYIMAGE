package com.example.myimage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Calculator : AppCompatActivity() {

    private lateinit var screen: TextView
    private lateinit var vault: TextView

    private lateinit var results: TextView
    private lateinit var on: Button

    private var firstNum = 0.0
    private var operation = ""
    private var isCalculatorOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        screen = findViewById(R.id.screen)
        results = findViewById(R.id.results)
        on = findViewById(R.id.on)
        vault = findViewById(R.id.vault)


        turnCalculatorOff()
        vault.setOnLongClickListener(OnLongClickListener { v: View? ->
            val intent: Intent = Intent(this@Calculator, PinActivity::class.java)
            startActivity(intent)
            true // VERY IMPORTANT
        })

        // ON / OFF
        on.setOnClickListener { toggleCalculator() }

        // Number buttons
        val numbers = intArrayOf(
            R.id.num0, R.id.num1, R.id.num2, R.id.num3, R.id.num4,
            R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9,
            R.id.num00
        )

        for (id in numbers) {
            findViewById<Button>(id).setOnClickListener {
                appendNumber((it as Button).text.toString())
            }
        }

        // Dot
        findViewById<Button>(R.id.dot).setOnClickListener { appendDot() }

        // Operators
        findViewById<Button>(R.id.plus).setOnClickListener { selectOperation("+") }
        findViewById<Button>(R.id.minus).setOnClickListener { selectOperation("-") }
        findViewById<Button>(R.id.multiply).setOnClickListener { selectOperation("X") }
        findViewById<Button>(R.id.divide).setOnClickListener { selectOperation("/") }

        // AC, DEL, =
        findViewById<Button>(R.id.ac).setOnClickListener { clearAll() }
        findViewById<Button>(R.id.del).setOnClickListener { deleteLast() }
        findViewById<Button>(R.id.equal).setOnClickListener { calculate() }
    }

    private fun toggleCalculator() {
        if (isCalculatorOn) turnCalculatorOff() else turnCalculatorOn()
    }

    private fun turnCalculatorOn() {
        isCalculatorOn = true
        screen.visibility = View.VISIBLE
        results.visibility = View.VISIBLE
        on.text = "OFF"
        setButtonsEnabled(true)
        clearAll()
    }

    private fun turnCalculatorOff() {
        isCalculatorOn = false
        screen.visibility = View.GONE
        results.visibility = View.GONE
        on.text = "ON"
        setButtonsEnabled(false)
        clearAll()
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        val ids = intArrayOf(
            R.id.num0, R.id.num1, R.id.num2, R.id.num3, R.id.num4,
            R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9,
            R.id.num00, R.id.dot, R.id.plus, R.id.minus,
            R.id.multiply, R.id.divide, R.id.equal,
            R.id.ac, R.id.del
        )
        for (id in ids) findViewById<Button>(id).isEnabled = enabled
    }

    private fun appendNumber(num: String) {
        if (isCalculatorOn) screen.append(num)
    }

    private fun appendDot() {
        if (!isCalculatorOn) return
        val parts = screen.text.toString().split(" ")
        if (!parts.last().contains(".")) screen.append(".")
    }

    private fun selectOperation(op: String) {
        val text = screen.text.toString()
        if (!isCalculatorOn || text.isEmpty() ||
            text.contains(" + ") || text.contains(" - ")
            || text.contains(" X ") || text.contains(" / ")
        ) return

        firstNum = text.toDouble()
        operation = op
        screen.text = "$text $op "
    }

    private fun calculate() {
        val parts = screen.text.toString().split(" ")
        if (!isCalculatorOn || parts.size != 3) return

        val secondNum = parts[2].toDoubleOrNull() ?: return
        val result = when (operation) {
            "+" -> firstNum + secondNum
            "-" -> firstNum - secondNum
            "X" -> firstNum * secondNum
            "/" -> if (secondNum == 0.0) return results.setText("Error") else firstNum / secondNum
            else -> 0.0
        }

        results.text = result.toString()
        screen.text = result.toString()
        firstNum = result
        operation = ""
    }

    private fun clearAll() {
        screen.text = ""
        results.text = ""
        firstNum = 0.0
        operation = ""
    }

    private fun deleteLast() {
        if (isCalculatorOn && screen.text.isNotEmpty()) {
            screen.text = screen.text.dropLast(1)
        }
    }
}
