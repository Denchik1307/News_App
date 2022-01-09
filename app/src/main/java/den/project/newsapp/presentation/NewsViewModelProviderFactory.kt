package den.project.newsapp.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import den.project.newsapp.repository.NewsRepository

class NewsViewModelProviderFactory(
    val app: Application,
    private val newsRepository: NewsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app, newsRepository) as T
    }
}
