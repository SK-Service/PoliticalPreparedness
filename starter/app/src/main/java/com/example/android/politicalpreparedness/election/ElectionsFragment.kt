package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment: Fragment() {

    private val electionViewModel: ElectionsViewModel by lazy {
            ViewModelProvider(this).get(ElectionsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i("ElectionsFragment", "Inside onCreateView")
        val binding = FragmentElectionBinding.inflate(inflater)

        binding.lifecycleOwner = this

        //Get hold of the application context
        val application = requireNotNull(this.activity).application
        //Get hold of the datasource
        val datasource = ElectionDatabase.getInstance(application).electionDao
        //Get hold of the ViewModelFactory
        val viewModelFactory = ElectionsViewModelFactory (datasource, application)
        //Get hold of the ViewModel
        val electionViewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)

        Log.i("ElectionsFragment", "Setting the listOfElection Observer")
        electionViewModel.listOfElection.observe(viewLifecycleOwner,
            Observer {
                Log.i(TAG, "Inside the ElectionViewModel.listOfElectionObserver")
                if (it != null ) {
                    Log.i(TAG, "assigning the instantiated ElectionViewModel")
                    binding.electionViewModel = electionViewModel
                }
            })

        electionViewModel.listOfSavedElections.observe(viewLifecycleOwner,
            Observer {
                Log.i(TAG, "Inside the ElectionViewModel.listOfSavedElections")
                if (it != null ) {
                    Log.i(TAG, "assigning the instantiated ElectionViewModel")
                    binding.electionViewModel = electionViewModel
                }
            })

        electionViewModel.navigateToVoterInfo.observe(viewLifecycleOwner,
                Observer {
                    Log.i(TAG, "Inside the electionViewModel.navigateToVoterInfo.observe")
                    if(it != null) {
                        Log.i(TAG, "Election is selected and not null")
                        this.findNavController().navigate(ElectionsFragmentDirections.
                                        actionElectionsFragmentToVoterInfoFragment(it))
                        Log.i(TAG, "After call to Navigate")
                        electionViewModel.displayVoterInfoComplete()
                        Log.i(TAG, "display voter info complete is called and set to null")
                    }
                })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        electionViewModel.retrieveElectionsFromRepos()
    }

}