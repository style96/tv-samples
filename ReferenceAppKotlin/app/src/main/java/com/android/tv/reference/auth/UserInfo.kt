package com.android.tv.reference.auth

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Room class and interface for tracking User Info of users.
 */

@Entity(tableName = "user_info")
data class UserInfo(
    // A unique identifier for the user
    @PrimaryKey
    @ColumnInfo(name = "token") val token: String,

    @ColumnInfo(name = "display_name") val displayName: String
)

@Dao
interface UserInfoDao {
    @Query("SELECT * FROM user_info WHERE token = :token LIMIT 1")
    fun getUserInfoByToken(token: String): LiveData<UserInfo>

    @Query("SELECT * FROM user_info LIMIT 1 OFFSET 0")
    fun getUserInfo(): LiveData<UserInfo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userInfo: UserInfo)

    @Query("DELETE FROM user_info")
    fun deleteAll()
}