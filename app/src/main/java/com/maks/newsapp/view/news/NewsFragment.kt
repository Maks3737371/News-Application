package com.maks.newsapp.view.news

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.maks.newsapp.adapter.NewsAdapter
import com.maks.newsapp.utils.Resource
import com.maks.newsapp.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import maks.newsapp.R
import maks.newsapp.databinding.FragmentNewsBinding

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val viewModel: NewsViewModel by viewModels()
    private val binding get() = requireNotNull(_binding)
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentNewsBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initsview()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initsview() = with(binding) {
        newsAdapter = NewsAdapter(requireContext())
        rvNews.apply {
            setHasFixedSize(true)
            adapter = newsAdapter
            setupobserver()
        }
    }

    private fun setupobserver() = with(binding) {
        viewModel.newsData.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarStatus(false)
                    tryAgainStatus(false)
                    newsAdapter.differ.submitList(response.data!!.articles)
                    rvNews.adapter = newsAdapter
                }
                is Resource.Error -> {
                    tryAgainStatus(true, response.message!!)
                    progressBarStatus(false)
                }
                is Resource.Loading -> {
                    tryAgainStatus(false)
                    progressBarStatus(true)
                }
            }
        })
        swiperefershnews()
    }

    private fun swiperefershnews() {
        val swiperefreshnews = view?.findViewById<SwipeRefreshLayout>(R.id.swipereferesh_news)
        swiperefreshnews?.setOnRefreshListener {
            viewModel.getNews()
            swiperefreshnews.isRefreshing = false
        }
    }

    private fun tryAgainStatus(status: Boolean, message: String = "message") {
        if (status) {
            binding.tryAgainMessage.text = message
            binding.tryAgainLayout.visibility = VISIBLE
            binding.tryAgainLayout.setBackgroundColor(Color.WHITE)

        } else {
            binding.tryAgainLayout.visibility = GONE
        }
    }

    
    private fun progressBarStatus(status: Boolean) {
        val progressBarNews = view?.findViewById<ProgressBar>(R.id.progressBar_news)
        progressBarNews?.visibility = if (status) VISIBLE else GONE

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        lifecycleScope.launchWhenStarted {
            val isChecked = viewModel.getUIMode.first()
            val uiMode = menu.findItem(R.id.action_night_mode)
            uiMode.isChecked = isChecked
            setUIMode(uiMode, isChecked)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_night_mode -> {
                item.isChecked = !item.isChecked
                setUIMode(item, item.isChecked)
                true
            }

            R.id.searchMenuItem -> {
                findNavController().navigate(
                    R.id.action_newsFragment_to_searchnewsFragment
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUIMode(item: MenuItem, isChecked: Boolean) {
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            viewModel.saveToDataStore(true)
            item.setIcon(R.drawable.ic_night)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            viewModel.saveToDataStore(false)
            item.setIcon(R.drawable.ic_day)
        }
    }

    //override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentNewsBinding.inflate(inflater, container, false)
}