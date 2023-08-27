package com.beacon.data

class DataRepository(private val dataDao: DisasterDAO) {
    suspend fun insertData(dataEntity: DisasterEntity) {
        dataDao.insert(dataEntity)
    }

    suspend fun updateData(dataEntity: DisasterEntity) {
        dataDao.update(dataEntity)
    }

    suspend fun getAllData(): List<DisasterEntity> {
        return dataDao.getAllDisaster()
    }

    suspend fun getIsCheckedList(): List<Int> {
        return dataDao.getIsCheckedValues()
    }

    suspend fun updateIsCheckedValue(position: Int, isCheckedValue: Int) {
        dataDao.updateIsCheckedValue(position, isCheckedValue)
    }

    suspend fun insertInitialData() {
        val initialData = listOf(
            DisasterEntity(1,"호우",0),
            DisasterEntity(2,"대설",0),
            DisasterEntity(3,"지진",0),
            DisasterEntity(4,"태풍",0),
            DisasterEntity(5,"산불",0),
            DisasterEntity(6,"민방위",0),
        )
        dataDao.insertAll(initialData)
    }
}