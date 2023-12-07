package com.example.todo.ui.tasks

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.database.TaskEntity
import com.example.todo.databinding.FragmentAllTasksBinding
import com.example.todo.utils.Action
import com.example.todo.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AllTasksFragment : BaseFragment(true) {
    private lateinit var binding: FragmentAllTasksBinding
    private lateinit var allTasksAdapter: AllTasksAdapter
    private lateinit var allTasksList: MutableList<TaskEntity>
    private lateinit var todayTasks: List<TaskEntity>
    private lateinit var todoTasksList: List<TaskEntity>
    private lateinit var doneTasksList: List<TaskEntity>
    private val viewModel: AllTasksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAllTasksBinding.inflate(inflater, container, false)
        allTasksList = ArrayList()
        todayTasks = ArrayList()
        todoTasksList = ArrayList()
        doneTasksList = ArrayList()

        setUpAdapter()

        viewModel.getAllTasksFromDatabase().observe(viewLifecycleOwner) {
            it?.let { tasks ->
                handleTasksLists(tasks)
            }
        }
        binding.ibFilter.setOnClickListener {
            showDropDownMenuFilter()
        }
        return binding.root
    }


    private fun handleTasksLists(it: List<TaskEntity>) {
        allTasksList.clear()
        (todayTasks as ArrayList<TaskEntity>).clear()
        (todoTasksList as ArrayList<TaskEntity>).clear()
        (doneTasksList as ArrayList<TaskEntity>).clear()

        allTasksList.addAll(it)
        todayTasks = it.filter { it.taskDate == getCurrentDate() }
        todoTasksList = it.filter { it.taskType == "TODO" }
        doneTasksList = it.filter { it.taskType == "DONE" }

        val newList = ArrayList<TaskEntity>()
        val newTodayTasks = ArrayList<TaskEntity>()
        val newTodoList = ArrayList<TaskEntity>()
        val newDoneList = ArrayList<TaskEntity>()

        newList.addAll(allTasksList)
        newTodayTasks.addAll(todayTasks)
        newTodoList.addAll(todoTasksList)
        newDoneList.addAll(doneTasksList)

        allTasksAdapter.submitList(newList)

        if (allTasksList.isEmpty()) {
            binding.lottieAnimationView.visibility = View.VISIBLE
            binding.tvNoTasks.visibility = View.VISIBLE
            binding.tvDisplayedList.visibility = View.GONE
            binding.ibFilter.visibility = View.GONE
        } else {
            binding.lottieAnimationView.visibility = View.GONE
            binding.tvNoTasks.visibility = View.GONE
            binding.tvDisplayedList.visibility = View.VISIBLE
            binding.ibFilter.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDropDownMenuFilter() {
        val newList = ArrayList<TaskEntity>()
        val newTodayTasks = ArrayList<TaskEntity>()
        val newTodoList = ArrayList<TaskEntity>()
        val newDoneList = ArrayList<TaskEntity>()

        newList.addAll(allTasksList)
        newTodayTasks.addAll(todayTasks)
        newTodoList.addAll(todoTasksList)
        newDoneList.addAll(doneTasksList)
        val popupMenu = PopupMenu(requireContext(), binding.ibFilter)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.all -> {
                    allTasksAdapter.submitList(newList)
                    binding.tvDisplayedList.text = "All Tasks"
                    true
                }

                R.id.today -> {
                    allTasksAdapter.submitList(newTodayTasks)
                    binding.tvDisplayedList.text = "Today's Due Tasks"
                    true
                }

                R.id.todo -> {
                    allTasksAdapter.submitList(newTodoList)
                    binding.tvDisplayedList.text = "ToDo List"
                    true
                }
                R.id.done -> {
                    allTasksAdapter.submitList(newDoneList)
                    binding.tvDisplayedList.text = "Done List"
                    true
                }
                else -> {
                    false
                }
            }
        }
        popupMenu.inflate(R.menu.drop_down_menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        }
        popupMenu.show()
    }

    private fun setUpAdapter() {
        allTasksAdapter = AllTasksAdapter()
        binding.rvTasks.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.rvTasks.adapter = allTasksAdapter


        allTasksAdapter.setOnButtonClickListener(object : AllTasksAdapter.OnButtonClickListener {
            override fun onButtonClick(
                action: Action,
                taskEntity: TaskEntity
            ) {
                when (action) {
                    Action.TaskType -> {
                        updateTypeState(taskEntity)
                    }
                    Action.TaskPriority -> {
                        updatePriorityState(taskEntity)
                    }
                    Action.TaskDelete -> {
                        showDialog(taskEntity)
                    }
                    Action.TaskEdit -> {
                        findNavController().navigate(
                            AllTasksFragmentDirections.actionAllTasksFragmentToEditTaskFragment(
                                taskEntity
                            )
                        )
                    }
                }
            }

        })
    }

    private fun updateTypeState(taskEntity: TaskEntity) {
        if (taskEntity.taskType == "TODO") {
            viewModel.updateTaskType(taskEntity.id, "DONE")
        } else {
            viewModel.updateTaskType(taskEntity.id, "TODO")
        }
    }

    private fun updatePriorityState(taskEntity: TaskEntity) {
        if (taskEntity.taskPriority == "REGULAR") {
            viewModel.updateTaskPriority(taskEntity.id, "HIGH")
        } else {
            viewModel.updateTaskPriority(taskEntity.id, "REGULAR")
        }
    }

    private fun showDialog(taskEntity: TaskEntity) {
//        val dialogBuilder = AlertDialog.Builder(requireContext())
//        val inflater = LayoutInflater.from(requireContext())
//
//        val dialogLayout = inflater.inflate(R.layout.delete_dialog, null)
//        dialogBuilder.setView(dialogLayout)


        val builder = Dialog(requireContext())
        builder.setContentView(R.layout.delete_dialog)
        builder.window?.setLayout((resources.displayMetrics.widthPixels * 0.9).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.window?.setGravity(Gravity.CENTER)

        val yesButton = builder.findViewById<Button>(R.id.btn_yes_delete)
        val noButton = builder.findViewById<Button>(R.id.btn_no_delete)


        yesButton.setOnClickListener {
            viewModel.deleteTaskFromDatabase(taskEntity)
            builder.dismiss()
        }

        noButton.setOnClickListener {
            builder.dismiss()
        }

        builder.show()
    }

    private fun getCurrentDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd.MMMM yyyy", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

}