package bonch.dev.domain.utils

object Constants {
    const val API_KEY = "6e2e73e8-4a73-42f5-9bf1-35259708af3c"

    const val ACCESS_TOKEN = "ACCESS_TOKEN"

    const val GEO_PERMISSION = "android.permission.ACCESS_FINE_LOCATION"
    const val STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE"
    const val GEO_PERMISSION_REQUEST = 1
    const val STORAGE_PERMISSION_REQUEST = 2

    const val BASE_URL = "https://17037868-review-poputi-16-npl1p6.server.bonch.dev"


    const val MAIN_FRAGMENT = 1
    const val REGULAR_DRIVING_VIEW = 2
    const val CREATE_RIDE_VIEW = 3
    const val PROFILE_VIEW = 4
    const val GET_DRIVER_VIEW = 5
    const val OFFER_PRICE_VIEW = 6
    const val ADD_BANK_CARD_VIEW = 7
    const val PHONE_VIEW = 8
    const val CONFIRM_PHONE_VIEW = 9
    const val FULL_NAME_VIEW = 10
    const val PROFILE_FULL_VIEW = 11
    const val PROFILE_CHECK_PHOTO = 12
    const val DRIVER_SIGNUP = 13
    const val DRIVER_SIGNUP_DOCS_VIEW = 14
    const val DRIVER_SIGNUP_STEP_VIEW = 15
    const val DRIVER_SIGNUP_CAR_INFO = 16
    const val DRIVER_SIGNUP_CHECK_PHOTO = 17
    const val DRIVER_SIGNUP_TABLE_DOCS = 18

    const val FROM = "FROM"
    const val TO = "TO"
    const val NOT_DESCRIPTION = "Место"
    const val USER_POINT = "USER_POINT"
    const val RIDE_DETAIL_INFO = "RIDE_DETAIL_INFO"
    const val OFFER_PRICE = "OFFER_PRICE"
    const val AVERAGE_PRICE = "AVERAGE_PRICE"
    const val CARD_NUMBER = "CARD_NUMBER"
    const val VALID_UNTIL = "VALID_UNTIL"
    const val CARD_IMG = "CARD_IMG"
    const val CVC = "CVC"

    //request MapKit
    const val CASH_VALUE_COUNT = 12
    const val MAX_COUNT_SUGGEST = 7

    //mil sec
    const val TIMER_USER_GET_DRIVER = 30
    const val BLOCK_REQUEST_GEOCODER = 3000L

    //min
    const val MAX_TIME_GET_DRIVER = 3L


    //reasons id
    const val REASON1 = 1
    const val REASON2 = 2
    const val REASON3 = 3
    const val REASON4 = 4


    //driver info
    const val NAME_DRIVER = "NAME_DRIVER"
    const val CAR_NAME = "CAR_NAME"
    const val CAR_NUMBER = "CAR_NUMBER"
    const val RATING = "RATING"
    const val IMG_DRIVER = "IMG_DRIVER"
    const val PRICE_DRIVER = "PRICE_DRIVER"
    const val IS_DRIVER_ARRIVED = "IS_DRIVER_ARRIVED"


    //profile data
    const val PROFILE_DATA = "PROFILE_DATA"
    const val PHOTO = "PHOTO"
    const val IS_SHOW_POPUP= "IS_SHOW_POPUP"

    const val CAMERA = 0
    const val GALLERY = 1


    //driver signup
    const val DRIVER_SIGNUP_START = 0
    const val DRIVER_SIGNUP_PROCESS = 1
    const val DRIVER_SIGNUP_COMPLETE = 2

    const val USER_PHOTO = 0
    const val PASSPORT_PHOTO = 1
    const val SELF_PHOTO_PASSPORT = 2
    const val PASSPORT_ADDRESS_PHOTO = 3
    const val DRIVER_DOC_FRONT = 4
    const val DRIVER_DOC_BACK = 5
    const val STS_DOC_FRONT = 6
    const val STS_DOC_BACK = 7


    //REALM const
    const val CASH_RIDE_REALM_NAME = "cashride.realm"
    const val CASH_REQUEST_REALM_NAME = "cashrequest.realm"
    const val DRIVER_DOCS_REALM_NAME = "docs.realm"
    const val PROFILE_REALM_NAME = "profile.realm"


    const val BOOL_DATA = "BOOL_DATA"
    const val STRING_DATA = "STRING_DATA"

    const val EXIT = -2

}
