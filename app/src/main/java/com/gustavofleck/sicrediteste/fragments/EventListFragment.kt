package com.gustavofleck.sicrediteste.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.gustavofleck.domain.models.SimplifiedEvent
import com.gustavofleck.presentation.viewmodels.EventListViewModel
import com.gustavofleck.presentation.viewstates.EventListViewState
import com.gustavofleck.sicrediteste.adapters.EventListAdapter
import com.gustavofleck.sicrediteste.databinding.EventListFragmentBinding
import com.gustavofleck.sicrediteste.enums.ErrorsEnum
import com.gustavofleck.sicrediteste.enums.ErrorsEnum.CONNECTION_ERROR
import com.gustavofleck.sicrediteste.enums.ErrorsEnum.GENERIC_ERROR
import com.gustavofleck.sicrediteste.handlers.DialogHandler
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EventListFragment : Fragment() {

    private lateinit var binding: EventListFragmentBinding
    private lateinit var adapter: EventListAdapter
    private val dialogHandler: DialogHandler by inject()
    private val viewModel: EventListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EventListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewState()
        setupAdapter()
        viewModel.eventList()
    }


    private fun observeViewState() {
        viewModel.viewStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EventListViewState.Loading -> handleProgressBar(isLoading = true)
                is EventListViewState.Success -> handleSuccessState(state.eventList)
                is EventListViewState.ConnectionError -> handleConnectionErrorState()
                is EventListViewState.GenericError -> handleGenericErrorState()
            }
        }
    }

    private fun setupAdapter() {
        adapter = EventListAdapter { eventId -> navigateToEventDetails(eventId) }
        binding.eventList.adapter = adapter
    }

    private fun navigateToEventDetails(eventId: String) {
        val action = EventListFragmentDirections.actionToEventDetails(eventId)
        findNavController().navigate(action)

    }

    private fun handleSuccessState(eventList: List<SimplifiedEvent>) {
        handleProgressBar(isLoading = false)
        adapter.submitList(eventList)
    }

    private fun handleConnectionErrorState() {
        handleProgressBar(isLoading = false)
        dialogHandler.showErrorDialog(CONNECTION_ERROR, requireContext())
    }

    private fun handleGenericErrorState() {
        handleProgressBar(isLoading = false)
        dialogHandler.showErrorDialog(GENERIC_ERROR, requireContext())
    }

    private fun handleProgressBar(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

}