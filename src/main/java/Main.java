import java.util.Arrays;
import java.util.HashSet;

public class Main {

    public static void main(String[] args) {
        RelationalModel relationalModel = new RelationalModel();

        relationalModel.setAttributes(new HashSet<>(Arrays.asList("A", "B", "C", "D")));

        relationalModel.setFunctionalDependencies(
                new HashSet<>(Arrays.asList(
                        new FunctionalDependency(
                                new HashSet<>(Arrays.asList("A", "B")),
                                new HashSet<>(Arrays.asList("C"))
                        ),
                        new FunctionalDependency(
                                new HashSet<>(Arrays.asList("A")),
                                new HashSet<>(Arrays.asList("D"))
                        ),
                        new FunctionalDependency(
                                new HashSet<>(Arrays.asList("B", "C")),
                                new HashSet<>(Arrays.asList("D"))
                        ),
                        new FunctionalDependency(
                                new HashSet<>(Arrays.asList("D")),
                                new HashSet<>(Arrays.asList("E", "F"))
                        ),
                        new FunctionalDependency(
                                new HashSet<>(Arrays.asList("D", "A")),
                                new HashSet<>(Arrays.asList("C"))
                        )
                ))
        );
//
//        System.out.println(Arrays.toString(relationalModel.calcClosure(new String[]{"A", "E"})));
//        System.out.println(relationalModel.isKey(new String[]{"A", "E"}, false));
//        System.out.println(relationalModel.isKey(new String[]{"A", "E", "F"}, true));

//        System.out.println(relationalModel.getMinimalKeys());

        relationalModel.isBase(new HashSet<>(Arrays.asList(
                new FunctionalDependency(
                        new HashSet<>(Arrays.asList("A", "B")),
                        new HashSet<>(Arrays.asList("C"))
                ),
                new FunctionalDependency(
                        new HashSet<>(Arrays.asList("D")),
                        new HashSet<>(Arrays.asList("E", "F"))
                ),
                new FunctionalDependency(
                        new HashSet<>(Arrays.asList("B", "C")),
                        new HashSet<>(Arrays.asList("D"))
                )
        )));

//        System.out.println(relationalModel.combination(Arrays.asList("C", "D", "E"), 2));;
    }
}
