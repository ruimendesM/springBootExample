package com.ruimendes.spring_boot_example.database.repository

import com.ruimendes.spring_boot_example.database.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository: MongoRepository<User, ObjectId> {

    fun findByEmail(email: String): User?
}