package com.yb.part6_chapter01.data.repository.restaurant.review

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.yb.part6_chapter01.data.entity.RestaurantReviewEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

@Suppress("UNCHECKED_CAST")
class DefaultRestaurantReviewRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
) : RestaurantReviewRepository {

    override suspend fun uploadPhoto(imageUriList: List<Uri>, id: Long): Result =
        withContext(ioDispatcher) {
            val uploadDeferred: List<Deferred<Any>> = imageUriList.mapIndexed { index, uri ->
                async {
                    try {
                        val fileName = "image_${index}.png"
                        return@async firebaseStorage.reference.child("review/photo")
                            .child(id.toString())
                            .child(fileName)
                            .putFile(uri)
                            .await().storage.downloadUrl.await().toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return@async Pair(uri, e)
                    }
                }
            }
            val result = uploadDeferred.awaitAll()
            val errorResults = result.filterIsInstance<Pair<Uri, Exception>>()
            val successResults = result.filterIsInstance<String>()

            when {
                errorResults.isNotEmpty() -> {
                    return@withContext Result.Error(errorResults.first().second)
                }
                else -> {
                    return@withContext Result.Success(result.filterIsInstance<String>())
                }
            }
        }

    override suspend fun uploadReview(
        reviewEntity: RestaurantReviewEntity,
    ): Result = withContext(ioDispatcher) {
        val result: Result
        val addReviewData = hashMapOf(
            "userId" to reviewEntity.userId,
            "title" to reviewEntity.title,
            "createdAt" to reviewEntity.createdAt,
            "content" to reviewEntity.content,
            "rating" to reviewEntity.rating,
            "imageUrlList" to reviewEntity.imageUrlList,
            "orderId" to reviewEntity.orderId,
            "restaurantTitle" to reviewEntity.restaurantTitle
        )
        result = try {
            firestore.collection("review")
                .add(addReviewData)
            Result.Success<Any>()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
        return@withContext result
    }

    override suspend fun getReviews(restaurantTitle: String): Result = withContext(ioDispatcher) {
        val result: Result = try {
            val snapshot = firestore.collection("review")
                .whereEqualTo("restaurantTitle", restaurantTitle)
                .get()
                .await()

            Result.Success(snapshot.documents.map {
                RestaurantReviewEntity(
                    id = it.id.toLong(),
                    userId = it.get("userId") as String,
                    title = it.get("title") as String,
                    createdAt = it.get("createdAt") as Long,
                    content = it.get("content") as String,
                    rating = it.get("rating") as Float,
                    imageUrlList = it.get("imageUrlList") as List<String>?,
                    orderId = it.get("orderId") as String,
                    restaurantTitle = it.get("restaurantTitle") as String
                )
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
        return@withContext result
    }

    sealed class Result {
        data class Success<T>(
            val data: T? = null,
        ) : Result()

        data class Error(
            val e: Throwable,
        ) : Result()
    }
}