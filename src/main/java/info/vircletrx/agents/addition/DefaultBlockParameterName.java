package info.vircletrx.agents.addition;

import com.fasterxml.jackson.annotation.JsonValue;
import info.vircletrx.agents.addition.DefaultBlockParameter;

public enum DefaultBlockParameterName implements DefaultBlockParameter {
    EARLIEST("earliest"),
    LATEST("latest"),
    PENDING("pending");

    private String name;

    private DefaultBlockParameterName(String name) {
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return this.name;
    }

    public static DefaultBlockParameterName fromString(String name) {
        if (name != null) {
            DefaultBlockParameterName[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                DefaultBlockParameterName defaultBlockParameterName = var1[var3];
                if (name.equalsIgnoreCase(defaultBlockParameterName.name)) {
                    return defaultBlockParameterName;
                }
            }
        }

        return valueOf(name);
    }
}
