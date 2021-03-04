package info.vircletrx.agents.addition;


import info.vircletrx.agents.addition.Tuple;

public final class Tuple6<T1, T2, T3, T4, T5, T6> implements Tuple {
    private static final int SIZE = 6;
    private final T1 value1;
    private final T2 value2;
    private final T3 value3;
    private final T4 value4;
    private final T5 value5;
    private final T6 value6;

    public Tuple6(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
        this.value5 = value5;
        this.value6 = value6;
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

    /** @deprecated */
    @Deprecated
    public T4 getValue4() {
        return this.value4;
    }

    public T4 component4() {
        return this.value4;
    }

    /** @deprecated */
    @Deprecated
    public T5 getValue5() {
        return this.value5;
    }

    public T5 component5() {
        return this.value5;
    }

    /** @deprecated */
    @Deprecated
    public T6 getValue6() {
        return this.value6;
    }

    public T6 component6() {
        return this.value6;
    }

    public int getSize() {
        return 6;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Tuple6 tuple6;
            label77: {
                tuple6 = (Tuple6)o;
                if (this.value1 != null) {
                    if (this.value1.equals(tuple6.value1)) {
                        break label77;
                    }
                } else if (tuple6.value1 == null) {
                    break label77;
                }

                return false;
            }

            label70: {
                if (this.value2 != null) {
                    if (this.value2.equals(tuple6.value2)) {
                        break label70;
                    }
                } else if (tuple6.value2 == null) {
                    break label70;
                }

                return false;
            }

            if (this.value3 != null) {
                if (!this.value3.equals(tuple6.value3)) {
                    return false;
                }
            } else if (tuple6.value3 != null) {
                return false;
            }

            label56: {
                if (this.value4 != null) {
                    if (this.value4.equals(tuple6.value4)) {
                        break label56;
                    }
                } else if (tuple6.value4 == null) {
                    break label56;
                }

                return false;
            }

            if (this.value5 != null) {
                if (this.value5.equals(tuple6.value5)) {
                    return this.value6 != null ? this.value6.equals(tuple6.value6) : tuple6.value6 == null;
                }
            } else if (tuple6.value5 == null) {
                return this.value6 != null ? this.value6.equals(tuple6.value6) : tuple6.value6 == null;
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
        result = 31 * result + (this.value4 != null ? this.value4.hashCode() : 0);
        result = 31 * result + (this.value5 != null ? this.value5.hashCode() : 0);
        result = 31 * result + (this.value6 != null ? this.value6.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Tuple6{value1=" + this.value1 + ", value2=" + this.value2 + ", value3=" + this.value3 + ", value4=" + this.value4 + ", value5=" + this.value5 + ", value6=" + this.value6 + "}";
    }
}
