package info.vircletrx.agents.addition;


import info.vircletrx.agents.addition.Tuple;

public final class Tuple2<T1, T2> implements Tuple {
    private static final int SIZE = 2;
    private final T1 value1;
    private final T2 value2;

    public Tuple2(T1 value1, T2 value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    /** @deprecated */
    @Deprecated
    public T1 getValue1() {
        return this.value1;
    }

    public T1 component1() {
        return this.value1;
    }

    /** @deprecated */
    @Deprecated
    public T2 getValue2() {
        return this.value2;
    }

    public T2 component2() {
        return this.value2;
    }

    public int getSize() {
        return 2;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Tuple2<?, ?> tuple2 = (Tuple2)o;
            if (this.value1 != null) {
                if (this.value1.equals(tuple2.value1)) {
                    return this.value2 != null ? this.value2.equals(tuple2.value2) : tuple2.value2 == null;
                }
            } else if (tuple2.value1 == null) {
                return this.value2 != null ? this.value2.equals(tuple2.value2) : tuple2.value2 == null;
            }

            return false;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.value1.hashCode();
        result = 31 * result + (this.value2 != null ? this.value2.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Tuple2{value1=" + this.value1 + ", value2=" + this.value2 + "}";
    }
}
