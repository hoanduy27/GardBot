package com.example.gardbot.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gardbot.dashboard.DashboardActivity
import com.example.gardbot.databinding.FragmentSignupBinding
import com.example.gardbot.model.User
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class SignUp : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private lateinit var userDatabase: DatabaseReference
    private lateinit var sysDatabase: DatabaseReference
    private  lateinit var usr: User
    private lateinit var auth: FirebaseAuth
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userDatabase = FirebaseDatabase.getInstance().getReference("user")
        sysDatabase = FirebaseDatabase.getInstance().getReference("wateringSystem").child("ws0001").child("operator")
        binding.btnLogin.setOnClickListener{
            validateSignUp()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun validateSignUp(){
        val username = binding.txtboxUsername.text.toString()
        val password = binding.txtboxPassword.text.toString()
        val email = binding.txtboxEmail.text.toString()
        val lName = binding.txtboxLname.text.toString()
        val fName = binding.txtboxFname.text.toString()
        val confirmPassword = binding.txtboxRePassword.text.toString()
        val phNbr = binding.txtboxPhoneNum.text.toString()
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var check = ""
                if (username != "" && password != "" && email != "" &&
                    lName != "" && fName != "" && confirmPassword != "" && phNbr != ""
                ) {
                        for (account in snapshot.getChildren()) {
                            check = checkExistance(account,username,phNbr,email)
                            if(check != "Valid") break;
                        }
                        if(check == "Valid"){
                            if(password == confirmPassword) {
                                Log.d(TAG, "Valid register")
                                doSignUp(username, password, email, lName, fName, phNbr)
                                return;
                            }
                            else Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context, check, Toast.LENGTH_SHORT).show()
                        }

                } else {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun checkExistance(account: DataSnapshot, username: String, phone: String, email: String): String{
        if(account.key.toString() == username) return "Tên đăng nhập đã tồn tại"
        else if(account.child("email").value.toString() == email) return "Địa chỉ email đã tồn tại"
        else if(account.child("phoneNumber").value.toString() == phone) return "Số điện thoại đã tồn tại"
        else return "Valid"
    }

    private  fun doSignUp(username: String, password: String, email: String, lName: String, fName: String, phNbr: String){
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {  task->
                if(task.isSuccessful){
                    auth.currentUser!!.sendEmailVerification().addOnCompleteListener{
                        task -> if (task.isSuccessful) {
                            Log.d(TAG, "Email sent.")
                        usr =  User(email,fName,lName,password,phNbr)
                        userDatabase.child(username).setValue(usr)
                        sysDatabase.child(username).setValue("0")
                        Toast.makeText(context, "Đăng kí thành công. Vui lòng xác nhận email để hoàn tất.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, AuthActivity::class.java)
                        startActivity(intent)
                        }
                        else{
                        Log.d(TAG, "Fail to send email.")
                        }
                    }
                }else{
                    Toast.makeText(context, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }

        }
    }
    companion object {
        fun newInstance(): SignUp = SignUp()
    }
}

