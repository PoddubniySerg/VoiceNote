package com.test.voicenote.presentation.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.test.voicenote.R
import com.test.voicenote.databinding.FragmentHomeBinding
import com.test.voicenote.presentation.ui.adapters.VoicesAdapter
import com.test.voicenote.presentation.viewmodels.HomeViewModel
import com.test.voicenote.utils.LoadStates
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment @Inject constructor() :
    BindFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    companion object {
        private val REQUEST_PERMISSIONS = buildList {
            add(Manifest.permission.RECORD_AUDIO)
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    private lateinit var launcher: ActivityResultLauncher<Array<String>>
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initLauncher()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setVoicesAdapter()
        setCollectors()
        setListeners()
        viewModel.getSavedVoices()
    }

    private fun setVoicesAdapter() {
        binding.voices.adapter = VoicesAdapter(viewLifecycleOwner.lifecycleScope, requireContext())
    }

    private fun setCollectors() {
        changeRecordState()
        handleLoadState()
        handleVoiceList()
    }

    private fun setListeners() {
        binding.recordButton.setOnClickListener { checkPermissions() }
    }

    private fun initLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsMap ->
                if (permissionsMap.values.all { it }) {
                    viewModel.recordOnPressed()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "permission is not Granted",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }

    private fun changeRecordState() {
        viewModel.recorderStateFlow.onEach { isRecordStarted ->
            if (isRecordStarted) {
                setButtonIcon(R.drawable.stop_record_icon)
            } else {
                setButtonIcon(R.drawable.start_record_icon)
                getNameDialog()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleLoadState() {
        viewModel.loadStateFlow.onEach { loadState ->
            binding.progressBar.isVisible = loadState == LoadStates.LOADING
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleVoiceList() {
        viewModel.voiceFlow.onEach { list ->
            (binding.voices.adapter as VoicesAdapter).submitList(list)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setButtonIcon(id: Int) {
        binding.recordButton.setImageIcon(Icon.createWithResource(requireContext(), id))
    }

    private fun getNameDialog() {
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.filters = arrayOf(InputFilter.LengthFilter(25))
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(R.string.get_name_dialog_title)
        dialogBuilder.setView(input)
        dialogBuilder.setPositiveButton(R.string.get_name_dialog_positive_button_title) { _, _ ->
            viewModel.saveVoice(input.text.toString())
        }
        dialogBuilder.setNegativeButton(R.string.get_name_dialog_negative_button_title) { dialog, _ ->
            dialog.cancel()
        }
        dialogBuilder.show()
    }

    private fun checkPermissions() {
        val isAllGranted = REQUEST_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (isAllGranted) viewModel.recordOnPressed()
        else launcher.launch(REQUEST_PERMISSIONS)
    }
}