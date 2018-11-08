//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.TreeSet;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class PMO_Test {
//    //////////////////////////////////////////////////////////////////////////
//    private static final Map<String, Double> tariff = new HashMap<>();
//
//    static {
//        tariff.put("simpleClosure", 1.0);
//        tariff.put("closureWithAdditionalFD", 0.8);
//        tariff.put("closureWithTrivialAdditionalFD", 0.6);
//        tariff.put("closureWithLongAttributeNames", 0.6);
//        tariff.put("closureWithMultipleAttributesLeft", 0.4);
//        tariff.put("closureWithMultipleAttributesRight", 0.4);
//        tariff.put("isKeyTest", 0.5);
//    }
//
//    private RelationalModel model;
//    //////////////////////////////////////////////////////////////////////////
//
//    public static double getTariff(String testName) {
//        return tariff.get(testName);
//    }
//
//    ////////////////////////////////////////////////
//    private static void showException(Exception e, String txt) {
//        e.printStackTrace();
//        fail(txt + " " + e.toString());
//    }
//
//    private static Set<String> splitString2Set(String string) {
//        return new TreeSet<>(Arrays.stream(string.split(",")).map(f -> f.trim()).collect(Collectors.toSet()));
//    }
//
//    private static String[] splitString2Table(String string) {
//        return collection2table(splitString2Set(string));
//    }
//
//    private static String[] collection2table(Collection<String> collection) {
//        return collection.toArray(new String[collection.size()]);
//    }
//
//    private static Collection<String> table2collection(String... strings) {
//        return Arrays.asList(strings);
//    }
//
//    private boolean equals(Collection<String> l1, Collection<String> l2) {
//        return l1.containsAll(l2) && l2.containsAll(l1);
//    }
//////////////////////////////////////////////////////
//
//    private static String[] splitL(String functionalDependency) {
//        return functionalDependency.split("->")[0].split(",");
//    }
//
//    private static String[] splitR(String functionalDependency) {
//        return functionalDependency.split("->")[1].split(",");
//    }
//
//    @BeforeEach
//    public void createRelationalModel() {
//        model = new RelationalModel();
//    }
//
//    private void sendFunctionalDependency(String functionalDependency) {
//        String[] argL = splitL(functionalDependency);
//        String[] argR = splitR(functionalDependency);
//
//        try {
//            assertTimeoutPreemptively(java.time.Duration.ofMillis(250), () -> {
//                model.setFunctionalDependencies(argL, argR);
//            });
//        } catch (Exception e) {
//            showException(e, "BĹÄd w trakcie wykonywania metody addFunctionalDependency");
//        }
//    }
//
//    private void sendAttributes(String[] attributes) {
////		System.out.println("sendAttributes: " + Arrays.toString(attributes));
//        try {
//            assertTimeoutPreemptively(java.time.Duration.ofMillis(250), () -> {
//                model.setAttributes(attributes);
//            });
//        } catch (Exception e) {
//            showException(e, "BĹÄd w trakcie wykonywania metody setAttributes");
//        }
//    }
//
//    private void configureRelationalModel(String[] functions, String additionalAttributes) {
//
//        Set<String> attributes = new TreeSet<>();
//
//        if (additionalAttributes.length() > 0)
//            attributes.addAll(splitString2Set(additionalAttributes));
//
//        attributes.addAll(Arrays.stream(functions).flatMap(f -> Arrays.stream(splitL(f))).map(f -> f.trim())
//                .collect(Collectors.toSet()));
//        attributes.addAll(Arrays.stream(functions).flatMap(f -> Arrays.stream(splitR(f))).map(f -> f.trim())
//                .collect(Collectors.toSet()));
//
//        sendAttributes(collection2table(attributes));
//        Arrays.stream(functions).forEach(this::sendFunctionalDependency);
//    }
//
//    private Collection<String> calcClosure(String... args) {
//
//        String[] result = null;
//        try {
//            result = assertTimeoutPreemptively(java.time.Duration.ofMillis(500), () -> {
//                return model.calcClosure(args);
//            });
//        } catch (Exception e) {
//            showException(e, "BĹÄd w trakcie wykonywania metody calcClosure dla " + Arrays.toString(args));
//        }
//
//        assertNotNull("BĹÄd: wynikie pracy calcClosure jest NULL", result);
//
//        return table2collection(result);
//    }
//
//    private static String keyDescription(String[] args, boolean minimal) {
//        return Arrays.toString(args)
//                + (minimal ? " test jako klucza minimalnego " : " jako test klucza nieminimalnego");
//    }
//
//    private boolean executeIsKey(boolean minimal, String... args) {
//        boolean result = false;
//        try {
//            result = assertTimeoutPreemptively(java.time.Duration.ofMillis(500), () -> {
//                return model.isKey(args, minimal);
//            });
//        } catch (Exception e) {
//            showException(e, "BĹÄd w trakcie wykonywania metody isKey dla " + keyDescription(args, minimal));
//        }
//
//        return result;
//    }
//
//    @Test
//    @DisplayName("Proste obliczenia domkniÄcia")
//    public void simpleClosure() {
//        String[] functions = new String[] { "A->B", "C->D", "A->E" };
//        String expectedString = "A,C,B,E,D";
//        String attributes = "A,C";
//
//        closureTest(functions, "", expectedString, attributes);
//    }
//
//    private void closureTest(String[] functions, String additionalAttributes, String expectedString,
//                             String attributes) {
//        configureRelationalModel(functions, additionalAttributes);
//        Collection<String> result = calcClosure(splitString2Table(attributes));
//        Collection<String> expected = splitString2Set(expectedString);
//
//        if (!equals(result, expected)) {
//            fail("BĹÄdny wynik obliczeĹ domkniÄcia. Oczekiwano: " + expected + " a jest " + result);
//        }
//    }
//
//    @Test
//    @DisplayName("DomkniÄcia z nieuĹźywanymi ZF")
//    public void closureWithAdditionalFD() {
//        String[] functions = new String[] { "A->B", "C->D", "A->E", "G->H", "Z->G" };
//        String expectedString = "A,C,B,E,D";
//        String attributes = "A,C";
//
//        closureTest(functions, "", expectedString, attributes);
//    }
//
//    @Test
//    @DisplayName("DomkniÄcia z trywialnymi ZF")
//    public void closureWithTrivialAdditionalFD() {
//        String[] functions = new String[] { "A->B", "A->A", "C->D", "A->E", "B->B", "G->H", "Z->G" };
//        String expectedString = "A,C,B,E,D";
//        String attributes = "A,C";
//
//        closureTest(functions, "", expectedString, attributes);
//    }
//
//    @Test
//    @DisplayName("DomkniÄcia z dlugimi nazwami atrybutow")
//    public void closureWithLongAttributeNames() {
//        String[] functions = new String[] { "Pesel->Imie1", "Pesel->Imie2", "Pesel->Nazwisko", "Kod->Miasto" };
//        String expectedString = "Pesel,Kod,ID,Miasto,Imie1,Imie2,Nazwisko";
//        String attributes = "Pesel,Kod,ID";
//
//        closureTest(functions, "nrTelefonu,wiek", expectedString, attributes);
//    }
//
//    @Test
//    @DisplayName("DomkniÄcia ZF z wieloma atrybutami po stronie lewej")
//    public void closureWithMultipleAttributesLeft() {
//        String[] functions = new String[] { "A,B->C", "A->C", "A->D", "A,C,D->E", "F->G", "F,G->R" };
//        String expectedString = "A,B,C,D,E,F,G,R";
//        String attributes = "A,B,F";
//
//        closureTest(functions, "H", expectedString, attributes);
//    }
//
//    @Test
//    @DisplayName("DomkniÄcia ZF z wieloma atrybutami po stronie prawej")
//    public void closureWithMultipleAttributesRight() {
//        String[] functions = new String[] { "A->C,D", "E->F,G", "B->C", "G->H" };
//        String expectedString = "A,E,C,D,F,G,H";
//        String attributes = "A,E";
//
//        closureTest(functions, "H2,H3", expectedString, attributes);
//    }
//
//    private static Stream<Arguments> isKeyTestDataProvider() {
//        return Stream.of(Arguments.of(new String[] { "A->E", "B->C" }, "D", "B,A", true, false),
//                Arguments.of(new String[] { "A->E", "B->C" }, "D", "B,A", false, false),
//                Arguments.of(new String[] { "A->E", "B->C" }, "D", "B,A,D", true, true),
//                Arguments.of(new String[] { "A->E", "B->C" }, "D", "B,D", false, false),
//                Arguments.of(new String[] { "A->E", "B->C" }, "D", "A,D", false, false),
//                Arguments.of(new String[] { "A->E", "B->C" }, "D", "D,A,B", false, true),
//                Arguments.of(new String[] { "A->E", "B->C" }, "D", "A,C,B,D", false, true),
//                Arguments.of(new String[] { "A->E", "B->C" }, "D", "A,C,B,D", true, false),
//                Arguments.of(new String[] { "Pesel->Imie,Nazwisko,Wiek", "NIP->Pesel", "Pesel->NIP" }, "", "NIP", true,
//                        true),
//                Arguments.of(new String[] { "Pesel->Imie,Nazwisko,Wiek", "NIP->Pesel", "Pesel->NIP" }, "", "NIP,Pesel",
//                        false, true),
//                Arguments.of(new String[] { "Pesel->Imie,Nazwisko,Wiek", "NIP->Pesel", "Pesel->NIP" }, "", "Pesel",
//                        true, true),
//                Arguments.of(new String[] { "Pesel->Imie,Nazwisko,Wiek", "NIP->Pesel", "Pesel->NIP" }, "",
//                        "Imie,Nazwisko", false, false),
//                Arguments.of(new String[] { "Pesel->Imie,Nazwisko,Wiek", "NIP->Pesel", "Pesel->NIP" }, "",
//                        "Pesel,Nazwisko", true, false),
//                Arguments.of(new String[] { "Pesel->Imie,Nazwisko,Wiek", "NIP->Pesel", "Pesel->NIP" }, "", "NIP", false,
//                        true),
//                Arguments.of(new String[] { "Pesel->Imie,Nazwisko,Wiek", "Pesel->NIP" }, "", "Pesel", false, true));
//    }
//
//    @ParameterizedTest
//    @DisplayName("Test metody isKey")
//    @MethodSource("isKeyTestDataProvider")
//    public void isKeyTest(String[] functions, String additionalAttributes, String keyProposition, boolean minimal,
//                          boolean expectedResult) {
//        configureRelationalModel(functions, additionalAttributes);
//        String[] key = splitString2Table(keyProposition);
//
//        boolean result = executeIsKey(minimal, key);
//
//        assertEquals(expectedResult, result, "Oczekiwano innego wyniku testu klucza " + keyDescription(key, minimal));
//    }
//
//}