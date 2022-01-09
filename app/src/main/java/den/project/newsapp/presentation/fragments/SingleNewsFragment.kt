package den.project.newsapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import den.project.newsapp.R
import den.project.newsapp.databinding.SingleNewsFragmentBinding
import den.project.newsapp.presentation.NewsActivity
import den.project.newsapp.presentation.NewsViewModel

class SingleNewsFragment : Fragment(R.layout.single_news_fragment) {

    lateinit var viewModel: NewsViewModel
    lateinit var binding: SingleNewsFragmentBinding
    val args: SingleNewsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SingleNewsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

        val singleNews = args.singleNewsUrl
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(singleNews.url)
        }
    }
}