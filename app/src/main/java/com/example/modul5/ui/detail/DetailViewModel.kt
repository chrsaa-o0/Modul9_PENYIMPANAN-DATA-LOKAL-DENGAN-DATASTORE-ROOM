package com.example.modul5.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.modul5.data.model.Student
import com.example.modul5.data.repository.FavoriteRepository
import com.example.modul5.data.repository.StudentRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val studentRepository: StudentRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var favoriteJob: Job? = null

    fun loadStudent(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val student = studentRepository.getStudent(id)
                if (student != null) {
                    _uiState.update { it.copy(isLoading = false, student = student) }
                    observeFavorite(id)
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Data mahasiswa tidak ditemukan") }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Data tidak tersedia"
                    )
                }
            }
        }
    }

    private fun observeFavorite(id: Int) {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            favoriteRepository.observeIsFavorite(id).collect { value ->
                _uiState.update { it.copy(isFavorite = value) }
            }
        }
    }

    fun toggleFavorite() {
        val student = _uiState.value.student ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                favoriteRepository.remove(student.id)
            } else {
                favoriteRepository.add(student)
            }
        }
    }

    companion object {
        fun factory(
            studentRepository: StudentRepository,
            favoriteRepository: FavoriteRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DetailViewModel(studentRepository, favoriteRepository) as T
                }
            }
        }
    }
}