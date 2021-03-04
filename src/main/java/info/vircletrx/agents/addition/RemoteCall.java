package info.vircletrx.agents.addition;

import io.reactivex.Flowable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import org.web3j.utils.Async;

public class RemoteCall<T> {
    private Callable<T> callable;

    public RemoteCall(Callable<T> callable) {
        this.callable = callable;
    }

    public T send() throws Exception {
        return this.callable.call();
    }

    public CompletableFuture<T> sendAsync() {
        return Async.run(this::send);
    }

    public Flowable<T> flowable() {
        return Flowable.fromCallable(this::send);
    }
}

