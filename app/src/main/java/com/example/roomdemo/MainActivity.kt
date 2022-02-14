package com.example.roomdemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdemo.databinding.ActivityMainBinding
import com.example.roomdemo.databinding.DialogUpdateBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding:ActivityMainBinding ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val employeeDao =(application as EmployeeApp).db.employeeDao()
        binding?.btnAdd?.setOnClickListener {
            //call add recorded with employeeDao
            addRecord(employeeDao)
        }

        lifecycleScope.launch {
            employeeDao.fetchAllEmployees().collect {
              val list = ArrayList(it)
                setUpListOfDataIntoRecycelerView(list,employeeDao)
            }
        }
    }

    fun addRecord(employeeDao: EmployeeDao){
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmail?.text.toString()


        if(email.isNotEmpty() && name.isNotEmpty()){
            lifecycleScope.launch{
                 employeeDao.insert(EmployeeEntitiy(name=name, email = email))
                Toast.makeText(applicationContext,"Record saved", Toast.LENGTH_LONG).show()
                binding?.etName?.text?.clear()
                binding?.etEmail?.text?.clear()
            }
        }else{
            Toast.makeText(this,"Name and email cannot be blank", Toast.LENGTH_LONG).show()
        }
    }

    private fun setUpListOfDataIntoRecycelerView(employeList: ArrayList<EmployeeEntitiy>,
    employeeDao: EmployeeDao){
          if(employeList.isNotEmpty()){
               val itemAdapter = ItemAdapter(employeList,
                   {updateId ->
                       updateRecordDialog(updateId,employeeDao)
                   },
                   { deleteId ->
                       deleteRecordAlertDialog(deleteId, employeeDao)

                   }
               )
             binding?.rvItemList?.layoutManager =LinearLayoutManager(this)
               binding?.rvItemList?.adapter = itemAdapter
              binding?.rvItemList?.visibility = View.VISIBLE
              binding?.tvNoRecFound?.visibility =View.GONE
          }else{
              binding?.rvItemList?.visibility = View.GONE
              binding?.tvNoRecFound?.visibility =View.VISIBLE
          }
    }





   private fun updateRecordDialog(id:Int,employeeDao: EmployeeDao){
          val updateDialog = Dialog(this,R.style.Theme_Dialog)
          updateDialog.setCancelable(false)
        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

          lifecycleScope.launch {
              employeeDao.fetchEmployeeById(id).collect {
                  if(it != null) {
                      binding.etUpdateName.setText(it.name)
                      binding.etUpdateEmail.setText(it.email)
                  }
              }
          }

        binding.tvUpdateClick.setOnClickListener {
               val name = binding.etUpdateName.text.toString()
            val email = binding.etUpdateEmail.text.toString()
            if(name.isNotEmpty() && email.isNotEmpty()){
                  lifecycleScope.launch{
                      employeeDao.update(EmployeeEntitiy(id,name, email))
                      Toast.makeText(applicationContext,"Record Updated",Toast.LENGTH_LONG).show()
                      updateDialog.dismiss()
                  }
            }else{
                Toast.makeText(applicationContext,"Email and name cannot be empty",Toast.LENGTH_LONG).show()
            }
        }
       binding.tvCancelClick.setOnClickListener {
           updateDialog.dismiss()
       }

       updateDialog.show()

    }


   private fun deleteRecordAlertDialog(id:Int, employeeDao: EmployeeDao){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setPositiveButton("Yes"){ dialogInterface,_->
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntitiy(id))
            }
            Toast.makeText(applicationContext,"Account Deleted",Toast.LENGTH_LONG).show()
            dialogInterface.dismiss()
        }

          builder.setNegativeButton("No"){dialogInterface,which->
              dialogInterface.dismiss()
          }

        val alertDialog:AlertDialog = builder.create()
           alertDialog.setCancelable(false)
        alertDialog.show()

    }

}