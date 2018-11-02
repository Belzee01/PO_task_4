import java.util.Arrays;
import java.util.HashSet;

public class Main {

    public static void main(String[] args) {
        RelationalModel relationalModel = new RelationalModel();

        relationalModel.setAttributes(new HashSet<>(Arrays.asList("A", "B", "C", "D", "E", "F")));

        relationalModel.setFunctionalDependencies(
                new HashSet<>(Arrays.asList(
                        new FunctionalDependency(
                                new HashSet<>(Arrays.asList("A")),
                                new HashSet<>(Arrays.asList("B"))
                        ),
                        new FunctionalDependency(
                                new HashSet<>(Arrays.asList("B")),
                                new HashSet<>(Arrays.asList("A"))
                        ),
                        new FunctionalDependency(
                                new HashSet<>(Arrays.asList("A")),
                                new HashSet<>(Arrays.asList("C", "D"))
                        ),
                        new FunctionalDependency(
                                new HashSet<>(Arrays.asList("E")),
                                new HashSet<>(Arrays.asList("F"))
                        )
                ))
        );

        System.out.println(Arrays.toString(relationalModel.calcClosure(new String[]{"A", "E"})));
        System.out.println(relationalModel.isKey(new String[]{"A", "E"}, false));
        System.out.println(relationalModel.isKey(new String[]{"A", "E"}, true));

        System.out.println(relationalModel.getMinimalKeys());



    }
}
