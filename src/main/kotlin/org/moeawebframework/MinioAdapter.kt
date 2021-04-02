package org.moeawebframework

import io.minio.BucketExistsArgs
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.core.io.Resource


class MinioAdapter(
    endpoint: String,
    access_key: String,
    secret_key: String,
    private val bucket: String
) {

    private val minioClient = MinioClient.builder()
        .endpoint(endpoint, 9000, false)
        .credentials(access_key, secret_key)
        .build()

    init {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build()))
            throw RuntimeException("[500] Internal exception")
    }

    suspend fun upload(md5: String, file: Resource) {
        GlobalScope.launch(Dispatchers.IO) {
            minioClient.putObject(
                PutObjectArgs.builder().bucket(bucket).`object`(md5).stream(file.inputStream, file.contentLength(), -1)
                    .contentType("application/octet-stream").build()
            )
        }.join()
    }

    suspend fun download(md5: String): ByteArray {
        var byteArray = ByteArray(0)
        GlobalScope.launch(Dispatchers.IO) {
            byteArray =
                minioClient.getObject(GetObjectArgs.builder().bucket(bucket).`object`(md5).build()).readAllBytes()
        }.join()
        return byteArray
    }

}