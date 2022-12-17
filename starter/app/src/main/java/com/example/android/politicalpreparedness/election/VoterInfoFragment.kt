package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
        val selectedElection = Election(args.argElectionId,"",
            Date("12/16/2022"), false, args.argDivision)

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
        //TODO: Add binding values

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        return binding.root

    }

    //TODO: Create method to load URL intents

}