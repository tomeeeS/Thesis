package tmit.bme.telkicar.test_data

import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.test.context.junit4.SpringRunner
import tmit.bme.telkicar.base.AbstractTestBase
import tmit.bme.telkicar.base.TLocation
import tmit.bme.telkicar.controller.dto.UjSajatFuvarDTO
import java.time.LocalDateTime
import tmit.bme.telkicar.controller.dto.UjFuvarIgenyDTO
import tmit.bme.telkicar.domain.user.Felhasznalo
import tmit.bme.telkicar.logic.helpers.AppContextHelper
import tmit.bme.telkicar.security.RegisterFelhasznaloDTO
import tmit.bme.telkicar.service.FelhasznaloService
import tmit.bme.telkicar.service.FuvarService
import tmit.bme.telkicar.service.IgenyService
import java.util.*
import kotlin.math.roundToInt

@Component
@RunWith(SpringRunner::class)
@SpringBootTest
class TestDataLoader @Autowired constructor(
    private val fuvarService: FuvarService,
    private val igenyService: IgenyService,
    private val felhasznaloService: FelhasznaloService,
    private val appContextHelper: AppContextHelper,
) : AbstractTestBase() {

    companion object {
        const val numberOfTravelPlansToGenerate = 30
        const val morningHour = 7
        const val goHomeHour = 16
    }

    private val special = TestDataBase.testLocations[appContextHelper.specialLocationTestKey]!!
    private val random = Random()
    private val minutes = intArrayOf(0, 15, 30, 45)

    @Test
    fun createFuvarNextMinute() {
        actAsAdmin()
        val destination = TestDataBase.testLocations["bme"]

        buildFuvarDto(
            special,
            destination,
            capacity = "3",
            hour = LocalDateTime.now().hour,
            minute = LocalDateTime.now().minute + 1
        )
    }

    @Test
    // Generates test users and rides in the morning to Bp and in the afternoon to Telki
    fun generateTestData() {
        igenyService.deleteAll()
        fuvarService.deleteAll()

        for (i in 1..numberOfTravelPlansToGenerate) {
            val name = TestDataBase.getRandomName()
            val tolerance = (random.nextDouble() * 4 + 4).roundToInt() * 5.0 // 20-40 %, 5 | x
            val fDto = RegisterFelhasznaloDTO.builder()
                .lastName(name[0])
                .firstName(name[1])
                .email("${random.nextInt()}@telki.test")
                .password("a")
                .distanceFlexibilityPercentage(tolerance)
                .timeFlexibilityPercentage(tolerance)
                .build()
//            val user = TFelhasznalo(
//                fDto
//            ).create(this)
            val user = felhasznaloService.registerNewUserAccount(fDto)

            val randomLocation = TestDataBase.getRandomLocation(appContextHelper)
            val capacity = (random.nextInt(3) + 2).toString()

//            actAs(user.email, user.password)

            // 2:1 Igeny - Fuvar ratio
            if (i % 3 < 2)
                generateIgeny(user, randomLocation)
            else
                generateFuvar(user, randomLocation, capacity)
        }
    }

    private fun generateIgeny(user: Felhasznalo, bp: TLocation) {
        igenyService.registerNewIgeny(
            ujFuvarIgenyDTO(
                special,
                bp,
                morningHour + random.nextInt(2)
            ),
            user.email
        )
        igenyService.registerNewIgeny(
            ujFuvarIgenyDTO(
                bp,
                special,
                goHomeHour + random.nextInt(2)
            ),
            user.email
        )
    }

    private fun ujFuvarIgenyDTO(
        start: TLocation,
        destination: TLocation,
        ora: Int,
    ) = UjFuvarIgenyDTO.builder()
        .datum(toFrontEndDateFormat(farFutureDate()))
        .honnan("innen")
        .hova("oda")
        .ora(ora)
        .perc(randomMinute())
        .utasokSzama(random.nextInt(2) + 1)
        .destinationAddress(destination.address)
        .destinationLat(destination.lat)
        .destinationLng(destination.lng)
        .destinationPlaceID(destination.placeID)
        .startAddress(start.address)
        .startLat(start.lat)
        .startLng(start.lng)
        .startPlaceID(start.placeID)
        .build()

    private fun generateFuvar(user: Felhasznalo, randomLocation: TLocation, capacity: String) {
        testFuvar(
            special,
            randomLocation,
            capacity,
            hour = morningHour + random.nextInt(2),
            minute = randomMinute(),
            user.email
        )
        testFuvar(
            randomLocation,
            special,
            capacity,
            hour = goHomeHour + random.nextInt(2),
            minute = randomMinute(),
            user.email
        )
    }

    private fun randomMinute() = minutes[random.nextInt(minutes.size)]

    private fun testFuvar(
        start: TLocation?,
        destination: TLocation?,
        capacity: String,
        hour: Int,
        minute: Int,
        userEmail: String
    ) {
        buildFuvarDto(
            start,
            destination,
            capacity,
            hour,
            minute,
        ).apply {
//            TFuvar(this).create(this@TestDataLoader)
            fuvarService.registerNewFuvar(this, userEmail)
        }
    }

    private fun buildFuvarDto(
        start: TLocation?,
        destination: TLocation?,
        capacity: String,
        hour: Int,
        minute: Int
    ) =
        UjSajatFuvarDTO.builder()
            .datum(toFrontEndDateFormat(farFutureDate()))
            .perc(minute)
            .ora(hour)
            .destinationAddress(destination!!.address)
            .destinationLat(destination.lat)
            .destinationLng(destination.lng)
            .destinationPlaceID(destination.placeID)
            .startAddress(start!!.address)
            .startLat(start.lat)
            .startLng(start.lng)
            .startPlaceID(start.placeID)
            .rendszam(TestDataBase.getRandomRendszam())
            .capacity(capacity)
            .autoLeiras("autoleiras")
            .info("info")
            .build()
}