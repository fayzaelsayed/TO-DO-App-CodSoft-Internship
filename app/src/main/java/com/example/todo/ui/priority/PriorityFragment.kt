package com.example.todo.ui.priority

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.database.TaskEntity
import com.example.todo.databinding.FragmentPriorityBinding
import com.example.todo.ui.tasks.AllTasksAdapter
import com.example.todo.utils.Action
import com.example.todo.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PriorityFragment : BaseFragment(true) {
    lateinit var binding: FragmentPriorityBinding
    private val viewModel: PriorityViewModel by viewModels()
    private lateinit var highPriorityTasksAdapter: AllTasksAdapter
    private lateinit var highPriorityTasksList: MutableList<TaskEntity>
    private lateinit var todayTasks: List<TaskEntity>
    private lateinit var todoTasksList: List<TaskEntity>
    private lateinit var doneTasksList: List<TaskEntity>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPriorityBinding.inflate(inflater, container, false)
        highPriorityTasksList = ArrayList()
        todayTasks = ArrayList()
        todoTasksList = ArrayList()
        doneTasksList = ArrayList()
        setUpAdapter()

        viewModel.getAllHighPriorityTasksFromDatabase().observe(viewLifecycleOwner) {
            it?.let { tasks ->
                handleTasksLists(tasks)
            }
        }

        binding.ibFilterHighPriority.setOnClickListener {
            showDropDownMenuFilter()
        }
        return binding.root
    }

    private fun handleTasksLists(it: List<TaskEntity>) {
        highPriorityTasksList.clear()
        (todayTasks as ArrayList<TaskEntity>).clear()
        (todoTasksList as ArrayList<TaskEntity>).clear()
        (doneTasksList as ArrayList<TaskEntity>).clear()


        highPriorityTasksList.addAll(it)
        todayTasks = it.filter { it.taskDate == getCurrentDate() }
        todoTasksList = it.filter { it.taskType == "TODO" }
        doneTasksList = it.filter { it.taskType == "DONE" }

        val newList = ArrayList<TaskEntity>()
        val newTodayTasks = ArrayList<TaskEntity>()
        val newTodoList = ArrayList<TaskEntity>()
        val newDoneList = ArrayList<TaskEntity>()

        newList.addAll(highPriorityTasksList)
        newTodayTasks.addAll(todayTasks)
        newTodoList.addAll(todoTasksList)
        newDoneList.addAll(doneTasksList)

        highPriorityTasksAdapter.submitList(newList)

        if (highPriorityTasksList.isEmpty()) {
            binding.lottieAnimationView.visibility = View.VISIBLE
            binding.tvNoHighPriorityTasks.visibility = View.VISIBLE
            binding.tvHighPriorityList.visibility = View.GONE
            binding.ibFilterHighPriority.visibility = View.GONE
        } else {
            binding.lottieAnimationView.visibility = View.GONE
            binding.tvNoHighPriorityTasks.visibility = View.GONE
            binding.tvHighPriorityList.visibility = View.VISIBLE
            binding.ibFilterHighPriority.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDropDownMenuFilter() {
        val newList = ArrayList<TaskEntity>()
        val newTodayTasks = ArrayList<TaskEntity>()
        val newTodoList = ArrayList<TaskEntity>()
        val newDoneList = ArrayList<TaskEntity>()

        newList.addAll(highPriorityTasksList)
        newTodayTasks.addAll(todayTasks)
        newTodoList.addAll(todoTasksList)
        newDoneList.addAll(doneTasksList)
        val popupMenu = PopupMenu(requireContext(), binding.ibFilterHighPriority)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.all -> {
                    highPriorityTasksAdapter.submitList(newList)
                    binding.tvHighPriorityList.text = "All High Priority Tasks"
                    true
                }

                R.id.today -> {
                    highPriorityTasksAdapter.submitList(newTodayTasks)
                    binding.tvHighPriorityList.text = "Today's Due Tasks"
                    true
                }

                R.id.todo -> {
                    highPriorityTasksAdapter.submitList(newTodoList)
                    binding.tvHighPriorityList.text = "ToDo List"
                    true
                }
                R.id.done -> {
                    highPriorityTasksAdapter.submitList(newDoneList)
                    binding.tvHighPriorityList.text = "Done List"
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
        highPriorityTasksAdapter = AllTasksAdapter()
        binding.rvHighPriorityTasks.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.rvHighPriorityTasks.adapter = highPriorityTasksAdapter


        highPriorityTasksAdapter.setOnButtonClickListener(object :
            AllTasksAdapter.OnButtonClickListener {
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
                            PriorityFragmentDirections.actionPriorityFragmentToEditTaskFragment(
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
        val dialogLayout = layoutInflater.inflate(R.layout.delete_dialog, null)
        val yesButton = dialogLayout.findViewById<Button>(R.id.btn_yes_delete)
        val noButton = dialogLayout.findViewById<Button>(R.id.btn_no_delete)

        val builder = AlertDialog.Builder(requireContext()).setView(dialogLayout).create()

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