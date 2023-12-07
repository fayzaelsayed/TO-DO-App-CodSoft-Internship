package com.example.todo.ui.edittask

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todo.database.TaskEntity
import com.example.todo.databinding.FragmentEditTaskBinding
import com.example.todo.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@AndroidEntryPoint
class EditTaskFragment : BaseFragment(false) {
    private lateinit var binding: FragmentEditTaskBinding
    private lateinit var args: EditTaskFragmentArgs
    private val viewModel: EditTaskViewModel by viewModels()
    private var setYear = 0
    private var setMonth = 0
    private var setDay = 0
    private var setHour = 0
    private var setMinute = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditTaskBinding.inflate(inflater, container, false)
        args = EditTaskFragmentArgs.fromBundle(requireArguments())
        fillOutTheViewsWithOldData()
        binding.ibDatePickerEdit.setOnClickListener {
            openDatePicker(binding.tvTaskDateEdit)
        }
        binding.ibTimePickerEdit.setOnClickListener {
            openTimePicker(binding.tvTaskTimeEdit)
        }
        binding.btnSaveTaskEdit.setOnClickListener {
            updateTask(args.task)
            findNavController().navigateUp()
        }
        binding.btnCancelEdit.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun fillOutTheViewsWithOldData() {
        binding.edtTaskTitleEdit.setText(args.task.taskTitle)
        binding.edtTaskDescriptionEdit.setText(args.task.taskDescription)
        binding.tvTaskDateEdit.text = args.task.taskDate
        binding.tvTaskTimeEdit.text = args.task.taskTime
    }

    private fun updateTask(taskEntity: TaskEntity) {
            taskEntity.taskTitle = binding.edtTaskTitleEdit.text.toString()
            taskEntity.taskDescription = binding.edtTaskDescriptionEdit.text.toString()
            taskEntity.taskDate = binding.tvTaskDateEdit.text.toString()
            taskEntity.taskTime = binding.tvTaskTimeEdit.text.toString()

        viewModel.updateTaskInDatabase(taskEntity)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openDatePicker(dateTextView: TextView) {
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
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
                Toast.makeText(requireContext(), "time unavailable", Toast.LENGTH_LONG).show()
            }

        }
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            timePickerDialogListener,
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }



}