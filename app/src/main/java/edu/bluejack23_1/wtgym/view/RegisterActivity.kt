package edu.bluejack23_1.wtgym.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.databinding.ActivityRegisterBinding
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import edu.bluejack23_1.wtgym.MainActivity
import edu.bluejack23_1.wtgym.viewmodel.RegisterViewModel
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    lateinit var datePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initDatePicker()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.setActivityReference(this)

        binding.dobInputLbl.text = getTodaysDate()

        binding.dobInputLbl.setOnClickListener {
            viewModel.onDatePickerClicked()
        }

        binding.registerBtn.setOnClickListener {
            viewModel.onRegisterClicked()
        }

        binding.alreadyHaveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        viewModel.nameError.observe(this, Observer { nameError ->
            if (nameError != null) {
                binding.lblError.text = nameError;
                binding.inputLblName.error = nameError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.inputLblName.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                binding.inputLblName.setBackgroundResource(R.drawable.edittext_fails)
            } else {
                binding.inputLblName.setBackgroundResource(R.drawable.edittext_roundedcorner)
            }
        })

        viewModel.emailError.observe(this, Observer { emailError ->
            if (emailError != null) {
                binding.lblError.text = emailError;
                binding.inputLblEmail.error = emailError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.inputLblEmail.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                binding.inputLblEmail.setBackgroundResource(R.drawable.edittext_fails)
            } else {
                binding.inputLblEmail.setBackgroundResource(R.drawable.edittext_roundedcorner)
            }
        })

        viewModel.passwordError.observe(this, Observer { passwordError ->
            if (passwordError != null) {
                binding.lblError.text = passwordError;
                binding.passwordInputLbl.error = passwordError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.passwordInputLbl.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                binding.passwordInputLbl.setBackgroundResource(R.drawable.edittext_fails)
            } else {
                binding.passwordInputLbl.setBackgroundResource(R.drawable.edittext_roundedcorner)
            }
        })

        viewModel.weightError.observe(this, Observer { weightError ->
            if (weightError != null) {
                binding.lblError.text = weightError;
                binding.weightInputLbl.error = weightError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.weightInputLbl.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                binding.weightInputLbl.setBackgroundResource(R.drawable.edittext_fails)
            } else {
                binding.weightInputLbl.setBackgroundResource(R.drawable.edittext_roundedcorner)
            }
        })

        viewModel.heightError.observe(this, Observer { heightError ->
            if (heightError != null) {
                binding.lblError.text = heightError;
                binding.heightInputLbl.error = heightError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.heightInputLbl.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                binding.heightInputLbl.setBackgroundResource(R.drawable.edittext_fails)
            } else {
                binding.heightInputLbl.setBackgroundResource(R.drawable.edittext_roundedcorner)
            }
        })

        viewModel.dobError.observe(this, Observer { dobError ->
            if (dobError != null) {
                binding.lblError.text = dobError;
                binding.dobInputLbl.error = dobError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.dobInputLbl.setPadding(paddingInPixels, 0, paddingInPixels, 0)

            } else {

            }
        })

        viewModel.navigateToLogin.observe(this, Observer { navigate ->
            if (navigate){
                val message = "Your registration has been successful!"

                // Use the root layout of your activity as the view for the Snackbar
                val rootView = findViewById<View>(android.R.id.content)

                // Create a Snackbar
                val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                snackbar.setBackgroundTint(Color.parseColor("#3A3B3C"))
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.setTextColor(Color.parseColor("#FFFFFF"))

                // Add an action to the Snackbar (optional)
                snackbar.setAction("Dismiss") {
                    // Handle the action click event here
                }
                // Show the Snackbar
                snackbar.show()

                Handler().postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }, 2500) // 3000 milliseconds (3 seconds)
            }
        })
    }

    private fun getTodaysDate(): CharSequence? {
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH) + 1
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)

        return makeDateString(day, month, year)
    }

    private fun initDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val month = month + 1
            val date = makeDateString(day, month, year)
            binding.dobInputLbl.text = date;
        }

        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)

        val style: Int = AlertDialog.THEME_HOLO_LIGHT
        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        var selectedMonth = getMonthFormat(month)
//        var monthString = month.toString()
        var dayString = day.toString()

//        if (monthString.length == 1) {
//            monthString = "0$monthString"
//        }

        if (dayString.length == 1) {
            dayString = "0$dayString"
        }

        return "$selectedMonth $dayString $year"
    }

    private fun getMonthFormat(month: Int): Any {
        if(month == 1)
            return "JAN"
        if(month == 2)
            return "FEB"
        if(month == 3)
            return "MAR"
        if(month == 4)
            return "APR"
        if(month == 5)
            return "MAY"
        if(month == 6)
            return "JUN"
        if(month == 7)
            return "JUL"
        if(month == 8)
            return "AUG"
        if(month == 9)
            return "SEP"
        if(month == 10)
            return "OCT"
        if(month == 11)
            return "NOV"
        if(month == 12)
            return "DEC"

        return "JAN"
    }
}