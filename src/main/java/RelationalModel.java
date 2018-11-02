import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RelationalModel extends AbstractRelationalModel {

    private List<String> attributes;
    private List<Pair<String[], String[]>> keyValues;

    public RelationalModel() {
        this.attributes = new ArrayList<>();
        this.keyValues = new ArrayList<>();
    }

    public String[] calcClosure(String[] args) {
        List<String> current = new ArrayList<>(Arrays.asList(args));
        List<Pair<String[], String[]>> keyValuesCopy = new ArrayList<>(this.keyValues);

        for (int i = 0; i < current.size(); i++) {
            boolean changed = false;
            for (int j = 0; j < keyValuesCopy.size(); j++) {
                Pair<String[], String[]> currentPair = keyValuesCopy.get(j);

                List<String> relationKeyAsList = Arrays.asList(currentPair.getKey());
                List<String> relationValueAsList = Arrays.asList(currentPair.getValue());

                if (current.containsAll(relationKeyAsList)) {
                    if (!Arrays.equals(currentPair.getKey(), currentPair.getValue()) && !current.containsAll(relationValueAsList)) {
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

    public boolean isKey(List<String> args, boolean minimal) {

        boolean isKey = false;
        List<String> closure = new ArrayList<>(args);

        Collections.sort(this.attributes);
        Collections.sort(closure);

        if (this.attributes.equals(closure))
            isKey = true;

        if (isKey && minimal) {
            List<String> currentSet = new ArrayList<>(args);
            for (Pair<String[], String[]> currentPair : this.keyValues) {
                List<String> keyValueCombined = new ArrayList<>(Arrays.asList(currentPair.getKey()));
                List<String> relationValueAsList = new ArrayList<>(Arrays.asList(currentPair.getValue()));

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
            this.keyValues.add(Pair.makePair(fd.getDeterminantSet(), fd.getDependentAttributes()));
        });
    }

    @Override
    public Collection<Set<String>> getMinimalKeys() {
        return checkMinimalKeyForEachCombination();
    }

    @Override
    public boolean isBase(Set<AbstractFunctionalDependency> base) {
        return false;
    }

    private void checkMinimalKeyForEachCombination(List<String> args) {

        for (int i = 1; i <= args.size(); i++) {
            List<List<String>> possibleMinimalKeys = combination(args, i); // check minimal keys for first iteration
            List<List<String>> minimalKeys = possibleMinimalKeys.stream()
                    .filter(pmk ->
                            (isKey(pmk, true))
                    ).collect(Collectors.toList());
            if (!minimalKeys.isEmpty())
                break;
        }
    }

    private List<List<String>> combination(List<String> values, int size) {

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

    public static class Pair<Key, Value> {
        private Key key;
        private Value value;

        public static <Key, Value> Pair makePair(Key key, Value value) {
            return new Pair<>(key, value);
        }

        private Pair(Key key, Value value) {
            this.key = key;
            this.value = value;
        }

        public Key getKey() {
            return key;
        }

        public Value getValue() {
            return value;
        }
    }
}