package model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class OnGetDataListener{
    fun onStart(){}
    fun onSuccess(data : DataSnapshot){}
    fun onFailed(databaseError: DatabaseError){}
}