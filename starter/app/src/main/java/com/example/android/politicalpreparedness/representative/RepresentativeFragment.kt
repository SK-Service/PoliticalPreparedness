package com.example.android.politicalpreparedness.representative

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.election.ElectionsFragmentDirections
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.ElectionsViewModelFactory
import com.example.android.politicalpreparedness.election.TAG
import com.example.android.politicalpreparedness.network.models.Address
import kotlinx.android.synthetic.main.fragment_representative.*
import kotlinx.android.synthetic.main.fragment_voter_info.*
import java.util.Locale

const val TAG_R = "RepresentativeFragment"
class RepresentativeFragment: Fragment() , AdapterView.OnItemSelectedListener {
    private lateinit var repViewModel: RepresentativeViewModel
    private lateinit var binding: FragmentRepresentativeBinding
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i(TAG_R, "Inside onCreateView")
        binding = FragmentRepresentativeBinding.inflate(inflater)

        binding.lifecycleOwner = this

        //Setting up the spinner Adapter and binding
        val stateSpinner = binding.state
        stateSpinner.onItemSelectedListener = this
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(
            requireNotNull(this.activity),
            R.array.usa_state,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            stateSpinner.adapter = adapter
        }

        //TODO: Add ViewModel values and create ViewModel
        //Get hold of the application context
        val application = requireNotNull(this.activity).application
        //Get hold of the datasource
        val datasource = ElectionDatabase.getInstance(application).electionDao
        //Get hold of the ViewModelFactory
        val viewModelFactory = RepresentativeViewModelFactory (datasource, application)
        //Get hold of the ViewModel
        repViewModel = ViewModelProvider(this, viewModelFactory).get(RepresentativeViewModel::class.java)

        binding.repviewmodel = repViewModel
        binding.inputaddress = Address("","","","","")

        Log.i(TAG_R, "Setting the listOfElection Observer")
        repViewModel.listOfRepresentatives.observe(viewLifecycleOwner,
            Observer {
                Log.i(TAG_R, "Inside the RepresentativeViewModel.listOfRepresentatives.observe")
                if (it != null ) {
                    Log.i(TAG, "assigning the instantiated RepresentativeViewModel")
                    binding.repviewmodel = repViewModel
                }
            })

        repViewModel.addressEnteredFlag.observe(viewLifecycleOwner,
            Observer {
                Log.i(TAG_R, "Inside addressEnteredFlag.observe")
                if ( it == false ) {
                    binding.message.text = "Please enter at least a valid USA zip for search to work"
                }
            })

        repViewModel.civicRepAPICallStatus.observe(viewLifecycleOwner,
            Observer {
                Log.i(TAG_R, "Inside civicRepAPICallStatus.observe")
                if ( it == RepresentativeCivicApiStatus.ERROR ) {
                    binding.message.text = "We are not able to handle your request at this time"
                } else if (it == RepresentativeCivicApiStatus.DONE) {
                    binding.message.text = ""
                } else if (it == RepresentativeCivicApiStatus.LOADING) {
//                    TODO - implement loading image when the status is loading
                }
            })

        repViewModel.addressCacheAvailableFlag.observe(viewLifecycleOwner,
            Observer {
                Log.i(TAG_R, "Inside addressCacheAvailableFlag.observe")
                if ( it == true ) {
                    if(!repViewModel.address.value?.line1.isNullOrEmpty()) {
                        binding.addressLine1.setText(repViewModel.address.value?.line1 ?: "")
                        Log.i(TAG_R, "Show Line 1")
                    }
                    if(!repViewModel.address.value?.city.isNullOrEmpty()) {
                        binding.city.setText(repViewModel.address.value?.city ?: "")
                        Log.i(TAG_R, "Show city")
                    }
                    if(!repViewModel.address.value?.zip.isNullOrEmpty()) {
                        binding.zip.setText(repViewModel.address.value?.zip?: "")
                        Log.i(TAG_R, "Show zip")
                    }
                    if(!repViewModel.address.value?.line2.isNullOrEmpty()) {
                        binding.addressLine2.setText(repViewModel.address.value?.line2?: "")
                        Log.i(TAG_R, "Show Line 2")
                    }
                    if(!repViewModel.address.value?.state.isNullOrEmpty()) {
                        val stateSelection = repViewModel.address.value?.state
                        val spinnerPosition: Int = adapter.getPosition(stateSelection)
                        stateSpinner.setSelection(spinnerPosition)
                        Log.i(TAG_R, "StateSelection:${stateSelection}, " +
                                "spinnerPosition:${spinnerPosition}, " +
                                "stateSpinner.Selection:${stateSpinner.selectedItem}")
                    }
                }
            })


        return binding.root

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
           val stateString = parent?.getItemAtPosition(pos).toString()
           binding.inputaddress?.state = stateString

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}