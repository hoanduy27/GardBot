package com.example.gardbot.auth

import android.content.ContentValues.TAG
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
import com.example.gardbot.DemoActivity
import com.example.gardbot.R
import com.example.gardbot.dashboard.DashboardActivity
import com.example.gardbot.databinding.FragmentLoginBinding
import com.google.firebase.database.*
import com.example.gardbot.model.Session
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Login : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
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
            /*findNavController().navigate(R.id.action_login_to_signup)*/
            findNavController().navigate(R.id.fragment_signup)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun validateLogin(){
        val username = binding.txtboxUsername.text.toString()
        val password = binding.txtboxPassword.text.toString()
        if(username.length == 0 || password.length == 0){
            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return;
        }
        auth = Firebase.auth
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(username)){
                    if(isPasswordMatches(snapshot.child(username), password)){
                        val email = snapshot.child(username).child("email").value.toString()
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    if(auth.currentUser!!.isEmailVerified) {
                                        Session.username = username
                                        Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(activity, DashboardActivity::class.java)
                                        startActivity(intent)
                                    }else{
                                        Toast.makeText(context, "Vui lòng xác thực email", Toast.LENGTH_SHORT).show()
                                    }
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success")

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                                    Toast.makeText(context, "Cơ sở dữ liệu bị lỗi", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    else{
                        Toast.makeText(context, "Sai mật khẩu", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(context, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show()
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