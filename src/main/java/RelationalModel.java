import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RelationalModel extends AbstractRelationalModel {

    private List<String> attributes;
    private List<AbstractFunctionalDependency> keyValues;

    public RelationalModel() {
        this.attributes = new ArrayList<>();
        this.keyValues = new ArrayList<>();
    }

    public String[] calcClosure(String[] args) {
        List<String> current = new ArrayList<>(Arrays.asList(args));
        List<AbstractFunctionalDependency> keyValuesCopy = new ArrayList<>(this.keyValues);

        for (int i = 0; i < current.size(); i++) {
            boolean changed = false;
            for (int j = 0; j < keyValuesCopy.size(); j++) {
                AbstractFunctionalDependency currentPair = keyValuesCopy.get(j);

                List<String> relationKeyAsList = new ArrayList<>(currentPair.getDeterminantSet());
                List<String> relationValueAsList = new ArrayList<>(currentPair.getDependentAttributes());

                if (current.containsAll(relationKeyAsList)) {
                    if (!currentPair.getDeterminantSet().equals(currentPair.getDependentAttributes()) && !current.containsAll(relationValueAsList)) {
                        int index = current.indexOf(relationKeyAsList.get(relationKeyAsList.size() - 1));
                        current.addAll(index + 1, relationValueAsList);
                    }
                    changed = true;
                    keyValuesCopy.remove(currentPair);
                }
            }
            if (!changed)
                break;
        }

        return current.toArray(new String[0]);
    }

    public boolean isKey(String[] args, boolean minimal) {

        boolean isKey = false;
        List<String> closure = Arrays.asList(this.calcClosure(args));
        ;

        Collections.sort(this.attributes);
        Collections.sort(closure);

        if (this.attributes.equals(closure))
            isKey = true;

        if (isKey && minimal) {
            List<String> currentSet = new ArrayList<>(Arrays.asList(args));
            for (AbstractFunctionalDependency currentPair : this.keyValues) {
                List<String> keyValueCombined = new ArrayList<>(currentPair.getDeterminantSet());
                List<String> relationValueAsList = new ArrayList<>(currentPair.getDependentAttributes());

                keyValueCombined.addAll(relationValueAsList);
                List<String> keyValueCombinedAndReduced = keyValueCombined.stream().distinct().collect(Collectors.toList());

                if (keyValueCombinedAndReduced.size() > 1 && currentSet.containsAll(keyValueCombinedAndReduced))
                    return false;
            }
        }

        return isKey;
    }

    @Override
    public void setAttributes(Set<String> atributes) {
        this.attributes = new ArrayList<>(atributes);
    }

    @Override
    public void setFunctionalDependencies(Set<AbstractFunctionalDependency> functionalDependencies) {
        functionalDependencies.forEach(fd -> {
            this.keyValues.addAll(functionalDependencies);
        });
    }

    @Override
    public Collection<Set<String>> getMinimalKeys() {
        return checkMinimalKeyForEachCombination(this.attributes);
    }

    @Override
    public boolean isBase(Set<AbstractFunctionalDependency> base) {
        // evaluate all closures for keys of the base
        Set<AbstractFunctionalDependency> fdCopy = new HashSet<>(this.keyValues);
        RelationalModel model = new RelationalModel();
        model.setFunctionalDependencies(base);
        base.forEach(b -> {
            String[] a = model.calcClosure(b.getDeterminantSet().toArray(new String[0]));

            Set<AbstractFunctionalDependency> functionalDependencies =
                    createFunctionalDependencyFromArray(b.getDeterminantSet().toArray(new String[0]), a);
            for (AbstractFunctionalDependency functionalDependency : functionalDependencies) {
                System.out.print(functionalDependency.getDeterminantSet() + " -> ");
                System.out.println(functionalDependency.getDependentAttributes());

            }

            for (AbstractFunctionalDependency functionalDependency : functionalDependencies) {
                fdCopy.removeIf(fd -> fd.getDeterminantSet().equals(functionalDependency.getDeterminantSet())
                        && fd.getDependentAttributes().equals(functionalDependency.getDependentAttributes()));
            }
        });

        return fdCopy.isEmpty();
    }

    private Set<AbstractFunctionalDependency> createFunctionalDependencyFromArray(String[] firstOperator, String[] set) {
        int offset = firstOperator.length;

        Set<AbstractFunctionalDependency> functionalDependencies = new HashSet<>();

        int j = 1;
        for (int i = offset; i < set.length; i++) {
            List<List<String>> dependants =
                    combination(subSet(new ArrayList<>(Arrays.asList(set)), Arrays.asList(firstOperator)), j++);
            dependants.forEach(d -> {
                functionalDependencies.add(new FunctionalDependency(
                        new HashSet<>(Arrays.asList(firstOperator)),
                        new HashSet<>(d)
                ));
            });
        }
        functionalDependencies.add(new FunctionalDependency(
                new HashSet<>(Arrays.asList(firstOperator)),
                new HashSet<>(Arrays.asList(firstOperator))
        ));
        return functionalDependencies;
    }

    private static <T> List<T> subSet(ArrayList<T> set, List<T> toBeRemoved) {
        List<T> subSet;
        set.removeAll(toBeRemoved);
        subSet = new ArrayList<>(set);
        return subSet;
    }

    private List<Set<String>> checkMinimalKeyForEachCombination(List<String> args) {
        List<Set<String>> result = new ArrayList<>();
        for (int i = 1; i <= args.size(); i++) {
            List<List<String>> possibleMinimalKeys = combination(args, i); // check minimal keys for first iteration
            List<List<String>> minimalKeys = possibleMinimalKeys.stream()
                    .filter(pmk ->
                            (isKey(pmk.toArray(new String[0]), true))
                    ).collect(Collectors.toList());
            if (!minimalKeys.isEmpty()) {
                minimalKeys.forEach(mk -> result.add(new HashSet<>(mk)));
                break;
            }
        }
        return result;
    }

    public List<List<String>> combination(List<String> values, int size) {

        if (size == 0)
            return Collections.singletonList(Collections.emptyList());

        if (values.isEmpty())
            return Collections.emptyList();

        List<List<String>> currentCombination = new ArrayList<>();
        final String first = values.get(0);

        List<String> subSet = new ArrayList<>(values);
        subSet.remove(first);

        List<List<String>> subSetCombination = combination(subSet, size - 1);

        subSetCombination.forEach(ssc -> {
            List<String> newSet = new ArrayList<>(ssc);
            newSet.add(0, first);
            currentCombination.add(newSet);
        });
        currentCombination.addAll(combination(subSet, size));

        return currentCombination;
    }
}