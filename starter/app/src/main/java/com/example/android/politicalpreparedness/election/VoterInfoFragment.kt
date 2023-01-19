package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.Election
import java.util.*

const val TAG3 = "VoterInfoFragment"
class VoterInfoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i("VoterInfoFragment", "Inside onCreateView")
        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this

        //TODO: Add ViewModel values and create ViewModel
        //Get hold of the application context
        val application = requireNotNull(this.activity).application
        //Get hold of the datasource
        val datasource = ElectionDatabase.getInstance(application).electionDao
        //Get hold of the ViewModelFactory

        val args = VoterInfoFragmentArgs.fromBundle(requireArguments())
        val selectedElection = args.argElection
        binding.election = selectedElection

        Log.i("TAG3", "Selected Election: " +
                "ID:${selectedElection.id}, Name:${selectedElection.name}")

        val viewModelFactory = VoterInfoViewModelFactory (selectedElection,
                                                            datasource, application)
        //Create ViewModel
        val voterInfoViewModel = ViewModelProvider(this, viewModelFactory).
                                                    get(VoterInfoViewModel::class.java)

        voterInfoViewModel.voterInfo.observe(viewLifecycleOwner, Observer {
                Log.i(TAG3, "Inside voterInfoViewModel.voterInfo.observe")
                if(it != null) {
                    binding.voterInfoViewModel = voterInfoViewModel
                }
        })
        Log.i(TAG3, "followElection Value before update: " +
                                    "${voterInfoViewModel.followElection.value}")

        //Check whether the selected election whether from the upcoming list or the saved election
        //list is already present in the database of saved election
        voterInfoViewModel.updateFollowElectionStatus(selectedElection)

        Log.i(TAG3, "followElection Value after upfate: " +
                                    "${voterInfoViewModel.followElection.value}")

        voterInfoViewModel.followElection.observe(viewLifecycleOwner, Observer {
                    if (it != null) {
                        if (voterInfoViewModel.followElection.value == true) {
                            binding.buttonFollowElectionToggle.text = "Unfollow Election"
                        } else {
                            binding.buttonFollowElectionToggle.text = "Follow Election"
                        }
                    }
        })

        binding.buttonFollowElectionToggle.setOnClickListener { view: View? ->
            kotlin.run {
                Log.i(TAG3, "inside the button follow election toggle on click listener")
                if (voterInfoViewModel.followElection?.value == true) {
                    Log.i(TAG3, "Change Follow to UnFollow")

                    voterInfoViewModel.unFollowElection()
                    binding.buttonFollowElectionToggle.text = "Follow Election"
                } else if ( voterInfoViewModel.followElection?.value == false) {
                    Log.i(TAG3, "Change UnFollow to Follow")

                    voterInfoViewModel.followElection()
                    binding.buttonFollowElectionToggle.text = "Unfollow Election"
                }
            }
        }

        binding.stateEditIcon.setOnClickListener {
            Log.i(TAG3, "After Address Edit click, navigating to Registered Address")
            this.findNavController().navigate(
                VoterInfoFragmentDirections.actionVoterInfoFragmentToRegisteredAddressFragment())
        }

        voterInfoViewModel.voterInfoAPICallStatus.observe(viewLifecycleOwner, Observer {
            if(it == null && it == VoterInfoCivicApiStatus.ERROR) {
                binding.buttonFollowElectionToggle.visibility = View.INVISIBLE
                binding.stateEditIcon.visibility = View.INVISIBLE
                binding.stateCorrespondenceHeader.visibility= View.INVISIBLE
                binding.voterInfoErrorMessage.visibility = View.VISIBLE
                binding.voterInfoErrorMessage.text = "At this time not able to load voting location and ballot information"
                voterInfoViewModel.resetVoterInfoAPICallStatus()

            }
        })

        Log.i(TAG3, "Exiting OnCreateView")
        return binding.root

    }

}