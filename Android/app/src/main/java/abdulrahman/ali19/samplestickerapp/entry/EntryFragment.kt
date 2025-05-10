package abdulrahman.ali19.samplestickerapp.entry

import abdulrahman.ali19.samplestickerapp.StickerPack
import abdulrahman.ali19.samplestickerapp.StickerPackDetailsActivity
import abdulrahman.ali19.samplestickerapp.StickerPackListActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.samplestickerapp.R
import com.example.samplestickerapp.databinding.FragmentEntryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EntryFragment : Fragment() {

    private val viewModel: EntryViewModel by viewModels()

    private var _binding: FragmentEntryBinding? = null
    private val binding: FragmentEntryBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadStickers(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.error.collect { showErrorMessage(it) }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.stickers.collect { it?.let { showStickerPack(it) } }
            }
        }
    }

    private fun showErrorMessage(errorMessage: String) {
        binding.entryActivityProgress.visibility = View.GONE
        binding.errorMessage.text = getString(R.string.error_message, errorMessage)
        Log.e("EntryActivity", "error fetching sticker packs, $errorMessage")
    }

    private fun showStickerPack(stickerPackList: ArrayList<StickerPack>) {
        binding.entryActivityProgress.visibility = View.GONE
        if (stickerPackList.size > 1) {
            val intent = Intent(requireActivity(), StickerPackListActivity::class.java)
            intent.putParcelableArrayListExtra(
                StickerPackListActivity.EXTRA_STICKER_PACK_LIST_DATA,
                stickerPackList
            )
            startActivity(intent)
            requireActivity().finish()
        } else {
            val intent = Intent(requireActivity(), StickerPackDetailsActivity::class.java)
            intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, false)
            intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, stickerPackList[0])
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
