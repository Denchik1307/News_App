package den.project.newsapp.repository

import den.project.newsapp.api.RetrofitInstance
import den.project.newsapp.db.ArticleDataBase

class NewsRepository(
    val db: ArticleDataBase
) {
    suspend fun getAllNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getAllNews(countryCode, pageNumber)

}