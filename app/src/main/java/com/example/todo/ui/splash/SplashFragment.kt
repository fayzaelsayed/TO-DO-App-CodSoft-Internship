package com.example.todo.ui.splash

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.databinding.FragmentSplashBinding
import com.example.todo.utils.BaseFragment


class SplashFragment : BaseFragment(false) {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var firstText: Animation
    private lateinit var secondText: Animation
    private lateinit var lottieView: Animation
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        firstText = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_from_right)
        secondText = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_from_left)
        lottieView = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_from_bottom)



        Handler().postDelayed({
            binding.lottieAnimationView.visibility = View.VISIBLE
            binding.lottieAnimationView.animation = lottieView
            Handler().postDelayed({
                binding.tvToDoList.visibility = View.VISIBLE
                binding.tvSchedulePlanner.visibility = View.VISIBLE
                binding.tvToDoList.startAnimation(secondText)
                binding.tvSchedulePlanner.startAnimation(firstText)
            }, 500)
        }, 500)

        Handler().postDelayed({
            findNavController().navigate(R.id.action_splashFragment_to_allTasksFragment)
        }, 4000)


        return binding.root
    }
}