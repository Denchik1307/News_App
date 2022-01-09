package den.project.newsapp.repository

import den.project.newsapp.api.RetrofitInstance
import den.project.newsapp.db.ArticleDataBase
import retrofit2.Retrofit

class NewsRepository(
    val db: ArticleDataBase
) {
    suspend fun getAllNewsRep(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getAllNews(countryCode, pageNumber)

    suspend fun searchNewsRep(searchQery:String,pageNumber: Int)=
        RetrofitInstance.api.searchNews(searchQery,pageNumber)
}