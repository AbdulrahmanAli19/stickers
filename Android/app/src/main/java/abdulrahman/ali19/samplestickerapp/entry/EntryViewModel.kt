package abdulrahman.ali19.samplestickerapp.entry

import abdulrahman.ali19.samplestickerapp.StickerPack
import abdulrahman.ali19.samplestickerapp.StickerPackValidator
import abdulrahman.ali19.samplestickerapp.data.StickerPackLoader
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val stickerPackProvider: StickerPackLoader
) : ViewModel() {

    private val _stickers: MutableStateFlow<ArrayList<StickerPack>?> =
        MutableStateFlow(null)
    val stickers: StateFlow<ArrayList<StickerPack>?> = _stickers

    private val _error: MutableStateFlow<String> = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun loadStickers(context: Context) {
        viewModelScope.launch {
            var result: Pair<String?, ArrayList<StickerPack>>
            try {
                val stickerPackList: ArrayList<StickerPack> =
                    stickerPackProvider.fetchStickerPacks(context)

                if (stickerPackList.isEmpty()) {

                    result = Pair("could not find any packs", arrayListOf())

                } else {
                    for (stickerPack in stickerPackList) {
                        StickerPackValidator.verifyStickerPackValidity(context, stickerPack)
                    }

                    result = Pair(null, stickerPackList)

                }

            } catch (ex: Exception) {
                Log.e("EntryActivity", "error fetching sticker packs", ex)
                result = Pair(ex.message, arrayListOf())
            }
            withContext(Dispatchers.Main) {

                result.first?.let {
                    _error.value = it
                } ?: run {
                    _stickers.value = result.second
                }
            }
        }

    }
}
