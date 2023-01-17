package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.example.android.politicalpreparedness.databinding.FragmentRegisteredAddressBinding
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.launch.LaunchFragmentDirections
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.representative.TAG_R
import com.example.android.politicalpreparedness.util.checkAddressGoodForSearch


const val TAG4 = "RegisteredAddressFragment"
class RegisteredAddressFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentRegisteredAddressBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i(TAG4, "Inside onCreateView")
        binding = FragmentRegisteredAddressBinding.inflate(inflater)
        binding.lifecycleOwner = this


        //Setting up the spinner Adapter and binding
        val stateSpinner = binding.state
        stateSpinner.onItemSelectedListener = this
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(
            requireNotNull(this.activity),
            R.array.states,
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

         val viewModelFactory = RegisteredAddressViewModelFactory (datasource, application)
        //Create ViewModel
        val regAddressViewModel = ViewModelProvider(this, viewModelFactory).
                                                    get(RegisteredAddressViewModel::class.java)

        regAddressViewModel.registeredAddress.observe(viewLifecycleOwner, Observer {
                Log.i(TAG3, "Inside  regAddressViewModel.registeredAddress.observe")
                if(it != null) {
                    if(!regAddressViewModel.registeredAddress.value?.line1.isNullOrEmpty()) {
                        binding.addressLine1.setText(
                            regAddressViewModel.registeredAddress.value?.line1 ?: "")
                        Log.i(TAG_R, "Show Line 1")
                    }
                    if(!regAddressViewModel.registeredAddress.value?.city.isNullOrEmpty()) {
                        binding.city.setText(
                            regAddressViewModel.registeredAddress.value?.city ?: "")
                        Log.i(TAG_R, "Show city")
                    }
                    if(!regAddressViewModel.registeredAddress.value?.zip.isNullOrEmpty()) {
                        binding.zip.setText(
                            regAddressViewModel.registeredAddress.value?.zip?: "")
                        Log.i(TAG_R, "Show zip")
                    }
                    if(!regAddressViewModel.registeredAddress.value?.line2.isNullOrEmpty()) {
                        binding.addressLine2.setText(
                            regAddressViewModel.registeredAddress.value?.line2?: "")
                        Log.i(TAG_R, "Show Line 2")
                    }
                    if(!regAddressViewModel.registeredAddress.value?.state.isNullOrEmpty()) {
                        Log.i(TAG_R,"Inside state attribute set , state is not empty or null")
                        val stateSelection = regAddressViewModel.registeredAddress.value?.state
                        val spinnerPosition: Int = adapter.getPosition(stateSelection)
                        stateSpinner.setSelection(spinnerPosition)
                        Log.i(
                            TAG_R, "StateSelection:${stateSelection}, " +
                                "spinnerPosition:${spinnerPosition}, " +
                                "stateSpinner.Selection:${stateSpinner.selectedItem}")
                    }
                }
        })


        binding.buttonSave.setOnClickListener {
            Log.i(TAG_R, "Inside SAVE Button click listener")

            val address = Address(binding.addressLine1.text.toString(),
                                    binding.addressLine2.text.toString(),
                                    binding.city.text.toString(),
                                    binding.state.getSelectedItem().toString(),
                                    binding.zip.text.toString())

            val isAddresGood = checkAddressGoodForSearch(address)
            if (isAddresGood) {
                regAddressViewModel.resetAddressIsIncomplete()

                regAddressViewModel.saveAddress(address)
            } else {
                regAddressViewModel.addressIsIncomplete()
            }

        }

        binding.buttonDone.setOnClickListener {
            Log.i(TAG_R, "Inside DONE Button click listener")
//                this.findNavController().navigate(RegisteredAddressFragmentDirections.actionRegisteredAddressFragmentToVoterInfoFragment())

        }

        Log.i(TAG4, "Exiting OnCreateView")
        return binding.root

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val stateString = parent?.getItemAtPosition(pos).toString()
        binding.inputaddress?.state = stateString

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}