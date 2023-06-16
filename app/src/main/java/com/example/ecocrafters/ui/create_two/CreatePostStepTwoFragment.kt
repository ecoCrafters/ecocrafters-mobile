package com.example.ecocrafters.ui.create_two

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.IngredientsResponse
import com.example.ecocrafters.databinding.FragmentCreatePostStepTwoBinding
import com.example.ecocrafters.ui.adapter.IngredientsAdapter
import com.example.ecocrafters.ui.create_one.CreatePostViewModel
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch

class CreatePostStepTwoFragment : Fragment(), View.OnClickListener {

    private var binding: FragmentCreatePostStepTwoBinding? = null
    private val viewModel: CreatePostViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostStepTwoBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startSubscription()

        lifecycleScope.launch {
            viewModel.updateAllIngredients()
        }

        binding?.apply {
            btnBackCreateTwo.setOnClickListener(this@CreatePostStepTwoFragment)
            btnNextCreateTwo.setOnClickListener(this@CreatePostStepTwoFragment)
            rvIngredientsCreate.layoutManager = LinearLayoutManager(requireContext())
            inputIngredientsCreate.setEndIconOnClickListener {
                if (!edIngredientsCreate.text.isNullOrBlank()) {
                    viewModel.addPostIngredients(edIngredientsCreate.text.toString())
                    edIngredientsCreate.text.clear()
                }
            }
            edIngredientsCreate.setOnItemClickListener { parent, view, position, id ->
                val item = edIngredientsCreate.adapter.getItem(position)
                viewModel.addPostIngredients(item as String)
                edIngredientsCreate.text.clear()
            }
            edIngredientsCreate.threshold = 2
        }
    }


    private fun startSubscription() {
        lifecycleScope.launch {
            viewModel.ingredientsListState.collect {
                renderResult(it)
            }
        }
        lifecycleScope.launch {
            viewModel.postIngredientsState.collect {postIngredients ->
                Log.d("this", postIngredients.toString())
                val rvAdapter = IngredientsAdapter().apply {
                    setOnRemoveCallback {ingredient ->
                        viewModel.removePostIngredients(ingredient)
                    }
                }
                binding?.rvIngredientsCreate?.apply{
                    adapter = rvAdapter
                }
                rvAdapter.submitList(postIngredients)
            }
        }
    }

    private fun renderResult(result: ResultOf<List<IngredientsResponse>>?) {
        when (result) {
            is ResultOf.Success -> {
                binding?.apply {
                    edIngredientsCreate.setAdapter(
                        IngredientsAutoCompleteAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            result.data
                        )
                    )
                }
            }

            is ResultOf.Error -> {
                requireContext().showToast(result.error)
            }

            else -> {}
        }
    }

    override fun onClick(v: View?) {
        binding?.apply {
            when (v?.id) {
                btnBackCreateTwo.id -> {
                    findNavController().popBackStack()
                }
                btnNextCreateTwo.id -> {
                    findNavController().navigate(R.id.navigation_create_three)
                }
            }
        }
    }
}

private class IngredientsAutoCompleteAdapter(
    context: Context?,
    textViewResourceId: Int,
    private val ingredientsList: List<IngredientsResponse>
) :
    ArrayAdapter<String>(context!!, textViewResourceId), Filterable {

    private var resultList: List<String>? = null
    override fun getCount(): Int {
        return resultList?.size ?: 0
    }

    override fun getItem(index: Int): String {
        return resultList?.get(index) ?: ""
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    resultList = ingredientsList.map {
                        it.name
                    }.filter {
                        it.contains(constraint.toString())
                    }

                    filterResults.values = resultList
                    filterResults.count = resultList?.size ?: 0
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }

        }
    }
}