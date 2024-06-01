import com.example.datn.data.dataresult.MyResponse
import com.example.datn.data.model.BodyFCM.Message
import com.example.datn.data.model.BodyFCM.NotifyBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMAPIService {
    @Headers("Content-Type:application/json")
    @POST("projects/datn-b3605/messages:send")
    fun sendNotification(@Body notifyBody: NotifyBody, @Header("Authorization") authHeader: String): Call<MyResponse>
}
