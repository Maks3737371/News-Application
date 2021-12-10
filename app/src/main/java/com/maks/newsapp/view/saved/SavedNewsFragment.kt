package com.maks.newsapp.view.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.maks.newsapp.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import maks.newsapp.R

@AndroidEntryPoint
class SavedNewsFragment : Fragment() {
    val viewModel: NewsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saved_news, container,false)
    }
}