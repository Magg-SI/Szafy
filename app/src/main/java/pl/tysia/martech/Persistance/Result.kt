package pl.tysia.martech.Persistance

/**
 * Reprezentuje rezultat zadania zapytania do API.
 *
 * @param T typ zwracany, np. dla listy szaf w miejsce T wpisujemy ArrayList<Locker>
 * @property item zwracany obiekt.
 * @property resultCode numer błędu, 0  jeśli funkcja wykonana bezbłędnie.
 * @property item opis błędu, jesli nie ma błędu to "OK".
 * @constructor Konstruktor tworzy nowy rezultat.
 *
 */
class Result<T> (var item : T, var resultCode : Int, var resultMessage : String) {

}