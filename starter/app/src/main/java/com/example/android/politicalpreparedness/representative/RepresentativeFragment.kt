package com.example.android.politicalpreparedness.representative

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.election.ElectionsFragmentDirections
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.ElectionsViewModelFactory
import com.example.android.politicalpreparedness.election.TAG
import com.example.android.politicalpreparedness.network.models.Address
import java.util.Locale

const val TAG_R = "RepresentativeFragment"
class RepresentativeFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i(TAG_R, "Inside onCreateView")
        val binding = FragmentRepresentativeBinding.inflate(inflater)

        binding.lifecycleOwner = this

        //TODO: Add ViewModel values and create ViewModel
        //Get hold of the application context
        val application = requireNotNull(this.activity).application
        //Get hold of the datasource
        val datasource = ElectionDatabase.getInstance(application).electionDao
        //Get hold of the ViewModelFactory
        val viewModelFactory = RepresentativeViewModelFactory (datasource, application)
        //Get hold of the ViewModel
        val repViewModel = ViewModelProvider(this, viewModelFactory).get(RepresentativeViewModel::class.java)

//        binding.electionViewModel = electionViewModel
        Log.i(TAG_R, "Setting the listOfElection Observer")
        repViewModel.listOfRepresentatives.observe(viewLifecycleOwner,
            Observer {
                Log.i(TAG, "Inside the RepresentativeViewModel.listOfRepresentatives.observe")
                if (it != null ) {
                    Log.i(TAG, "assigning the instantiated RepresentativeViewModel")
                    binding.repviewmodel = repViewModel
                }
            })

        return binding.root

    }

}