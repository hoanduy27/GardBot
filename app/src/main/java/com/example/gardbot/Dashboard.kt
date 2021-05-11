package com.example.gardbot

import Adapters.Selection
import Adapters.SelectionAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.example.gardbot.databinding.FragmentDashboardBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import model.Session

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class Dashboard : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private var sysList : ArrayList<Selection> = ArrayList()
    private lateinit var systemAdapter : SelectionAdapter
    private lateinit var database: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference("wateringSystem")
        //Add to list
        addSystem()
        systemAdapter = SelectionAdapter(sysList, activity)
        //Fill gridview
        binding.sysList.adapter = systemAdapter
        //Change background to alert background: red
        alertSystem()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun addSystem(){
        //TODO: Read from firebase
        sysList.add(Selection("Owner", "HT1", "GB0001"))
        sysList.add(Selection("Owner", "HT2", "GB0002"))

    }
    fun alertSystem(){
        //TODO (not yet completed): Change background to red
        binding.sysList.adapter.getView(1, null, binding.sysList).setBackgroundResource(R.color.alert)
    }
    companion object{
        fun newInstance(): Dashboard = Dashboard()
    }
}