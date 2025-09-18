package com.ruimendes.spring_boot_example.database.repository

import com.ruimendes.spring_boot_example.database.model.Note
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface NoteRepository : MongoRepository<Note, ObjectId> {

    fun findByOwnerId(ownerId: ObjectId): List<Note>
}