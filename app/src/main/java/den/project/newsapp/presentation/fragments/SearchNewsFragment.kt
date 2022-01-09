package den.project.newsapp.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import den.project.newsapp.R
import den.project.newsapp.databinding.SearchNewsFragmentBinding
import den.project.newsapp.presentation.NewsActivity
import den.project.newsapp.presentation.NewsViewModel
import den.project.newsapp.presentation.adapters.NewsAdapter
import den.project.newsapp.utils.Constants
import den.project.newsapp.utils.Constants.Companion.DELAY_FOR_SEARCH_NEWS
import den.project.newsapp.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.search_news_fragment) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: SearchNewsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SearchNewsFragmentBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("singleNewsUrl", it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_singleNewsFragment,
                bundle
            )
        }

        var job: Job? = null
        binding.searchNews.addTextChangedListener { inputText ->
            job?.cancel()
            job = MainScope().launch {
                delay(DELAY_FOR_SEARCH_NEWS)
                inputText?.let {
                    if (inputText.toString().isNotEmpty()) {
                        viewModel.searchInAllNews(inputText.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer
        { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if (isLastPage){
                            binding.recyclerSearchNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity,"Error: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        isLoading = false

    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount


            val isNotLoadingAndNotLastPage = !isLoading && isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shoudPaginate = isNotLoadingAndNotLastPage
                    && isAtLastItem
                    && isNotAtBeginning
                    && isTotalMoreThanVisible
                    && isScrolling
            if (shoudPaginate) {
                viewModel.searchInAllNews(binding.searchNews.text.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.recyclerSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }
}