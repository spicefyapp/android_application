package com.dicoding.spicifyapplication.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.spicifyapplication.data.local.Spice
import com.dicoding.spicifyapplication.data.local.SpiceDao
import com.dicoding.spicifyapplication.data.local.SpiceRoomDatabase

class SpiceDetectRepository(application: Application) {

    private val mSpiceDao: SpiceDao

    init {
        val db = SpiceRoomDatabase.getDatabase(application)
        mSpiceDao = db.spiceDao()
    }

    fun getSpiceDetail(id: Int): LiveData<Spice?> = mSpiceDao.getSpiceDetail(id)

    companion object {
        @Volatile
        private var INSTANCE: SpiceDetectRepository? = null

        fun getInstance(application: Application): SpiceDetectRepository {
            return INSTANCE ?: synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = SpiceDetectRepository(application)
                }
                return INSTANCE as SpiceDetectRepository
            }
        }
    }
}