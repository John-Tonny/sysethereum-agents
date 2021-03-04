package info.vircletrx.agents.addition;

import java.util.List;
import java.util.concurrent.Callable;
import info.vircletrx.agents.addition.RemoteCall;
import org.tron.tronj.abi.FunctionEncoder;
import org.tron.tronj.abi.FunctionReturnDecoder;
import org.tron.tronj.abi.datatypes.Function;
import org.tron.tronj.abi.datatypes.Type;

public class RemoteFunctionCall<T> extends RemoteCall<T> {
    private final Function function;

    public RemoteFunctionCall(Function function, Callable<T> callable) {
        super(callable);
        this.function = function;
    }

    public String encodeFunctionCall() {
        return FunctionEncoder.encode(this.function);
    }

    public List<Type> decodeFunctionResponse(String response) {
        return FunctionReturnDecoder.decode(response, this.function.getOutputParameters());
    }
}

