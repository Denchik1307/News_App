package den.project.newsapp.repository

import den.project.newsapp.api.RetrofitInstance
import den.project.newsapp.db.ArticleDataBase
import den.project.newsapp.models.Article
import retrofit2.Retrofit

class NewsRepository(
    val db: ArticleDataBase
) {
    suspend fun getAllNewsRep(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getAllNews(countryCode, pageNumber)

    suspend fun searchNewsRep(searchQery:String,pageNumber: Int)=
        RetrofitInstance.api.searchNews(searchQery,pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticles(article)
}