import java.util.Set;

public class FunctionalDependency extends AbstractFunctionalDependency {

    private Set<String> determinantSet;
    private Set<String> dependentAttributes;

    public FunctionalDependency(Set<String> determinantSet, Set<String> dependentAttributes) {
        this.determinantSet = determinantSet;
        this.dependentAttributes = dependentAttributes;
    }

    @Override
    public Set<String> getDeterminantSet() {
        return this.determinantSet;
    }

    @Override
    public Set<String> getDependentAttributes() {
        return this.dependentAttributes;
    }
}
