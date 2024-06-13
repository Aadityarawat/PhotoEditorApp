package com.example.photoeditor.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.photoeditor.dashBoard.DashboardScreen
import com.example.photoeditor.R
import com.example.photoeditor.room.Model.UserDetail
import com.example.photoeditor.viewModel.MainViewModel
import com.example.photoeditor.databinding.FragmentLoginScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginScreen : Fragment() {

    private val binding by lazy { FragmentLoginScreenBinding.inflate(layoutInflater) }

    lateinit var viewModel : MainViewModel
    private var list = ArrayList<UserDetail>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        observer()
        clickEvents()

    }

    private fun clickEvents() {
        binding.logsign.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.farmelayout, RegisterScreen())?.commit()
        }
        binding.loginbutton.setOnClickListener {
            val email = binding.logemail.text.toString()
            val password = binding.logpass.text.toString()

            val user = list.find { it.email == email && it.password == password }
            Log.d("TAGG","${user?.email}")
            if (user != null){
                saveData()
                val intent = Intent(requireContext(), DashboardScreen::class.java)
                startActivity(intent)
                activity?.finish()
            }else{
                Toast.makeText(requireContext(),"InValid Details", Toast.LENGTH_LONG).show()
            }

        }

        binding.logemail.setOnFocusChangeListener{ _, focused ->
            val email = binding.logemail.text.toString()
            if (!focused){
                if (email.length < 2 ){
                    binding.emailContainer.helperText = "Minimum 2 characters"
                    binding.emailContainer.boxStrokeColor = Color.RED
                }else{
                    binding.emailContainer.helperText = null
                    binding.emailContainer.boxStrokeColor = Color.parseColor("#4899EA")
                }
            }
        }

        binding.logpass.setOnFocusChangeListener{ _, focused ->
            val pass = binding.logpass.text.toString()
            if (!focused){
                if (pass.length < 2 ){
                    binding.passContainer.helperText = "Minimum 2 characters"
                    binding.passContainer.boxStrokeColor = Color.RED
                }else{
                    binding.passContainer.helperText = null
                    binding.passContainer.boxStrokeColor = Color.parseColor("#4899EA")
                }
            }
        }
    }

    private fun saveData() {
        val sharedpref = requireActivity().getSharedPreferences("shared",MODE_PRIVATE)
        val edit = sharedpref.edit()
        edit.putString("login","success")
        edit.apply()
    }

    private fun observer() {
        viewModel.getUser().observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()){
                list.clear()
                list.addAll(it)
                Log.d("emailList", "${list}")
            }
        }
    }
}