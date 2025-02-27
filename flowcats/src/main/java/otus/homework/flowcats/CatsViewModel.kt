package otus.homework.flowcats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CatsViewModel(
    private val catsRepository: CatsRepository
) : ViewModel() {
    private val _catsStateFlow = MutableStateFlow<Result<Fact?>>(Result.Initial())
    val catsStateFlow = _catsStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
                catsRepository.listenForCatFacts()
                    .catch { _catsStateFlow.value = Result.Error(it) }
                    .collect {
                        Log.d("MyLog","Result.Success")
                        _catsStateFlow.emit(Result.Success(it))
                    }
        }
    }
}

class CatsViewModelFactory(private val catsRepository: CatsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        CatsViewModel(catsRepository) as T
}