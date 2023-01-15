package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.election.TAG
import com.example.android.politicalpreparedness.network.models.Address
import com.google.android.gms.location.LocationServices
import android.provider.Settings
import com.google.android.gms.location.FusedLocationProviderClient
import java.util.*


const val TAG_R = "RepresentativeFragment"
class RepresentativeFragment: Fragment() , AdapterView.OnItemSelectedListener {
    private lateinit var repViewModel: RepresentativeViewModel
    private lateinit var binding: FragmentRepresentativeBinding
    private val permissionId = 2
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i(TAG_R, "Inside onCreateView")
        binding = FragmentRepresentativeBinding.inflate(inflater)

        binding.lifecycleOwner = this
        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(
            this.requireActivity())


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

        Log.i(TAG_R, "Setting the listOfRepresentatives Observer")
        repViewModel.listOfRepresentatives.observe(viewLifecycleOwner,
            Observer {
                Log.i(TAG_R, "Inside the RepresentativeViewModel.listOfRepresentatives.observe")
                if (!it.isNullOrEmpty() ) {
                    Log.i(TAG, "assigning the instantiated RepresentativeViewModel")
                    binding.repviewmodel = repViewModel
                    binding.representativeListTitle.text = "My Representatives"
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

        binding.buttonLocation.setOnClickListener {
            Log.i(TAG_R, "Inside USE MY LOCATION Button click listener")
            val address = retrieveAddressFromLocationDetection()
            repViewModel.searchMyRepresentative(address.line1 ?: "",
                                                        address.line2?:"",
                                                        address.city?: "",
                                                        address.state?:"",
                                                        address.zip?:"")
        }

        return binding.root

    }

    private fun retrieveAddressFromLocationDetection() : Address {
        var locationBasedAddress = Address("","","","","")
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity())) { task ->
                    val location: Location? = task.result
//                    if (location != null) {
//                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
//                        val addressList =
//                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
//                        if ((addressList != null && addressList.size > 0)) {
//                            val geoAddress = addressList.get(0)
//                            val line1 = StringBuilder()
//                            for (i in 0 until geoAddress.maxAddressLineIndex) {
//                                line1.append(geoAddress.getAddressLine(i)).append("")
//                            }
//                            locationBasedAddress.line1 = line1.toString()
//                            Log.i(TAG_R,"Address Line 1:${locationBasedAddress.line1}")
//                            Log.i(TAG_R, "LOCALITY: ${geoAddress.locality}")
//                            Log.i(TAG_R, "PostalCode: ${geoAddress.postalCode}")
//                            locationBasedAddress.zip = geoAddress.postalCode
//                        }
//                    }
                }
            }else {
                Toast.makeText(requireContext(),
                    "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        }else {
            requestPermissions()
        }

        return locationBasedAddress
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
           val stateString = parent?.getItemAtPosition(pos).toString()
           binding.inputaddress?.state = stateString

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                                        locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

}