package com.example.android.politicalpreparedness.network.models

data class ElectionLists (
    var electionList: List<Election>,
    var savedElectionList: List<SavedElection>
        )