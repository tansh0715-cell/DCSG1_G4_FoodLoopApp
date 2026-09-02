package com.example.assignment.data.repository

import com.example.assignment.data.supabase.supabase
import io.github.jan.supabase.storage.storage
import java.util.UUID

class LicensePhotoRepository {

    suspend fun uploadLicensePhoto(
        imageBytes: ByteArray
    ): String {

        val fileName =
            "license_${UUID.randomUUID()}.jpg"

        val bucket =
            supabase.storage.from("license-photos")

        bucket.upload(
            path = fileName,
            data = imageBytes
        ) {
            upsert = false
        }

        return bucket.publicUrl(fileName)
    }
}