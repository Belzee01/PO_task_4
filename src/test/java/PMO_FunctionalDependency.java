import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PMO_FunctionalDependency extends AbstractFunctionalDependency {

    private final Set<String> dependent;
    private final Set<String> determinant;

    public static Set<String> splitString2Set(String string) {
        return new TreeSet<>(Arrays.stream(string.split(",")).map(f -> f.trim()).collect(Collectors.toSet()));
    }

    public static String[] splitString2Table(String string) {
        return collection2table(splitString2Set(string));
    }

    public static String[] collection2table(Collection<String> collection) {
        return collection.toArray(new String[collection.size()]);
    }

    public static Collection<String> table2collection(String... strings) {
        return Arrays.asList(strings);
    }

    public static Set<String> table2set(String... strings) {
        return new HashSet<>( table2collection(strings) );
    }

    public static String[] splitL(String functionalDependency) {
        return functionalDependency.split("->")[0].split(",");
    }

    public static String[] splitR(String functionalDependency) {
        return functionalDependency.split("->")[1].split(",");
    }
////////////////////////////////////////////////////


    @Override
    public Set<String> getDependentAttributes() {
        return dependent;
    }

    @Override
    public Set<String> getDeterminantSet() {
        return determinant;
    }

    public PMO_FunctionalDependency( String functionalDependency ) {
        String[] argL = splitL(functionalDependency);
        String[] argR = splitR(functionalDependency);

        determinant = table2set( argL );
        dependent = table2set( argR );
    }
}