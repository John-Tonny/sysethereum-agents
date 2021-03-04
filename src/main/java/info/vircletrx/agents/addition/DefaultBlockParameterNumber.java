package info.vircletrx.agents.addition;

import com.fasterxml.jackson.annotation.JsonValue;
import org.tron.tronj.utils.Numeric;

import java.math.BigInteger;

public class DefaultBlockParameterNumber implements DefaultBlockParameter {
    private BigInteger blockNumber;

    public DefaultBlockParameterNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public DefaultBlockParameterNumber(long blockNumber) {
        this(BigInteger.valueOf(blockNumber));
    }

    @JsonValue
    public String getValue() {
        return Numeric.encodeQuantity(this.blockNumber);
    }

    public BigInteger getBlockNumber() {
        return this.blockNumber;
    }
}

