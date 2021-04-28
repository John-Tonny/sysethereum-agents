package info.vircletrx.agents.addition;

import info.vircletrx.agents.constants.SystemProperties;
import info.vircletrx.agents.constants.TrxAddresses;
import info.vircletrx.agents.contract.VircleBattleManagerExtended;
import info.vircletrx.agents.core.bridge.BattleContractApi;
import info.vircletrx.agents.core.bridge.ClaimContractApi;
import info.vircletrx.agents.core.bridge.SuperblockContractApi;
import info.vircletrx.agents.core.vircle.SuperblockChain;
import info.vircletrx.agents.core.vircle.VircleWrapper;
import info.vircletrx.agents.util.JsonGasRanges;
import org.bitcoinj.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.tron.tronj.abi.FunctionEncoder;
import org.tron.tronj.api.WalletGrpc;
import org.tron.tronj.proto.Chain.Transaction;
import org.tron.tronj.proto.Chain.TransactionOrBuilder;
import org.tron.tronj.proto.Response;
import org.tron.tronj.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;


import org.tron.tronj.client.TronClient;
import org.tron.tronj.client.contract.Contract;

import org.tron.tronj.client.contract.Trc20Contract;
import org.tron.tronj.client.exceptions.IllegalException;
import org.tron.tronj.client.transaction.TransactionBuilder;

import org.tron.tronj.proto.Response.TransactionExtention;
import org.tron.tronj.proto.Response.TransactionReturn;

import org.tron.tronj.abi.FunctionReturnDecoder;
import org.tron.tronj.abi.datatypes.Function;
import org.tron.tronj.abi.datatypes.Type;
import org.tron.tronj.utils.Strings;

import org.tron.tronj.proto.Contract.TriggerSmartContract;

public class TrxContract {

    private final TronClient client;
    private final String contractAddress;
    private final String ownerAddress;
    private final Contract contract;

    public TrxContract(
            TronClient client,
            String contractAddress,
            String ownerAddress
    )  {
        this.client = client;
        this.contractAddress = contractAddress;
        this.ownerAddress = ownerAddress;
        this.contract = this.client.getContract(this.contractAddress);
    }

    public TronClient getClient(){
        return this.client;
    }

    public Contract getContract(){
        return this.contract;
    }

    public String getOwnerAddress(){
        return this.ownerAddress;
    }

    protected List<Type> executeConstantCall(Function function) throws IOException {
        TransactionExtention txExt = this.client.constantCall(this.ownerAddress, this.contractAddress, function);
        if(txExt.getConstantResultCount()==0){
            return null;
        }
        String values = Numeric.toHexStringNoPrefix (txExt.getConstantResult(0).toByteArray());
        return FunctionReturnDecoder.decode(values, function.getOutputParameters());
    }

    protected  <T extends Type> T executeCallSingleValueReturn(Function function) throws IOException {
        List<Type> values = this.executeConstantCall(function);
        return !values.isEmpty() ? (T)values.get(0) : null;
    }

    /*
    protected <T extends Type, R> R executeCallSingleValueReturn(Function function, Class<R> returnType) throws IOException {
        T result = this.executeCallSingleValueReturn(function);
        if (result == null) {
            throw new ContractCallException("Empty value (0x) returned from contract");
        } else {
            Object value = result.getValue();
            if (returnType.isAssignableFrom(value.getClass())) {
                return value;
            } else if (result.getClass().equals(Address.class) && returnType.equals(String.class)) {
                return result.toString();
            } else {
                throw new ContractCallException("Unable to convert response: " + value + " to expected type: " + returnType.getSimpleName());
            }
        }
    }*/

    public  <T extends Type> T executeRemoteCallSingleValueReturn(Function function) {
        try {
            return this.executeCallSingleValueReturn(function);
        }catch (IOException e){
            return null;
        }
    }

    /*
    protected <T> RemoteFunctionCall<T> executeRemoteCallSingleValueReturn(Function function, Class<T> returnType) {
        return new RemoteFunctionCall(function, () -> {
            return this.executeCallSingleValueReturn(function, returnType);
        });
    }
    */

    public List<Type> executeCallMultipleValueReturn(Function function) {
        try {
            return this.executeConstantCall(function);
        }catch (IOException e){
            return null;
        }
    }


    protected TransactionReturn executeTransaction(Function function) {
        return this.executeTransaction(function, BigInteger.ZERO);
    }

    protected TransactionReturn executeTransaction(Function function, BigInteger weiValue) {
        String encodedHex = FunctionEncoder.encode(function);


        TriggerSmartContract trigger = TriggerSmartContract.newBuilder()
                .setOwnerAddress(TronClient.parseAddress(this.ownerAddress))
                .setContractAddress(TronClient.parseAddress(this.contractAddress))
                .setCallValue(weiValue.longValue())
                .setData(TronClient.parseHex(encodedHex))
                .build();

        // john 20210427
        TransactionExtention txnExt = this.client.triggerContract(trigger);

        Transaction unsignedTxn = txnExt.getTransaction().toBuilder()
                 .setRawData(txnExt.getTransaction().getRawData().toBuilder().setFeeLimit(100000000L))
                 .build();

        Transaction signedTxn = this.client.signTransaction(unsignedTxn);

        return this.client.broadcastTransaction(signedTxn);

    }

    public TransactionReturn executeRemoteCallTransaction(Function function) {
        return this.executeTransaction(function);
    }

    public TransactionReturn executeRemoteCallTransaction(Function function, BigInteger weiValue) {
        return this.executeTransaction(function, weiValue);
    }

    public static String toHexStringWithNoPrefix(BigInteger value) {
        String result = Numeric.toHexStringNoPrefix(value);
        if (result.length()%2 != 0) {
            result = Strings.zeros(1) + result;
        }
        return result;
    }
}
