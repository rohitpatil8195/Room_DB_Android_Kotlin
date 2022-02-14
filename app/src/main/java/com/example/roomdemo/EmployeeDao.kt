package com.example.roomdemo

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Insert
     fun insert(employeeEntitiy: EmployeeEntitiy)

    @Update
     fun update(employeeEntitiy: EmployeeEntitiy)

    @Delete
     fun delete(employeeEntitiy: EmployeeEntitiy)

    @Query("SELECT * FROM `employee-table`")
    fun fetchAllEmployees():Flow<List<EmployeeEntitiy>>

    @Query("SELECT * FROM `employee-table` where id=:id")
    fun fetchEmployeeById(id:Int):Flow<EmployeeEntitiy>

}