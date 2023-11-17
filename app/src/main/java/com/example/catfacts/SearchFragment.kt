package com.example.catfacts

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.catfacts.api.CatApi
import com.example.catfacts.api.CatBreed
import com.example.catfacts.databinding.FragmentSearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchFragment : Fragment() {
    @Inject
    lateinit var catApi: CatApi

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var catBreedsListAdapter: CatBreedsListAdapter

    private val catBreedsList: MutableList<CatBreed> = mutableListOf()
    private var filteredCatBreedsList: MutableList<CatBreed> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireContext().applicationContext as AppComponentContainer).injector().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                fetchCatBreedsList()
            }
            withContext(Dispatchers.Main) {
                catBreedsListAdapter = CatBreedsListAdapter(requireContext())
                binding.breedsList.adapter = catBreedsListAdapter
            }
        }

        binding.searchBreeds.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Not needed for this functionality
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Call the filter method whenever the search query changes
                filterCats(newText)
                return true
            }
        })

        return binding.root
    }

    private suspend fun fetchCatBreedsList() {
        catBreedsList.clear() // important for when navigating back to this fragment
        filteredCatBreedsList.clear()

        val breedsResponse = runCatching { catApi.getBreeds() }
        breedsResponse.onSuccess {
            catBreedsList.addAll(it.data)
            filteredCatBreedsList.addAll(catBreedsList)
        }.onFailure {
            Log.e("<SearchFragment>", "Exception while fetching list of all cat breeds: ${it.message}")
        }
    }

    private fun filterCats(query: String) {
        // clear filtered data list
        filteredCatBreedsList.clear()

        // If the query is empty, show all the data
        if (TextUtils.isEmpty(query)) {
            filteredCatBreedsList.addAll(catBreedsList)
        } else {
            for (cat in catBreedsList) {
                if (cat.breed.contains(query, ignoreCase = true)) {
                    filteredCatBreedsList.add(cat)
                }
            }
        }

        // Notify the adapter that the data has changed
        catBreedsListAdapter.notifyDataSetChanged()
    }

    fun onCatSelected(cat: CatBreed) {
        val catFragment = CatFragment()
        val bundle = Bundle().apply {
            putParcelable("cat", cat)
        }
        catFragment.arguments = bundle

        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, catFragment)
            addToBackStack(null)
            commit()
        }
    }

    private inner class CatBreedsListAdapter(context: Context): ArrayAdapter<CatBreed>(
        context,
        R.layout.cat_item,
        R.id.catBreed,
        filteredCatBreedsList
    ) {
        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            var view = convertView
            if (convertView == null) {
                view = View.inflate(context, R.layout.cat_item, null)
            }
            val cat = getItem(position)!!
            val catBreedTextView = view!!.findViewById<TextView>(R.id.catBreed)
            catBreedTextView.text = cat.breed
            view.setOnClickListener {
                onCatSelected(cat)
                //Toast.makeText(context, "Cat selected: ${cat.breed}", Toast.LENGTH_SHORT).show()
            }
            return view
        }
    }
}