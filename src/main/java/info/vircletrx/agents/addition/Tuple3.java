package info.vircletrx.agents.addition;


import info.vircletrx.agents.addition.Tuple;

public final class Tuple3<T1, T2, T3> implements Tuple {
    private static final int SIZE = 3;
    private final T1 value1;
    private final T2 value2;
    private final T3 value3;

    public Tuple3(T1 value1, T2 value2, T3 value3) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
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

    /** @deprecated */
    @Deprecated
    public T3 getValue3() {
        return this.value3;
    }

    public T3 component3() {
        return this.value3;
    }

    public int getSize() {
        return 3;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Tuple3 tuple3;
            label41: {
                tuple3 = (Tuple3)o;
                if (this.value1 != null) {
                    if (this.value1.equals(tuple3.value1)) {
                        break label41;
                    }
                } else if (tuple3.value1 == null) {
                    break label41;
                }

                return false;
            }

            if (this.value2 != null) {
                if (this.value2.equals(tuple3.value2)) {
                    return this.value3 != null ? this.value3.equals(tuple3.value3) : tuple3.value3 == null;
                }
            } else if (tuple3.value2 == null) {
                return this.value3 != null ? this.value3.equals(tuple3.value3) : tuple3.value3 == null;
            }

            return false;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.value1.hashCode();
        result = 31 * result + (this.value2 != null ? this.value2.hashCode() : 0);
        result = 31 * result + (this.value3 != null ? this.value3.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Tuple3{value1=" + this.value1 + ", value2=" + this.value2 + ", value3=" + this.value3 + "}";
    }
}
