import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class AccessToken {
    private val firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging"

    suspend fun getAccessToken(): String {
        try {
            val jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"datn-b3605\",\n" +
                    "  \"private_key_id\": \"b7f3fc8c2c6893d29ce47e6d09a1bc43cd3c0e3d\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCikr7omEdGe7P4\\nazrcHUn2wUDYJJUJC+bKFJZX0roOWdvX6R9vECw+gKah5CEsZWbVoELrc9G2LBBH\\ngjVHzYPZfT2nzvUwxFwOBQAAO2RlY4WzC4e0d8W07tEo4nZ6rUl7YKN2alb1UDJg\\nG1sScPoITwKlFauMRcOrraSGRO/vyzgscVapGAF70fgYzZaBlyNgXzne/kLqc9CH\\nBnx0AMf4p1nnWW5fZf8hJMVW2VmisRvOyWtzD1iZQzJV9B9USe6FbHcNbF8Rop+p\\n0snwWcMco/CtHA0mudjENxZtUEXv9H3WVUD/lHJ//wsD/+f2ZUdetdbJCD8giHW3\\n9HYW1kOVAgMBAAECggEAGsb15+SxFLRqW7f5vWiblqeVESTRYWiS7mT+sF92osd9\\nNO5ibY4p42+YQqELL79R9UzMyPBOMeXHFqglM3rvxSJiReIKM6qcCPkc22zs5+BB\\nFNEI3Jo/t8IANG48mo8xQ7+SnP397bD0Yy0OTpbKMBUnd95+X3/cL0pOB4mSRZod\\nMHzQhza3qo+D7Z0Zi2YiSzuZr+td8wfhXq21HyormmQnr4yqqnO0SlJtemxktSU3\\nA7yJgcLrAOHcM1CUG3+W8rj2G6eFY0gpNz4vO+9MSulFNPINdl64LEnj+aI9NJrI\\nGws86Ai+vLpY/KOfMV/BDOiR13Pc5df2C04vR7x5PwKBgQDlQEOmW0CLQDrIXep/\\n3YFYbEPhOFQDmHiwoZu90uVpI0GL9K+WEp6UAPjOC6hgkazNa6mKAHcCFsJbNHae\\nIkde9LBj2ogGDpwdbGrktdwtoKaKtbcIVX7ohLn6RCuiF7cUmXspiBVW0K0X+4Lc\\nBH1B+kLeBjYn+pXSmnmTyQJiowKBgQC1itAwWEtjLrJdcGs5ojUUyK+MUL3Q499k\\nLzHnFGHlE0cjXgc+kkf52LwRXGNmw01Sw5BDk5LacgYoGq1dbhzsEr8pfSem70fn\\nssD/BD1tZlLwNmCoRK+qoVCW14UqxHkcTs74W2nXK6v7MRNQV2MtzNrgYXNfhh5f\\nKcJlm7pcZwKBgFi/rzDIpM0DuX319aE4hClSCFk5MDUQIrDmb19mNI+mKWrf99WW\\nMbPESI2jr7Rq0MXZhxG4qlmxq94ZCk6foJefDGYrpHsLzt4JKbyOtsUnKzIJtOjS\\nCEZFk6XSHD4tBiBAZpCmcPRh5pChOoCocEFJe/dt2itR9AxK5wQSTg0XAoGAHetn\\nzl/u5MTAbrflZBVArFE65WQ1NGUp3wgMMGpii+92bPTFOuG+7QV0UWQpmbkcWESU\\n1R5QOKJMM3XaFqjcKWgZi0vuKgwLjLsgJadcPFh92MsNeQ4A6Z02e/W/I8JHt4uh\\nfrsKIYU4DekTps0QMm8tr4dADYE8JjDqsfZNa4cCgYEAmSi5Nx3tFKzwtpeWyROr\\nyq8L20ASBZR5VlpajUP/FKiGSjA7xmz+XGohmi6eAvzVI7RoKr0PCY1UfyJwYI5O\\ndevdGfMyJvMZC/hNtS5/VOEuf4bNFWaEr3s/8BZj7mKvpmWHbckt1BUX8Z/vt8lP\\nlPSdVJWLvlDsEFXlzSxAEZE=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-qivu3@datn-b3605.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"104965574001230341308\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-qivu3%40datn-b3605.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}"
            val stream: InputStream =
                ByteArrayInputStream(jsonString.toByteArray(StandardCharsets.UTF_8))

            // Use GoogleCredentials to get an access token
            val credentials = GoogleCredentials.fromStream(stream)
                .createScoped(listOf(firebaseMessagingScope))

            credentials.refresh()
            return credentials.accessToken.tokenValue
        } catch (e: IOException) {
            Log.e("AccessToken", "Error: ${e.message}")
            throw RuntimeException("Failed to get access token", e)
        }
    }
}
