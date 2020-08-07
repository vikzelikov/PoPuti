package bonch.dev.poputi.data.network.driver

import bonch.dev.poputi.domain.entities.driver.signup.DriverData
import bonch.dev.poputi.domain.entities.driver.signup.DriverDataDTO
import bonch.dev.poputi.domain.entities.driver.signup.NewPhoto
import retrofit2.Response
import retrofit2.http.*

interface SignupService {

    @POST("/api/drivers")
    suspend fun createDriver(
        @HeaderMap headers: Map<String, String>, @Body driverData: DriverData
    ): Response<DriverDataDTO>


    @GET("/api/drivers/{id}")
    suspend fun getDriver(
        @HeaderMap headers: Map<String, String>, @Path("id") driverId: Int
    ): Response<DriverData>


    @PUT("/api/drivers/{id}")
    suspend fun putNewPhoto(
        @HeaderMap headers: Map<String, String>,
        @Path("id") driverId: Int,
        @Body photo: NewPhoto
    ): Response<*>

}