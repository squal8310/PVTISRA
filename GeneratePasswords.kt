import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

fun main() {
    val encoder = BCryptPasswordEncoder()

    val passwords = mapOf(
        "admin" to "admin",
        "cajero1" to "cajero"
    )

    println("==============================")
    println("HASHES BCRYPT PARA BASE DE DATOS")
    println("==============================\n")

    passwords.forEach { (user, password) ->
        val hash = encoder.encode(password)
        println("Usuario: $user")
        println("Contraseña: $password")
        println("Hash: $hash")
        println("SQL: UPDATE users SET password_hash = '$hash' WHERE user_identifier = '$user';")
        println("---")
    }
}
