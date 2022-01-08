package den.project.newsapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import den.project.newsapp.R
import den.project.newsapp.databinding.FavoriteNewsFragmentBinding
import den.project.newsapp.presentation.NewsActivity
import den.project.newsapp.presentation.NewsViewModel

class FavoriteNewsFragment : Fragment(R.layout.favorite_news_fragment) {

    lateinit var viewModel: NewsViewModel
    lateinit var binding: FavoriteNewsFragmentBinding

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
    }
}