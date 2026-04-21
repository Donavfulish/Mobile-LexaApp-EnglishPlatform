package com.home.lexa.data.repository

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import com.home.lexa.data.remote.FlashcardApiService
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.AllFlashcardPaginationResponse
import com.home.lexa.domain.models.AllFlashcardResultPaginationResponse
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.UpdateFlashcardResultRequest
import com.home.lexa.domain.repository.FlashcardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray

class FlashcardRepositoryImpl(
    private val apiService: FlashcardApiService
) : FlashcardRepository {
    private val okHttpClient = OkHttpClient()
    private fun generateCacheKey(type: String, searchInfo: SearchInfo, deckId: Long): String {
        val q = searchInfo.query ?: ""
        val sort = searchInfo.sortBy ?: ""
        val order = searchInfo.order ?: ""
        return "${type}_${deckId}_${q}_${sort}_${order}"
    }

    override suspend fun getFlashcardSuggestions(query: String?): Result<List<String>?> {
        return try {
            val response = apiService.getFlashcardSuggestions(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Lỗi lấy gợi ý Flashcard"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllFlashcard(
        deckId: Long,
        searchInfo: SearchInfo,
        nextCursor: Long?): Result<AllFlashcardPaginationResponse> {
        return try {
            val cacheKey = generateCacheKey("getAllFlashcard", searchInfo, deckId)
            val isFirstPage = nextCursor == null

            if (isFirstPage) {
                val cachedResponse: AllFlashcardPaginationResponse? = AppMemoryCache.get(cacheKey)
                if (cachedResponse != null && cachedResponse.data.isNotEmpty()) {
                    return Result.success(cachedResponse)
                }
            }

            val response = apiService.getAllFlashcard(
                deckId = deckId,
                query = searchInfo.query,
                sort = searchInfo.sortBy,
                order = searchInfo.order,
                limit = searchInfo.limit?.toString(),
                next_id = nextCursor?.toString(),
            )
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val apiPaginationData = body.data ?: throw Exception("Dữ liệu data trong body bị null")
                val newFlashcards = apiPaginationData.data

                val finalFlashcards = if (isFirstPage) {
                    newFlashcards
                } else {
                    val oldCache: AllFlashcardPaginationResponse? = AppMemoryCache.get(cacheKey)
                    val oldFlashcards = oldCache?.data ?: emptyList()
                    oldFlashcards + newFlashcards
                }
                val updatedResponse = apiPaginationData.copy(data = finalFlashcards)
                AppMemoryCache.put(cacheKey, updatedResponse)
                Result.success(updatedResponse)
            } else {
                val errorMsg = body?.message ?: "Lỗi từ máy chủ: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllFlashcardWithResult(
        deckId: Long,
        searchInfo: SearchInfo,
        nextCursor: Long?
    ): Result<AllFlashcardResultPaginationResponse> {
        return try {
            val cacheKey = generateCacheKey("getAllFlashcardWithResult", searchInfo, deckId)
            val isFirstPage = nextCursor == null

            if (isFirstPage) {
                val cachedResponse: AllFlashcardResultPaginationResponse? = AppMemoryCache.get(cacheKey)
                if (cachedResponse != null && cachedResponse.data.isNotEmpty()) {
                    return Result.success(cachedResponse)
                }
            }

            val response = apiService.getAllFlashcardWithResult(
                deckId = deckId,
                query = searchInfo.query,
                sort = searchInfo.sortBy,
                order = searchInfo.order,
                limit = searchInfo.limit?.toString(),
                next_id = nextCursor?.toString(),
            )
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val apiPaginationData = body.data ?: throw Exception("Dữ liệu data trong body bị null")
                val newFlashcards = apiPaginationData.data

                val finalResults = if (isFirstPage) {
                    newFlashcards
                } else {
                    val oldCache: AllFlashcardResultPaginationResponse? = AppMemoryCache.get(cacheKey)
                    val oldFlashcards = oldCache?.data ?: emptyList()
                    oldFlashcards + newFlashcards
                }
                val updatedResponse = apiPaginationData.copy(data = finalResults)
                AppMemoryCache.put(cacheKey, updatedResponse)
                Result.success(updatedResponse)
            } else {
                val errorMsg = body?.message ?: "Lỗi từ máy chủ: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createFlashcard(deckId: Long, request: RequestBody, imageUri: MultipartBody.Part?): Result<Long> {
        return try {
            val response = apiService.createFlashcard(deckId, request, imageUri)
            val body = response.body()
            Log.d("FlashcardRepositoryImpl", "Response body: $body")

            if (response.isSuccessful && body?.success == true && body.data != null) {
                Log.d("Đã xoá cache create", "Cache create đã được xoá_${deckId}")
                AppMemoryCache.removePrefix("getAllFlashcard_${deckId}");
                AppMemoryCache.removePrefix("getAllFlashcardWithResult_${deckId}");
                AppMemoryCache.remove("getAllDecks");
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi tạo flashcard"))
            }
        } catch (e: Exception) {
            Log.e("ERROR_CATCH_FLASHCARD", "Cache create đã được xoá", e)
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun updateFlashcard(deckId: Long, request: RequestBody, imageUri: MultipartBody.Part?): Result<Boolean> {
        return try {
            val response = apiService.updateFlashcard(deckId, request, imageUri)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache update", "Cache update đã được xoá")
                AppMemoryCache.removePrefix("getAllFlashcard_${deckId}");
                AppMemoryCache.removePrefix("getAllFlashcardWithResult_${deckId}");
                Result.success(body.data ?: true)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi cập nhật flashcard"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun deleteFlashcard(flashcardId: Long, deckId: Long): Result<Boolean> {
        return try {
            val response = apiService.deleteFlashcard(deckId, flashcardId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache delete", "Cache delete: getAllFlashcard_${flashcardId}")
                AppMemoryCache.removePrefix("getAllFlashcard_${deckId}");
                AppMemoryCache.removePrefix("getAllFlashcardWithResult_${deckId}");
                AppMemoryCache.remove("getAllDecks");
                Result.success(body.data ?: true)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi xóa flashcard"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun updateFlashcardResults(deckId: Long, request: UpdateFlashcardResultRequest): Result<Boolean> {
        return try {
            val response = apiService.updateFlashcardResults(deckId, request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache update result", "Cache update result đã được xoá")
                AppMemoryCache.removePrefix("getAllFlashcardWithResult_${deckId}");
                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi cập nhật kết quả flashcard"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getExampleSuggestion(generativeModel: GenerativeModel, word: String, meaning: String, partOfSpeech: String): String {


        val prompt = """
            Bạn là một chuyên gia ngôn ngữ học. Nhiệm vụ của bạn là tạo 1 câu ví dụ tiếng Anh trong 100 kí tự cho từ vựng "$word".
            Ngữ cảnh do người dùng cung cấp:
            - Loại từ: $partOfSpeech
            - Nghĩa tiếng Việt: $meaning
            
            QUY TRÌNH XỬ LÝ:
            1. Đầu tiên, hãy kiểm tra xem Nghĩa tiếng Việt và Loại từ này có thực sự hợp lý và tồn tại cho từ "$word" trong tiếng Anh hay không (chấp nhận cả nghĩa lóng, nghĩa hiếm).
            2. NẾU HỢP LÝ: Trả về duy nhất nội dung câu ví dụ tiếng Anh. Tuyệt đối không có ngoặc kép hay giải thích.
            3. NẾU VÔ LÝ (Ví dụ: Từ "Apple" mà nghĩa là "Chạy bộ", hoặc từ không có thật): Hãy trả về chính xác cụm từ này: INVALID_CONTEXT
            
            Tuyệt đối tuân thủ, không in ra thêm bất kỳ chữ nào khác.
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)
        val exampleText = response.text?.trim() ?:"Ngữ cảnh không phù hợp"
        if(exampleText == "INVALID_CONTEXT") return "Ngữ cảnh không phù hợp"
        return exampleText
    }

    override suspend fun getPhoneticFromApi(word: String, partOfSpeech: String): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = Request.Builder()
                .url("https://api.dictionaryapi.dev/api/v2/entries/en/$word")
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) return@withContext Result.failure(Exception("Không tìm thấy từ vựng"))

            val responseBody = response.body?.string() ?: return@withContext Result.failure(Exception("Dữ liệu rỗng"))
            val jsonArray = JSONArray(responseBody)

            if (jsonArray.length() == 0) return@withContext Result.failure(Exception("Không có dữ liệu"))

            val targetPos = when (partOfSpeech) {
                "Danh từ" -> "noun"
                "Động từ" -> "verb"
                "Tính từ" -> "adjective"
                "Trạng từ" -> "adverb"
                else -> ""
            }


            data class PhoneticCandidate(
                val text: String,
                val audioUrl: String,
                val entryPartsOfSpeech: List<String>
            )

            val candidates = mutableListOf<PhoneticCandidate>()

            for (i in 0 until jsonArray.length()) {
                val entry = jsonArray.getJSONObject(i)

                val entryPosList = mutableListOf<String>()
                if (entry.has("meanings")) {
                    val meanings = entry.getJSONArray("meanings")
                    for (j in 0 until meanings.length()) {
                        val m = meanings.getJSONObject(j)
                        if (m.has("partOfSpeech") && !m.isNull("partOfSpeech")) {
                            entryPosList.add(m.getString("partOfSpeech").lowercase())
                        }
                    }
                }

                if (entry.has("phonetics")) {
                    val phoneticsArray = entry.getJSONArray("phonetics")
                    for (j in 0 until phoneticsArray.length()) {
                        val pObj = phoneticsArray.getJSONObject(j)
                        val text = if (pObj.has("text") && !pObj.isNull("text")) pObj.getString("text") else ""
                        val audioUrl = if (pObj.has("audio") && !pObj.isNull("audio")) pObj.getString("audio") else ""

                        if (text.isNotEmpty()) {
                            candidates.add(PhoneticCandidate(text, audioUrl, entryPosList))
                        }
                    }
                }


                if (entry.has("phonetic") && !entry.isNull("phonetic")) {
                    val text = entry.getString("phonetic")
                    if (text.isNotEmpty()) {
                        candidates.add(PhoneticCandidate(text, "", entryPosList))
                    }
                }
            }

            if (candidates.isEmpty()) return@withContext Result.failure(Exception("Từ vựng không có phiên âm"))

            var finalPhoneticText: String? = null

            if (targetPos.isNotEmpty()) {

                for (candidate in candidates) {
                    val audio = candidate.audioUrl.lowercase()
                    if (audio.contains("-$targetPos.mp3") || audio.contains("-$targetPos-")) {
                        finalPhoneticText = candidate.text
                        break
                    }
                }

                if (finalPhoneticText == null) {
                    for (candidate in candidates) {
                        if (candidate.entryPartsOfSpeech.contains(targetPos)) {
                            finalPhoneticText = candidate.text
                            break
                        }
                    }
                }
            }

            if (finalPhoneticText == null) {
                finalPhoneticText = candidates.first().text
            }

            Result.success(finalPhoneticText)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Lỗi mạng: ${e.message}"))
        }
    }

}
