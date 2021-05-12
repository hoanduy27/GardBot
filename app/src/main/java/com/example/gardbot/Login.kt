package com.example.gardbot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.example.gardbot.ViewInfomation.ViewInfomationActivity
import com.example.gardbot.databinding.FragmentLoginBinding
import com.google.firebase.database.*
import com.google.firebase.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Login : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private lateinit var database: DatabaseReference


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            //TODO - not yet implemented: press back button 2 times to exit
        }
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?

    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("user")

        binding.btnLogin.setOnClickListener {
            validateLogin()
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.txtNoaccount.setOnClickListener{
            Log.e("CurrentFragment", this.toString())
            findNavController().navigate(R.id.action_login_to_signup)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun validateLogin(){
        val username = binding.txtboxUsername.text.toString()
        val password = binding.txtboxPassword.text.toString()
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var stringToast = if(snapshot.hasChild(username)){
                    if(isPasswordMatches(snapshot.child(username), password)){
                        "Đăng nhập thành công"
                    }
                    else{
                        "Sai mật khẩu"
                    }
                }
                else{
                    "Tài khoản không tồn tại"
                }
                Toast.makeText(context, stringToast, Toast.LENGTH_SHORT).show()
                if(stringToast == "Đăng nhập thành công"){
                    Log.e("he", activity.toString())
//                    val intent = Intent(activity, MainActivity::class.java)
                    val intent = Intent(activity, ViewInfomationActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun isPasswordMatches(userSnapshot: DataSnapshot, password: String) : Boolean{
        return (userSnapshot.child("password").value.toString() == password)
    }

    companion object{
        fun newInstance() : Login = Login()
    }
}