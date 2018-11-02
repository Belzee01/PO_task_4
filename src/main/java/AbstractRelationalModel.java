import java.util.Collection;
import java.util.Set;

public abstract class AbstractRelationalModel {
    /**
     * Metoda ustala zbiĂłr atrybutĂłw naleĹźÄcych do relacji. Nazwy atrybutĂłw w
     * sÄ unikalne. MogÄ one skĹadaÄ siÄ z jednego lub wiÄkszej liczby
     * znakĂłw.
     *
     * @param atributes zbiĂłr zawierajÄcy nazwy atrybutĂłw
     */
    public abstract void setAttributes(Set<String> atributes);

    /**
     * Metoda ustala zbiĂłr zaleĹźnoĹci funkcyjnych speĹnianych przez danÄ relacjÄ.
     * @param functionalDependencies zbiĂłr zaleĹźnoĹci funkcyjnych
     */
    public abstract void setFunctionalDependencies( Set<AbstractFunctionalDependency> functionalDependencies );

    /**
     * Metoda generuje kolekcjÄ kluczy minimalnych. Jeden klucz minimalny reprezentowany jest
     * jako zbiĂłr nazw atrybutĂłw wchodzÄcych w skĹad klucza.
     *
     * @return kolekcja kluczy minimalnych.
     */
    public abstract Collection<Set<String>> getMinimalKeys();

    /**
     * Metoda sprawdza czy przekazany zbiĂłr zaleĹźnoĹci funkcyjnych jest bazÄ dla
     * ustalonej za pomocÄ setAttributes relacji. Jest tak wtedy, gdy wszystkie
     * zaleĹźnoĹci funkcyjne ustalone za pomocÄ setFunctionalDependencies moĹźna
     * wyprowadziÄ z zbioru base.
     *
     * @param base zbiĂłr zaleĹźnoĹci funkcyjnych, ktĂłre naleĹźy przetestowaÄ pod kÄtem
     * bycia bazÄ.
     * @return true - zbiĂłr base stanowi bazÄ, false - tak nie jest.
     */
    public abstract boolean isBase( Set<AbstractFunctionalDependency> base );
} 