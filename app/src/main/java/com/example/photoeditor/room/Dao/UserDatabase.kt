package com.example.photoeditor.room.Dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.photoeditor.room.Model.UserDetail

@Database(entities = [UserDetail::class], version = 1)
abstract class UserDatabase : RoomDatabase(){
    abstract fun userDao() : UserDao

//    companion object{
//        private var INSTANCE : UserDatabase? = null
//
//        fun getDatabase(context: Context): UserDatabase{
//            if (INSTANCE == null){
//                INSTANCE = Room.databaseBuilder(context,
//                    UserDatabase::class.java,
//                    "user_database",
//                    ).build()
//            }
//            return INSTANCE!!
//        }
//    }

}