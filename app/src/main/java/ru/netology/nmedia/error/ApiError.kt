import retrofit2.HttpException

data class ApiError(
    val code: Int,
    val message: String
) {
    constructor(exception: HttpException) : this(
        code = exception.code(),
        message = exception.message()
    )
}