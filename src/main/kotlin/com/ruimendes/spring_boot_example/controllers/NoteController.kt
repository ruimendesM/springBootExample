package com.ruimendes.spring_boot_example.controllers

import com.ruimendes.spring_boot_example.database.model.Note
import com.ruimendes.spring_boot_example.database.repository.NoteRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/notes")
class NoteController(
    private val repository: NoteRepository
) {

    data class NoteRequest(
        val id: String?,
        @field:NotBlank(message = "Title can't be blank")
        val title: String,
        val content: String,
        val color: Long
    )

    data class NoteResponse(
        val id: String,
        val title: String,
        val content: String,
        val color: Long,
        val createdAt: Instant,
    )

    @PostMapping
    fun save(@Valid @RequestBody body: NoteRequest): NoteResponse {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        val note = with(body) {
            repository.save(
                Note(
                    id = id?.let { ObjectId(id) } ?: ObjectId.get(),
                    title = title,
                    content = content,
                    color = color,
                    createdAt = Instant.now(),
                    ownerId = ObjectId(ownerId)
                )
            )
        }
        return NoteResponse(
            id = note.id.toHexString(),
            title = note.title,
            content = note.content,
            color = note.color,
            createdAt = note.createdAt
        )
    }

    @GetMapping
    fun findByOwnerId(): List<NoteResponse> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        val notes = repository.findByOwnerId(ObjectId(ownerId))
        return notes.map { note ->
            note.toNoteResponse()
        }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(
        @PathVariable(required = true) id: String
    ) {
        val note = repository.findById(ObjectId(id)).orElseThrow {
            IllegalArgumentException("Note not found")
        }
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        if (note.ownerId.toHexString() == ownerId) {
            repository.deleteById(ObjectId(id))
        } else {
            throw IllegalArgumentException("Note not found")
        }
    }
}

private fun Note.toNoteResponse() = NoteController.NoteResponse(
    id = this.id.toHexString(),
    title = this.title,
    content = this.content,
    color = this.color,
    createdAt = this.createdAt
)