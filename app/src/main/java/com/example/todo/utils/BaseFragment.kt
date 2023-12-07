package com.example.todo.utils

import androidx.fragment.app.Fragment
import com.example.todo.ui.main.MainActivity

abstract class BaseFragment(private val isVisible: Boolean): Fragment() {
    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).isBottomAppBarAndActionBarVisible(isVisible)
    }

}