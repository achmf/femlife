package com.example.femlife.ui.activities.pregnancy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.femlife.databinding.FragmentTrimesterBinding
import com.google.android.material.tabs.TabLayoutMediator

class TrimesterFragment : Fragment() {

    private var _binding: FragmentTrimesterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrimesterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trimester = arguments?.getInt(ARG_TRIMESTER) ?: 1
        val adapter = WeekPagerAdapter(this, trimester)
        binding.viewPagerWeeks.adapter = adapter

        TabLayoutMediator(binding.tabLayoutWeeks, binding.viewPagerWeeks) { tab, position ->
            tab.text = "Minggu ${adapter.getWeekNumber(position)}"
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TRIMESTER = "trimester"

        fun newInstance(trimester: Int): TrimesterFragment {
            return TrimesterFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TRIMESTER, trimester)
                }
            }
        }
    }
}

