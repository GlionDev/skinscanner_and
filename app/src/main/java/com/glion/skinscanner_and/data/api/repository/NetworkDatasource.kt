package com.glion.skinscanner_and.data.api.repository

import com.glion.skinscanner_and.data.api.data.DermatologyData
import com.glion.skinscanner_and.data.api.data.RequestCheckFile
import com.glion.skinscanner_and.data.api.data.RequestExchangeKey
import kotlinx.coroutines.flow.Flow

interface NetworkDatasource {

//    suspend fun getVersion() : Flow<Int?>

    suspend fun getDermatologyList(
        query: String = "피부과",
        categoryGroupCode: String = "HP8",
        x: String,
        y: String,
        radius: Int = 3000,
        page: Int
    ): Flow<DermatologyData>

    suspend fun exchangeKey(request: RequestExchangeKey) : Flow<Boolean>

    suspend fun checkFile(request: RequestCheckFile): Flow<Boolean>
}