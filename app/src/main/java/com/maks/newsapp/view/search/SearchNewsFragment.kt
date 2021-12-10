package com.maks.newsapp.view.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.maks.newsapp.adapter.NewsAdapter
import com.maks.newsapp.utils.EndlessScrollListener
import com.maks.newsapp.utils.QUERY_PAGE_SIZE
import com.maks.newsapp.utils.Resource
import com.maks.newsapp.utils.SEARCH_NEWS_TIME_DELAY
import com.maks.newsapp.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import maks.newsapp.R
import maks.newsapp.databinding.FragmentSearchNewsBinding

@AndroidEntryPoint
class SearchNewsFragment : Fragment() {

    private var _binding: FragmentSearchNewsBinding?= null
    private val binding get() = requireNotNull(_binding)

    val viewModel: NewsViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter
    private var job: Job? = null

    private val TAG = "SearchNewsFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchNewsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inistview()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inistview() = with(binding) {

        val rvSearchNews = view?.findViewById<RecyclerView>(R.id.rv_SearchNews)

        newsAdapter = NewsAdapter(requireContext())
        rvSearchNews?.apply {
            adapter = newsAdapter

            val scrollListener = object : EndlessScrollListener() {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    viewModel.getSearchNews(etSearch.text.toString())
                }
            }
            addOnScrollListener(scrollListener)
        }

        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) { viewModel.getSearchNews(editable.toString()) }
                }
            }
        }

        viewModel.newsData.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse?.articles?.toList())
                        val totalPagesResult = newsResponse!!.totalResult / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPagesResult

                        if (isLastPage) { rvSearchNews?.setPadding(0, 0, 0, 0) }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured $message")
                        Toast.makeText(context, "Error!! $message", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> { showProgressBar() }
            }
        })
    }

    private var isLoading = false
    private var isLastPage = false
    private fun showProgressBar() {
        val progressBarSearchnews = view?.findViewById<ProgressBar>(R.id.progressBar_searchnews)
        progressBarSearchnews?.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar() {
        val progressBarSearchnews = view?.findViewById<ProgressBar>(R.id.progressBar_searchnews)
        progressBarSearchnews?.visibility = View.INVISIBLE
        isLoading = false
    }
}