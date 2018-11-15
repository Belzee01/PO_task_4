import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.fail;

public class PMO_Test {
    //////////////////////////////////////////////////////////////////////////
    private static final Map<String, Double> tariff = new HashMap<>();

    static {
        tariff.put("noFD", 0.3);
        tariff.put("trivialFD", 0.4);
        tariff.put("isKeyTest", 0.5);
        tariff.put("base", 0.8);
        tariff.put("base2", 0.7);
        tariff.put("base3", 0.01);
        tariff.put("notBase", 0.6);
        tariff.put("notBase2", 0.6);
    }

    private AbstractRelationalModel model;
    //////////////////////////////////////////////////////////////////////////

    public static double getTariff(String testName) {
        return tariff.get(testName);
    }

    ////////////////////////////////////////////////
    private static void showException(Exception e, String txt) {
        e.printStackTrace();
        fail(txt + " " + e.toString());
    }

    public boolean equals(Collection<String> l1, Collection<String> l2) {
        return l1.containsAll(l2) && l2.containsAll(l1);
    }
////////////////////////////////////////////////

    @BeforeEach
    public void createRelationalModel() {
        model = new RelationalModel();
    }

    private void sendFunctionalDependencies(Set<AbstractFunctionalDependency> functions) {

        try {
            assertTimeoutPreemptively(java.time.Duration.ofMillis(250), () -> {
                model.setFunctionalDependencies(functions);
            });
        } catch (Exception e) {
            showException(e, "BĹÄd w trakcie wykonywania metody addFunctionalDependency");
        }
    }

    private void sendAttributes(String[] attributes) {
        sendAttributes(PMO_FunctionalDependency.table2set(attributes));
    }

    private void sendAttributes(Set<String> attributes) {
//		System.out.println("sendAttributes: " + Arrays.toString(attributes));
        try {
            assertTimeoutPreemptively(java.time.Duration.ofMillis(250), () -> {
                model.setAttributes(attributes);
            });
        } catch (Exception e) {
            showException(e, "BĹÄd w trakcie wykonywania metody setAttributes");
        }
    }

    private void configureRelationalModel(String[] functions, String additionalAttributes) {

        Set<String> attributes = new TreeSet<>();
        Set<AbstractFunctionalDependency> functionsSet = new HashSet<>();

        if (additionalAttributes.length() > 0)
            attributes.addAll(PMO_FunctionalDependency.splitString2Set(additionalAttributes));

        attributes.addAll(Arrays.stream(functions).flatMap(f -> Arrays.stream(PMO_FunctionalDependency.splitL(f)))
                .map(f -> f.trim()).collect(Collectors.toSet()));
        attributes.addAll(Arrays.stream(functions).flatMap(f -> Arrays.stream(PMO_FunctionalDependency.splitR(f)))
                .map(f -> f.trim()).collect(Collectors.toSet()));

        sendAttributes(PMO_FunctionalDependency.collection2table(attributes));
        Arrays.stream(functions).forEach(f -> functionsSet.add(new PMO_FunctionalDependency(f)));
        sendFunctionalDependencies(functionsSet);
    }

    private Collection<Set<String>> getMinimalKeys() {

        Collection<Set<String>> result = null;
        try {
            result = assertTimeoutPreemptively(java.time.Duration.ofMillis(500), () -> {
                return new HashSet<>(model.getMinimalKeys());
            });
        } catch (Exception e) {
            showException(e, "BĹÄd w trakcie wykonywania metody getMinimalKeys");
        }

        assertNotNull("BĹÄd: wynikie pracy getMinimalKeys jest NULL", result);

        return result;
    }

    private boolean isBase(Set<AbstractFunctionalDependency> base) {
        boolean result = false;
        try {
            result = assertTimeoutPreemptively(java.time.Duration.ofMillis(500), () -> {
                return model.isBase(base);
            });
        } catch (Exception e) {
            showException(e, "BĹÄd w trakcie wykonywania metody isBase");
        }
        assertNotNull("BĹÄd: wynikie pracy isBase jest NULL", result);
        return result;
    }

    private Set<AbstractFunctionalDependency> table2fd(String[] functions) {
        return Arrays.stream(functions).map(PMO_FunctionalDependency::new).collect(Collectors.toSet());
    }

    private static String keyDescription(String[] args, boolean minimal) {
        return Arrays.toString(args)
                + (minimal ? " test jako klucza minimalnego " : " jako test klucza nieminimalnego");
    }

    private boolean expectedKeyFound(Set<String> expected, Collection<Set<String>> actual) {
        return actual.stream().anyMatch(k -> equals(k, expected));
    }

    private void testKey(Set<String> expected, Collection<Set<String>> actual) {
        if (!expectedKeyFound(expected, actual)) {
            fail("WĹrĂłd zaproponowanych kluczy brak " + expected + " jest " + actual);
        }
    }

    private void testKeys(Collection<Set<String>> expected) {
        Collection<Set<String>> actual = getMinimalKeys();

        expected.forEach(ke -> testKey(ke, actual));
    }
////////////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("Klucz dla relacji bez ZF")
    public void noFD() {
        sendAttributes(Set.of("A", "B", "C"));
        Collection<Set<String>> keysExpected = Set.of(Set.of("A", "B", "C"));
        testKeys(keysExpected);
    }

    @Test
    @DisplayName("Klucz dla relacji z trywialnÄ ZF")
    public void trivialFD() {
        configureRelationalModel(new String[]{"A->A"}, "B,C,D");
        Collection<Set<String>> keysExpected = Set.of(Set.of("A", "B", "C", "D"));
        testKeys(keysExpected);
    }

    private static Stream<Arguments> keyTestDataProvider() {
        return Stream.of(Arguments.of(new String[]{"A->B,C"}, "D,E", Set.of(Set.of("A", "D", "E"))),
                Arguments.of(new String[]{"A->B", "A->C"}, "D,E", Set.of(Set.of("A", "D", "E"))),
                Arguments.of(new String[]{"A->B", "D->E"}, "C", Set.of(Set.of("A", "C", "D"))),
                Arguments.of(new String[]{"A->B", "B->A", "B->C,D,E"}, "", Set.of(Set.of("A"), Set.of("B"))),
                Arguments.of(new String[]{"A->B", "B->A", "B->C,D,E"}, "F",
                        Set.of(Set.of("A", "F"), Set.of("B", "F"))),
                Arguments.of(new String[]{"A,B->C,D", "E->F"}, "G", Set.of(Set.of("A", "B", "E", "G"))));
    }

    @ParameterizedTest
    @DisplayName("Test metody generujÄcej klucz")
    @MethodSource("keyTestDataProvider")
    public void isKeyTest(String[] functions, String additionalAttributes, Collection<Set<String>> expected) {
        configureRelationalModel(functions, additionalAttributes);
        testKeys(expected);
    }

    @Test
    @DisplayName("Test bazy - propozycja jest poprawna")
    public void base() {
        configureRelationalModel(new String[]{"A->B", "A->C", "B->C"}, "D");

        boolean result = isBase(table2fd(new String[]{"A->B", "B->C"}));

        assertEquals(true, result, "BĹÄd w teĹcie propozycji bazy");
    }

    @Test
    @DisplayName("Test bazy - propozycja jest poprawna")
    public void base2() {
        configureRelationalModel(new String[]{"A->B", "A->C", "B->C", "B->A", "C->A"}, "D");

        boolean result = isBase(table2fd(new String[]{"A->B", "B->C", "C->A"}));

        assertEquals(true, result, "BĹÄd w teĹcie propozycji bazy");
    }

    @Test
    @DisplayName("Test bazy - propozycja jest poprawna, E->E wynika samo z siebie")
    public void base3() {
        configureRelationalModel(new String[]{"A->B", "A->C", "B->C", "E->E"}, "D");

        boolean result = isBase(table2fd(new String[]{"A->B", "B->C"}));

        assertEquals(true, result, "BĹÄd w teĹcie propozycji bazy");
    }

    @Test
    @DisplayName("Test bazy - propozycja jest bĹÄdna, brak A->B")
    public void notBase() {
        configureRelationalModel(new String[]{"A->B", "A->C", "B->C"}, "E");

        boolean result = isBase(table2fd(new String[]{"A->C", "B->C"}));

        assertEquals(false, result, "BĹÄd w teĹcie propozycji bazy");
    }

    @Test
    @DisplayName("Test bazy - propozycja jest bĹÄdna, brak E->F")
    public void notBase2() {
        configureRelationalModel(new String[]{"A->B", "A->C", "B->C", "E->FG"}, "");

        boolean result = isBase(table2fd(new String[]{"A->C", "B->C", "E->G"}));

        assertEquals(false, result, "BĹÄd w teĹcie propozycji bazy");
    }

}