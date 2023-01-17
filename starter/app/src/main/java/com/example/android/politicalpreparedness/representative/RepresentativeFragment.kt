package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
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
    private var requestPermissionCount: Int = 0
    private  var locationBasedAddress = Address ("","","","","")

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i(TAG_R, "Inside onCreateView")
        binding = FragmentRepresentativeBinding.inflate(inflater)

        binding.lifecycleOwner = this
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(
            this.requireActivity())


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
                } else if (it == RepresentativeCivicApiStatus.ERROR_404) {
                    binding.message.text = "The address doesn't seem to be correct"
                }else if (it == RepresentativeCivicApiStatus.DONE) {
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
                        Log.i(TAG_R,"Inside state attribute set , state is not empty or null")
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
            repViewModel.setRepButtonClickedTypeToLocation()
            repViewModel.resetRepresentativeList()
            repViewModel.resetLocationAddressRetrievalComplete()
            retrieveAddressFromLocationDetection()
           // repViewModel.locationPermissionNotGranted()

        }

        binding.buttonSearch.setOnClickListener {
            Log.i(TAG_R, "Inside Search By Address click listener")
            repViewModel.setRepButtonClickedTypeToManual()
            repViewModel.resetRepresentativeList()
            repViewModel.resetLocationAddressRetrievalComplete()
            val address = Address(binding.addressLine1.text.toString(),
                                binding.addressLine2.text.toString(),
                                binding.city.text.toString(),
                                binding.state.getSelectedItem().toString(),
                                binding.zip.text.toString())
            val isAddresGood = repViewModel.checkAddressGoodForSearch(address)
            if (isAddresGood) {
                repViewModel.resetAddressIsIncomplete()
                repViewModel.searchMyRepresentative(address)
                repViewModel.saveAddress(address)
            } else {
                repViewModel.addressIsIncomplete()
            }

        }

        repViewModel.locationAddressRetrievalCompleteFlag.observe(viewLifecycleOwner,
            Observer{
                  if (repViewModel.locationAddressRetrievalCompleteFlag.value == true) {
                      Log.i(TAG_R, "inside setOnClickListener - zip: ${locationBasedAddress.zip}")
                      Log.i(TAG_R, "inside setOnClickListener - city: ${locationBasedAddress.city}")
                      Log.i(TAG_R, "inside setOnClickListener - line1: ${locationBasedAddress.line1}")
                      if (locationBasedAddress.zip.isNullOrEmpty() ||
                          (locationBasedAddress.city.isNullOrEmpty() &&
                                  locationBasedAddress.line1.isNullOrEmpty())) {

                          binding.message.text =
                              "Failed to retrieve enough address details for using this service"
                          repViewModel.addressIsIncomplete()
                          repViewModel.resetAddressCacheAvailableFlag()

                      } else {
                          binding.message.text = ""

                          repViewModel.searchMyRepresentative(locationBasedAddress)
                          repViewModel.saveAddress(locationBasedAddress)
                          repViewModel.resetAddressIsIncomplete()
                          repViewModel.setAddressCacheAvailableFlag()
                      }
                      repViewModel.resetLocationAddressRetrievalComplete()
                  }
            })

//        repViewModel.locationPermissionGranted.observe(viewLifecycleOwner, Observer {
//            run {
//                Log.i(TAG_R, "inside locationPermissionGranted.observe")
//                if (it == false &&
//                    repViewModel.repButtonClickedType.value.equals("LOCATION") &&
//                    requestPermissionCount != 0
//                ) {
//                    retrieveAddressFromLocationDetection()
//                }
//            }
//        })
        return binding.root

    }
    
    private fun retrieveAddressFromLocationDetection(){
        Log.i(TAG_R, "Inside retrieveAddressFromLocationDetection")
        if (isLocationEnabled()) {
            Log.i(TAG_R, "Location services are enabled")
//                repViewModel.locationPermissionGranted()
            if (checkPermissions()) {
                Log.i(TAG_R, "Permission Check Successful - call retrieve location address")
                retrieveLocationAddress()
            }else {
                Log.i(TAG_R, "Location services enabled but no permission granted - request permission")
                requestPermissions()
//            repViewModel.locationPermissionNotGranted()
                Log.i(TAG_R, "After requesting permission - retrieveLocationAddress")
                retrieveLocationAddress()
            }

        }else {
            Log.i(TAG_R, "Location Service is not enabled")
            Log.i(TAG_R, "Showing a toast message to turn location services")
            Toast.makeText(requireContext(),
                "Please turn on location and then click to use your location",
                Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            Log.i(TAG_R, "Start Setting Activity")
            startActivity(intent)
            //repViewModel.locationPermissionNotGranted()
            Log.i(TAG_R, "Second call for checkPermissions() after setting start activity")
            if (!checkPermissions()) {
                Log.i(TAG_R, "After Location Services Permission set in the Settings")
                requestPermissions()
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun retrieveLocationAddress() {
        if (checkPermissions() && (isLocationEnabled())) {
            mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                val location: Location? = task.result
                Log.i(TAG_R, "FusedLocationClient - addOnCompleteListener")
                if (location != null) {
                    Log.i(
                        TAG_R, "location is not nul, LATITUDE:" +
                                "${location.latitude}; LONGITUDE:${location.longitude}"
                    )
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addressList =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if ((!addressList.isNullOrEmpty() && addressList.size > 0)) {
                        Log.i(TAG_R, "Address List from Location is not empty")
                        val geoAddress = addressList.get(0)
                        val line1 = StringBuilder()
                        for (i in 0 until geoAddress.maxAddressLineIndex) {
                            line1.append(geoAddress.getAddressLine(i)).append("")
                        }

                        Log.i(TAG_R, "Geocode-Address Line 1:${line1.toString()}")
                        Log.i(TAG_R, "Geocode-LOCALITY: ${geoAddress.locality}")
                        Log.i(TAG_R, "Geocode-PostalCode: ${geoAddress.postalCode}")
                        Log.i(TAG_R, "Geocode- adminArea${geoAddress.adminArea}")
                        locationBasedAddress.zip = geoAddress.postalCode ?: ""
                        locationBasedAddress.line1 = line1.toString() ?: ""
                        locationBasedAddress.city = geoAddress.locality ?: ""
                        locationBasedAddress.state = geoAddress.adminArea ?: ""

                        repViewModel.locationAddressRetrievalComplete()
                    }
                }
            }
        }
        Log.i(TAG_R, "Exiting retrieveAddressFromLocationDetection")
        Log.i(TAG_R, "locationBasedAddress-Address Line 1:${locationBasedAddress.line1}")
        Log.i(TAG_R, "locationBasedAddress-LOCALITY: ${locationBasedAddress.city}")
        Log.i(TAG_R, "locationBasedAddress-PostalCode: ${locationBasedAddress.zip}")
        Log.i(TAG_R, "locationBasedAddress- adminArea${locationBasedAddress.state}")
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