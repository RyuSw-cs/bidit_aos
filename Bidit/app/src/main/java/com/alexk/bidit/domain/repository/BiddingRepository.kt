package com.alexk.bidit.domain.repository

import com.alexk.bidit.CancelBidMutation
import com.alexk.bidit.DoBidMutation
import com.alexk.bidit.GetBiddingInfoQuery
import com.alexk.bidit.GetMyBiddingInfoQuery
import com.apollographql.apollo3.api.ApolloResponse

interface BiddingRepository {
    suspend fun retrieveBiddingInfo(itemId: Int): ApolloResponse<GetBiddingInfoQuery.Data>
    suspend fun retrieveMyBiddingInfo() : ApolloResponse<GetMyBiddingInfoQuery.Data>
    suspend fun controlBid(
        itemId: Int,
        bidPrice: Int,
        status: Int
    ): ApolloResponse<DoBidMutation.Data>
}