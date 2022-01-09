package den.project.newsapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import den.project.newsapp.R
import den.project.newsapp.databinding.FavoriteNewsFragmentBinding
import den.project.newsapp.presentation.NewsActivity
import den.project.newsapp.presentation.NewsViewModel
import den.project.newsapp.presentation.adapters.NewsAdapter

class FavoriteNewsFragment : Fragment(R.layout.favorite_news_fragment) {

    lateinit var viewModel: NewsViewModel
    lateinit var binding: FavoriteNewsFragmentBinding
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FavoriteNewsFragmentBinding.inflate(inflater)
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
                R.id.action_favoriteNewsFragment_to_singleNewsFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object :ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val news = newsAdapter.differ.currentList[position]
                viewModel.deleteNews(news)
                Snackbar.make(view,"deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("undelete"){
                        viewModel.saveNews(news)
                    }
                }.show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerFavoriteNews)
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer {news->
            newsAdapter.differ.submitList(news)
        })
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.recyclerFavoriteNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}



