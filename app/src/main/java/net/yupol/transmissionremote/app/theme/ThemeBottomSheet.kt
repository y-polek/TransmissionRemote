package net.yupol.transmissionremote.app.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.databinding.ThemeBottomsheetBinding

@AndroidEntryPoint
class ThemeBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: ThemeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ThemeBottomsheetBinding.inflate(
            inflater.cloneInContext(ContextThemeWrapper(activity, R.style.AppTheme)),
            container,
            false
        )
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.radio_off -> NightMode.OFF
                R.id.radio_on -> NightMode.ON
                else -> NightMode.AUTO
            }
            viewModel.onNightModeSelected(mode)
        }

        viewModel.nightMode.observe(viewLifecycleOwner) { nightMode ->
            nightMode ?: return@observe
            when (nightMode) {
                NightMode.OFF -> binding.radioOff.isChecked = true
                NightMode.ON -> binding.radioOn.isChecked = true
                NightMode.AUTO -> binding.radioDeviceSettings.isChecked = true
            }
        }

        return binding.root
    }

    companion object {
        const val TAG = "ThemeBottomSheet"
        fun newInstance(): ThemeBottomSheet = ThemeBottomSheet()
    }
}
