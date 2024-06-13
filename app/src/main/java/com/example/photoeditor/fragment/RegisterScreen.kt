package com.example.photoeditor.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.photoeditor.R
import com.example.photoeditor.room.Model.UserDetail
import com.example.photoeditor.viewModel.MainViewModel
import com.example.photoeditor.databinding.FragmentRegisterScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterScreen : Fragment() {

    private val binding by lazy { FragmentRegisterScreenBinding.inflate(layoutInflater) }

    lateinit var viewModel : MainViewModel
    private val list = ArrayList<UserDetail>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        observer()
        clickEvents()
        return binding.root
    }

    private fun clickEvents() {
        binding.signbtn.setOnClickListener {
            val email = binding.signemailET.text.toString()
            val password = binding.signpassET.text.toString()
            val fname = binding.signfirstnameET.text.toString()
            val lname = binding.signlastnameET.text.toString()

            val userData = UserDetail(
                email = email,
                password = password,
                firstName = fname,
                lastName = lname,
                id = 0
            )

            val data = list.find { (it.email.equals(email) || it.password.equals(password) )}
            if (data == null){
                if (email.isNotEmpty() && password.isNotEmpty() && fname.isNotEmpty() && lname.isNotEmpty()){
                    viewModel.insertUser(userData)
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.farmelayout, LoginScreen())?.commit()
                }

            }else Toast.makeText(requireContext(),"Invalid Credentials", Toast.LENGTH_LONG).show()
        }

        binding.signback.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.farmelayout, LoginScreen())?.commit()
        }

        binding.signlogin.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.farmelayout, LoginScreen())?.commit()
        }

        binding.signemailET.setOnFocusChangeListener{ _, focused ->
            val email = binding.signemailET.text.toString()
            if (!focused){
                if (email.length < 2 ){
                    binding.emaillContainer.helperText = "Minimum 2 characters"
                    binding.emaillContainer.boxStrokeColor = Color.RED
                }else{
                    binding.emaillContainer.helperText = null
                    binding.emaillContainer.boxStrokeColor = Color.parseColor("#4899EA")
                }
            }
        }

        binding.signpassET.setOnFocusChangeListener{ _, focused ->
            val pass = binding.signpassET.text.toString()
            if (!focused){
                if (pass.length < 2 ){
                    binding.passwordContainer.helperText = "Minimum 2 characters"
                    binding.passwordContainer.boxStrokeColor = Color.RED
                }else{
                    binding.passwordContainer.helperText = null
                    binding.passwordContainer.boxStrokeColor = Color.parseColor("#4899EA")
                }
            }
        }
        binding.signfirstnameET.setOnFocusChangeListener{ _, focused ->
            val fname = binding.signfirstnameET.text.toString()
            if (!focused){
                if (fname.length < 2 ){
                    binding.firstnameContainer.helperText = "Minimum 2 characters"
                    binding.firstnameContainer.boxStrokeColor = Color.RED
                }else{
                    binding.firstnameContainer.helperText = null
                    binding.firstnameContainer.boxStrokeColor = Color.parseColor("#4899EA")
                }
            }
        }
        binding.signlastnameET.setOnFocusChangeListener{ _, focused ->
            val lname = binding.signlastnameET.text.toString()
            if (!focused){
                if (lname.length < 2 ){
                    binding.lastnameContainer.helperText = "Minimum 2 characters"
                    binding.lastnameContainer.boxStrokeColor = Color.RED
                }else{
                    binding.lastnameContainer.helperText = null
                    binding.lastnameContainer.boxStrokeColor = Color.parseColor("#4899EA")
                }
            }
        }
    }

    private fun observer() {
        viewModel.getUser().observe(viewLifecycleOwner, Observer{
            list.clear()
            list.addAll(it)
        })
    }

}