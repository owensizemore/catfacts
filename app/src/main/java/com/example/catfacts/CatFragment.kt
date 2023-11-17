package com.example.catfacts

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.catfacts.Utils.Companion.TIP_REGEX_PATTERN
import com.example.catfacts.api.CatBreed
import com.example.catfacts.databinding.FragmentCatBinding

class CatFragment : Fragment() {
    private var _binding: FragmentCatBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCatBinding.inflate(inflater, container, false)

        val cat = arguments?.getParcelable("cat", CatBreed::class.java)
        if (cat != null) {
            binding.catBreed.text = cat.breed
            binding.catCountry.text = StringBuilder().append("Country: ").append(cat.country).toString()
            binding.catOrigin.text = StringBuilder().append("Origin: ").append(cat.origin).toString()
            binding.catCoat.text = StringBuilder().append("Coat: ").append(cat.coat).toString()
            binding.catPattern.text = StringBuilder().append("Pattern: ").append(cat.pattern).toString()
        }

        binding.editTextTip.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is being changed.
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has been changed.
                s?.let {
                    if (TIP_REGEX_PATTERN.matches(s) || s.isEmpty()) {
                        binding.tipWarning.visibility = View.INVISIBLE
                    } else {
                        binding.tipWarning.visibility = View.VISIBLE
                    }
                }
            }
        })

        binding.sendTipButton.setOnClickListener {
            validateTip(binding.editTextTip.text.toString())
        }

        return binding.root
    }

    private fun validateTip(tipText: String) {
        val toastText = if (tipText.isEmpty()) {
            "You need to specify a tip, silly!"
        } else {
            if (TIP_REGEX_PATTERN.matches(tipText)) {
                "Sent $tipText tip"
            } else {
                "Can't send invalid tip!"
            }
        }

        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
    }
}