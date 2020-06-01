package pl.tysia.martech.Persistance

/**
 * Reprezentuje rezultat zadania zapytania do API.
 *
 * @param T typ zwracany, np. dla listy szaf w miejsce T wpisujemy ArrayList<Locker>
 * @property item zwracany obiekt.
 * @property resultCode numer błędu, 0  jeśli funkcja wykonana bezbłędnie.
 * @property resultMessage opis błędu, jesli nie ma błędu to "OK".
 * @constructor Konstruktor tworzy nowy rezultat.
 *
 */

class Result<T : Any> (var item : T?, var resultCode : Int, var resultMessage : String) {
    companion object{
        public const val RESULT_OK = 0;
    }
}