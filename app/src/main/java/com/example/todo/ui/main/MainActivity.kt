package com.example.todo.ui.main

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.todo.R
import com.example.todo.database.TaskEntity
import com.example.todo.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration
    private val viewModel: MainViewModel by viewModels()
    private lateinit var dialog: BottomSheetDialog
    private var setYear = 0
    private var setMonth = 0
    private var setDay = 0
    private var setHour = 0
    private var setMinute = 0


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfig = AppBarConfiguration(
            setOf(
                R.id.allTasksFragment,
                R.id.priorityFragment
            )
        )
        binding.fab.setOnClickListener {
            showBottomSheet()

        }

        binding.chipAppBar.setItemSelected(R.id.allTasksFragment, true)

        binding.chipAppBar.setOnItemSelectedListener { id ->
            when (id) {
                R.id.allTasksFragment -> navController.navigate(R.id.allTasksFragment)
                R.id.priorityFragment -> navController.navigate(R.id.priorityFragment)
            }
        }
        binding.bottomAppBar.setupWithNavController(navController, appBarConfig)

    }

    fun isBottomAppBarAndActionBarVisible(isVisible: Boolean) {
        if (isVisible) {
            binding.bottomAppBar.visibility = View.VISIBLE
            binding.fab.visibility = View.VISIBLE
        } else {
            binding.bottomAppBar.visibility = View.GONE
            binding.fab.visibility = View.GONE
        }
    }

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
        dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        dialog.setContentView(dialogView)
        val taskTitleEditText = dialogView.findViewById<TextInputEditText>(R.id.edt_task_title)
        val taskTitleInputLayout = dialogView.findViewById<TextInputLayout>(R.id.tv_task_title)
        val taskDescriptionInputLayout = dialogView.findViewById<TextInputLayout>(R.id.tv_task_description)
        val taskDescriptionEditText =
            dialogView.findViewById<TextInputEditText>(R.id.edt_task_description)
        val datePickerButton = dialogView.findViewById<ImageButton>(R.id.ib_date_picker)
        val timePickerButton = dialogView.findViewById<ImageButton>(R.id.ib_time_picker)
        val taskDateTextView = dialogView.findViewById<TextView>(R.id.tv_task_date)
        val taskTimeTextView = dialogView.findViewById<TextView>(R.id.tv_task_time)
        val addButton = dialogView.findViewById<Button>(R.id.btn_save_task)
        val cancelButton = dialogView.findViewById<Button>(R.id.btn_cancel)

        taskTitleEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                taskTitleInputLayout.isErrorEnabled = false
            }else{
                taskTitleInputLayout.validateInput(taskTitleEditText.text.toString())
            }
        }

        taskDescriptionEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                taskDescriptionInputLayout.isErrorEnabled = false
            }else{
                taskDescriptionInputLayout.validateInput(taskDescriptionEditText.text.toString())
            }
        }

        datePickerButton.setOnClickListener {
            openDatePicker(taskDateTextView)
            taskDateTextView.visibility = View.VISIBLE
        }

        timePickerButton.setOnClickListener {
            openTimePicker(taskTimeTextView)
            taskTimeTextView.visibility = View.VISIBLE
        }

        fun validateDateAndTime(): Boolean {
            var isOk = false
            if (taskDateTextView.text.isNullOrBlank()) {
                Toast.makeText(this, "The date of the task is required", Toast.LENGTH_LONG)
                    .show()
            } else if (taskTimeTextView.text.isNullOrBlank()) {
                Toast.makeText(this, "The time of the task is required", Toast.LENGTH_LONG)
                    .show()
            } else {
                isOk = true
            }
            return isOk
        }

        addButton.setOnClickListener {
            val task = TaskEntity(
                id = 0,
                taskTitle = taskTitleEditText.text.toString(),
                taskDescription = taskDescriptionEditText.text.toString(),
                taskDate = taskDateTextView.text.toString(),
                taskTime = taskTimeTextView.text.toString(),
                taskType = "TODO",
                taskPriority = "REGULAR"
            )
            val validTitle = taskTitleInputLayout.validateInput(taskTitleEditText.text.toString())
            val validDescription = taskDescriptionInputLayout.validateInput(taskDescriptionEditText.text.toString())
            if (validTitle && validDescription && validateDateAndTime()) {
                viewModel.addTaskToDatabase(task)
                dialog.dismiss()
            }
        }
        dialog.show()

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun TextInputLayout.validateInput(text: String?) : Boolean{
        return if (text.isNullOrBlank()){
            this.error = "Required"
            this.isErrorEnabled = true
            false
        }else{
            this.isErrorEnabled = false
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openDatePicker(dateTextView: TextView) {
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, mYear, mMonth, mDay ->
                try {
                    val formatter = DateTimeFormatter.ofPattern("dd.MMMM yyyy", Locale.getDefault())
                    val date: LocalDate = LocalDate.of(mYear, mMonth + 1, mDay)
                    val formattedDate = date.format(formatter)
                    dateTextView.text = formattedDate.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                setYear = mYear
                setMonth = mMonth
                setDay = mDay
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openTimePicker(timeTextView: TextView) {
        val calender = Calendar.getInstance()
        val timePickerDialogListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calender.set(
                setYear,
                setMonth,
                setDay,
                hour,
                minute
            )
            setHour = hour
            setMinute = minute
            if (calender.timeInMillis >= Calendar.getInstance().timeInMillis) {
                timeTextView.text = SimpleDateFormat("HH:mm a").format(calender.time)

            } else {
                Toast.makeText(this, "time unavailable", Toast.LENGTH_LONG).show()
            }

        }
        val timePickerDialog = TimePickerDialog(
            this,
            timePickerDialogListener,
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }


}

