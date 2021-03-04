package info.vircletrx.agents.addition;

import info.vircletrx.agents.addition.Tuple;

public final class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> implements Tuple {
    private static final int SIZE = 9;
    private final T1 value1;
    private final T2 value2;
    private final T3 value3;
    private final T4 value4;
    private final T5 value5;
    private final T6 value6;
    private final T7 value7;
    private final T8 value8;
    private final T9 value9;

    public Tuple9(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
        this.value5 = value5;
        this.value6 = value6;
        this.value7 = value7;
        this.value8 = value8;
        this.value9 = value9;
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

    /** @deprecated */
    @Deprecated
    public T7 getValue7() {
        return this.value7;
    }

    public T7 component7() {
        return this.value7;
    }

    /** @deprecated */
    @Deprecated
    public T8 getValue8() {
        return this.value8;
    }

    public T8 component8() {
        return this.value8;
    }

    /** @deprecated */
    @Deprecated
    public T9 getValue9() {
        return this.value9;
    }

    public T9 component9() {
        return this.value9;
    }

    public int getSize() {
        return 9;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?> tuple9 = (Tuple9)o;
            if (this.value1 != null) {
                if (!this.value1.equals(tuple9.value1)) {
                    return false;
                }
            } else if (tuple9.value1 != null) {
                return false;
            }

            label106: {
                if (this.value2 != null) {
                    if (this.value2.equals(tuple9.value2)) {
                        break label106;
                    }
                } else if (tuple9.value2 == null) {
                    break label106;
                }

                return false;
            }

            if (this.value3 != null) {
                if (!this.value3.equals(tuple9.value3)) {
                    return false;
                }
            } else if (tuple9.value3 != null) {
                return false;
            }

            label92: {
                if (this.value4 != null) {
                    if (this.value4.equals(tuple9.value4)) {
                        break label92;
                    }
                } else if (tuple9.value4 == null) {
                    break label92;
                }

                return false;
            }

            if (this.value5 != null) {
                if (!this.value5.equals(tuple9.value5)) {
                    return false;
                }
            } else if (tuple9.value5 != null) {
                return false;
            }

            if (this.value6 != null) {
                if (!this.value6.equals(tuple9.value6)) {
                    return false;
                }
            } else if (tuple9.value6 != null) {
                return false;
            }

            label71: {
                if (this.value7 != null) {
                    if (this.value7.equals(tuple9.value7)) {
                        break label71;
                    }
                } else if (tuple9.value7 == null) {
                    break label71;
                }

                return false;
            }

            if (this.value8 != null) {
                if (this.value8.equals(tuple9.value8)) {
                    return this.value9 != null ? this.value9.equals(tuple9.value9) : tuple9.value9 == null;
                }
            } else if (tuple9.value8 == null) {
                return this.value9 != null ? this.value9.equals(tuple9.value9) : tuple9.value9 == null;
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
        result = 31 * result + (this.value7 != null ? this.value7.hashCode() : 0);
        result = 31 * result + (this.value8 != null ? this.value8.hashCode() : 0);
        result = 31 * result + (this.value9 != null ? this.value9.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Tuple9{value1=" + this.value1 + ", value2=" + this.value2 + ", value3=" + this.value3 + ", value4=" + this.value4 + ", value5=" + this.value5 + ", value6=" + this.value6 + ", value7=" + this.value7 + ", value8=" + this.value8 + ", value9=" + this.value9 + "}";
    }
}