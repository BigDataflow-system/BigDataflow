package alias_data;

import java.util.HashSet;
import java.util.Set;

public class Singletons {
    public Set<Integer> singletonSet;

    public Singletons() {
        this.singletonSet = new HashSet<>();
    }

    public final boolean isSingleton(int vid) {
        return singletonSet.contains(vid);
    }

    public final void addOneSingleton(int vid) {
        this.singletonSet.add(vid);
    }
}
