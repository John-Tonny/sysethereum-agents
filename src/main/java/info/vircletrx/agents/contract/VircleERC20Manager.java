package info.vircletrx.agents.contract;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.tron.tronj.abi.TypeReference;
import org.tron.tronj.abi.datatypes.*;
import org.tron.tronj.abi.datatypes.generated.Bytes32;
import org.tron.tronj.abi.datatypes.generated.Uint256;
import org.tron.tronj.abi.datatypes.generated.Uint32;
import org.tron.tronj.abi.datatypes.generated.Uint8;
import org.tron.tronj.client.TronClient;
import org.tron.tronj.client.contract.Contract;
import org.tron.tronj.proto.Response.TransactionReturn;

import info.vircletrx.agents.addition.TrxContract;
import info.vircletrx.agents.addition.BaseEventResponse;
import info.vircletrx.agents.addition.Tuple2;
import info.vircletrx.agents.addition.Tuple6;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.5.
 */
@SuppressWarnings("rawtypes")
public class VircleERC20Manager extends Contract {
    private static final String BINARY = "0x608060405234801561001057600080fd5b50d3801561001d57600080fd5b50d2801561002a57600080fd5b50612b648061003a6000396000f3fe6080604052600436106100c4576000357c0100000000000000000000000000000000000000000000000000000000900480636cde8d6f116100815780636cde8d6f1461049f5780638b502b7a1461051a578063af56f158146105c8578063cf496b10146106a4578063f7daeb8514610715578063fe2e97181461078d576100c4565b8063085e7092146100c957806317c047e1146101cd5780631b728920146102625780632f3489c7146102965780635f959b69146102f157806360e14dd014610432575b600080fd5b3480156100d557600080fd5b50d380156100e257600080fd5b50d280156100ef57600080fd5b506101226004803603602081101561010657600080fd5b81019080803563ffffffff1690602001909291905050506107fc565b604051808781526020018681526020018573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018363ffffffff1663ffffffff1681526020018260048111156101b457fe5b60ff168152602001965050505050505060405180910390f35b3480156101d957600080fd5b50d380156101e657600080fd5b50d280156101f357600080fd5b506102606004803603608081101561020a57600080fd5b8101908080359060200190929190803563ffffffff169060200190929190803563ffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506108b9565b005b6102946004803603602081101561027857600080fd5b81019080803563ffffffff169060200190929190505050610bb4565b005b3480156102a257600080fd5b50d380156102af57600080fd5b50d280156102bc57600080fd5b506102ef600480360360208110156102d357600080fd5b81019080803563ffffffff169060200190929190505050610ec9565b005b3480156102fd57600080fd5b50d3801561030a57600080fd5b50d2801561031757600080fd5b50610418600480360360a081101561032e57600080fd5b8101908080359060200190929190803563ffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803560ff1690602001909291908035906020019064010000000081111561039257600080fd5b8201836020820111156103a457600080fd5b803590602001918460018302840111640100000000831117156103c657600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290505050611205565b604051808215151515815260200191505060405180910390f35b34801561043e57600080fd5b50d3801561044b57600080fd5b50d2801561045857600080fd5b506104856004803603602081101561046f57600080fd5b8101908080359060200190929190505050611807565b604051808215151515815260200191505060405180910390f35b3480156104ab57600080fd5b50d380156104b857600080fd5b50d280156104c557600080fd5b50610518600480360360408110156104dc57600080fd5b81019080803563ffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611819565b005b34801561052657600080fd5b50d3801561053357600080fd5b50d2801561054057600080fd5b506105736004803603602081101561055757600080fd5b81019080803563ffffffff169060200190929190505050611a74565b604051808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018263ffffffff1663ffffffff1681526020019250505060405180910390f35b3480156105d457600080fd5b50d380156105e157600080fd5b50d280156105ee57600080fd5b506106a2600480360360e081101561060557600080fd5b810190808035906020019092919080359060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803563ffffffff169060200190929190803560ff169060200190929190505050611ac8565b005b3480156106b057600080fd5b50d380156106bd57600080fd5b50d280156106ca57600080fd5b506106d3611ea1565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561072157600080fd5b50d3801561072e57600080fd5b50d2801561073b57600080fd5b5061078b6004803603604081101561075257600080fd5b81019080803560ff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611ec7565b005b34801561079957600080fd5b50d380156107a657600080fd5b50d280156107b357600080fd5b506107e6600480360360208110156107ca57600080fd5b81019080803563ffffffff169060200190929190505050612050565b6040518082815260200191505060405180910390f35b6000806000806000806000603760008963ffffffff1663ffffffff1681526020019081526020016000209050806000015481600101548260020160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168360030160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168460030160149054906101000a900463ffffffff168560030160189054906101000a900460ff169650965096509650965096505091939550919395565b603360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610961576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260218152602001806129aa6021913960400191505060405180910390fd5b8163ffffffff16603a60008563ffffffff1663ffffffff16815260200190815260200160002060000160149054906101000a900463ffffffff1663ffffffff161015156109f9576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401808060200182810382526031815260200180612abd6031913960400191505060405180910390fd5b610a0284612068565b1515610a76576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260148152602001807f545820616c72656164792070726f63657373656400000000000000000000000081525060200191505060405180910390fd5b60408051908101604052808273ffffffffffffffffffffffffffffffffffffffff1681526020018363ffffffff16815250603a60008563ffffffff1663ffffffff16815260200190815260200160002060008201518160000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060208201518160000160146101000a81548163ffffffff021916908363ffffffff1602179055509050507f5276cc41288d98dae7d6e7ca6412b8335adfb3bd319f2e1dfc3933901d1730338382604051808363ffffffff1663ffffffff1681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a150505050565b6000603760008363ffffffff1663ffffffff168152602001908152602001600020905060016004811115610be457fe5b8160030160189054906101000a900460ff166004811115610c0157fe5b141515610c59576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260518152602001806129146051913960600191505060405180910390fd5b8060030160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610d03576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260518152602001806128066051913960600191505060405180910390fd5b60006002811115610d1057fe5b603960009054906101000a900460ff166002811115610d2b57fe5b14610d3857618ca0610d3d565b620dd7c05b81600001544203111515610d9c576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401808060200182810382526053815260200180612a406053913960600191505060405180910390fd5b633b9aca003410151515610dfb576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260458152602001806129656045913960600191505060405180910390fd5b34603860008463ffffffff1663ffffffff1681526020019081526020016000208190555042816000018190555060028160030160186101000a81548160ff02191690836004811115610e4957fe5b02179055507f1bd938c0559acc36703807b71652dec64b2eed0d54f1716803e65cedc4f55a123383604051808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018263ffffffff1663ffffffff1681526020019250505060405180910390a15050565b6000603760008363ffffffff1663ffffffff168152602001908152602001600020905060026004811115610ef957fe5b8160030160189054906101000a900460ff166004811115610f1657fe5b141515610f6e576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252604b815260200180612aee604b913960600191505060405180910390fd5b610e1081600001544203111515610fd0576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260478152602001806129f96047913960600191505060405180910390fd5b60048160030160186101000a81548160ff02191690836004811115610ff157fe5b021790555060008160020160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506110698260010154603460008560030160149054906101000a900463ffffffff1663ffffffff1663ffffffff168152602001908152602001600020546120b790919063ffffffff16565b603460008460030160149054906101000a900463ffffffff1663ffffffff1663ffffffff168152602001908152602001600020819055506110f38260030160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1683600101548373ffffffffffffffffffffffffffffffffffffffff166121429092919063ffffffff16565b60008260030160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506000603860008663ffffffff1663ffffffff168152602001908152602001600020549050603860008663ffffffff1663ffffffff168152602001908152602001600020600090557f558dcc0f85e822d51fb0c98b95ab299d76c136c9d1a34b9cb2e3ede1689cdcfe8460030160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1686604051808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018263ffffffff1663ffffffff1681526020019250505060405180910390a15050505050565b6000838660008273ffffffffffffffffffffffffffffffffffffffff1663313ce5676040518163ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040160206040518083038186803b15801561126d57600080fd5b505afa158015611281573d6000803e3d6000fd5b505050506040513d602081101561129757600080fd5b810190808051906020019092919050505060ff1690506112c4600a82600a0a61222f90919063ffffffff16565b821015151561131e576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252602c815260200180612888602c913960400191505060405180910390fd5b60008551111515611397576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601c8152602001807f766972636c65416464726573732063616e6e6f74206265207a65726f0000000081525060200191505060405180910390fd5b60008863ffffffff16111515611415576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260188152602001807f41737365742047554944206d757374206e6f742062652030000000000000000081525060200191505060405180910390fd5b60008790508073ffffffffffffffffffffffffffffffffffffffff1663313ce5676040518163ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040160206040518083038186803b15801561147c57600080fd5b505afa158015611490573d6000803e3d6000fd5b505050506040513d60208110156114a657600080fd5b810190808051906020019092919050505060ff168760ff16141515611516576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260318152602001806128576031913960400191505060405180910390fd5b61154333308c8473ffffffffffffffffffffffffffffffffffffffff166122c2909392919063ffffffff16565b6115758a603460008c63ffffffff1663ffffffff168152602001908152602001600020546123e490919063ffffffff16565b603460008b63ffffffff1663ffffffff168152602001908152602001600020819055506036600081819054906101000a900463ffffffff168092919060010191906101000a81548163ffffffff021916908363ffffffff1602179055505060c0604051908101604052804281526020018b81526020018973ffffffffffffffffffffffffffffffffffffffff1681526020013373ffffffffffffffffffffffffffffffffffffffff1681526020018a63ffffffff1681526020016001600481111561163c57fe5b81525060376000603660009054906101000a900463ffffffff1663ffffffff1663ffffffff168152602001908152602001600020600082015181600001556020820151816001015560408201518160020160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060608201518160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060808201518160030160146101000a81548163ffffffff021916908363ffffffff16021790555060a08201518160030160186101000a81548160ff0219169083600481111561175d57fe5b02179055509050507faabab1db49e504b5156edf3f99042aeecb9607a08f392589571cd49743aaba8d338b603660009054906101000a900463ffffffff16604051808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018381526020018263ffffffff1663ffffffff168152602001935050505060405180910390a1600194505050505095945050505050565b60006118128261246e565b9050919050565b603360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156118c1576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260218152602001806129aa6021913960400191505060405180910390fd5b6000603760008463ffffffff1663ffffffff1681526020019081526020016000209050600260048111156118f157fe5b8160030160189054906101000a900460ff16600481111561190e57fe5b141515611966576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260608152602001806128b46060913960600191505060405180910390fd5b60038160030160186101000a81548160ff0219169083600481111561198757fe5b02179055506000603860008563ffffffff1663ffffffff168152602001908152602001600020549050603860008563ffffffff1663ffffffff168152602001908152602001600020600090557f960e217c57581c52cdc4e321eb617416d051a348a2ecf62bb8023a3558e80e858260030160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1685604051808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018263ffffffff1663ffffffff1681526020019250505060405180910390a150505050565b603a6020528060005260406000206000915090508060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16908060000160149054906101000a900463ffffffff16905082565b603360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611b70576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260218152602001806129aa6021913960400191505060405180910390fd5b600083905060008173ffffffffffffffffffffffffffffffffffffffff1663313ce5676040518163ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040160206040518083038186803b158015611bd957600080fd5b505afa158015611bed573d6000803e3d6000fd5b505050506040513d6020811015611c0357600080fd5b810190808051906020019092919050505090508260ff168160ff161115611c365782810360ff16600a0a88029750611c5c565b8260ff168160ff161015611c5b5780830360ff16600a0a88811515611c5757fe5b0497505b5b611c668189612498565b611c6f89612068565b1515611ce3576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260148152602001807f545820616c72656164792070726f63657373656400000000000000000000000081525060200191505060405180910390fd5b611d1588603460008763ffffffff1663ffffffff168152602001908152602001600020546120b790919063ffffffff16565b603460008663ffffffff1663ffffffff168152602001908152602001600020819055506000611d4f6127108a61222f90919063ffffffff16565b90506000611d66828b6120b790919063ffffffff16565b9050611d9388838673ffffffffffffffffffffffffffffffffffffffff166121429092919063ffffffff16565b7f378dbe173f6ed6e11630b29573f719ec4cefc9b49f430deed915911c5f78a0808883604051808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018281526020019250505060405180910390a1611e2989828673ffffffffffffffffffffffffffffffffffffffff166121429092919063ffffffff16565b7fb925ba840e2f36bcb317f8179bd8b5ed01aba4a22abf5f169162c0894dea87ab8982604051808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018281526020019250505060405180910390a15050505050505050505050565b603360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600060019054906101000a900460ff1680611ee65750611ee561258e565b5b80611efd57506000809054906101000a900460ff16155b1515611f54576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252602e8152602001806129cb602e913960400191505060405180910390fd5b60008060019054906101000a900460ff161590508015611fa4576001600060016101000a81548160ff02191690831515021790555060016000806101000a81548160ff0219169083151502179055505b82603960006101000a81548160ff02191690836002811115611fc257fe5b021790555081603360006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000603660006101000a81548163ffffffff021916908363ffffffff160217905550801561204b5760008060016101000a81548160ff0219169083151502179055505b505050565b60346020528060005260406000206000915090505481565b60006120738261246e565b1561208157600090506120b2565b60016035600084815260200190815260200160002060006101000a81548160ff021916908315150217905550600190505b919050565b6000828211151515612131576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601e8152602001807f536166654d6174683a207375627472616374696f6e206f766572666c6f77000081525060200191505060405180910390fd5b600082840390508091505092915050565b61222a838473ffffffffffffffffffffffffffffffffffffffff1663a9059cbb90507c0100000000000000000000000000000000000000000000000000000000028484604051602401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050604051602081830303815290604052907bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19166020820180517bffffffffffffffffffffffffffffffffffffffffffffffffffffffff838183161783525050505061259f565b505050565b600080821115156122a8576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601a8152602001807f536166654d6174683a206469766973696f6e206279207a65726f00000000000081525060200191505060405180910390fd5b600082848115156122b557fe5b0490508091505092915050565b6123de848573ffffffffffffffffffffffffffffffffffffffff166323b872dd90507c010000000000000000000000000000000000000000000000000000000002858585604051602401808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018281526020019350505050604051602081830303815290604052907bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19166020820180517bffffffffffffffffffffffffffffffffffffffffffffffffffffffff838183161783525050505061259f565b50505050565b6000808284019050838110151515612464576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601b8152602001807f536166654d6174683a206164646974696f6e206f766572666c6f77000000000081525060200191505060405180910390fd5b8091505092915050565b60006035600083815260200190815260200160002060009054906101000a900460ff169050919050565b60008260ff169050600082111515612518576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260168152602001807f56616c7565206d75737420626520706f7369746976650000000000000000000081525060200191505060405180910390fd5b61252f600a82600a0a61222f90919063ffffffff16565b8210151515612589576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252602c815260200180612888602c913960400191505060405180910390fd5b505050565b600080303b90506000811491505090565b6125be8273ffffffffffffffffffffffffffffffffffffffff166127f2565b1515612632576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601f8152602001807f5361666545524332303a2063616c6c20746f206e6f6e2d636f6e74726163740081525060200191505060405180910390fd5b600060608373ffffffffffffffffffffffffffffffffffffffff16836040518082805190602001908083835b602083101515612683578051825260208201915060208101905060208303925061265e565b6001836020036101000a0380198251168184511680821785525050505050509050019150506000604051808303816000865af19150503d80600081146126e5576040519150601f19603f3d011682016040523d82523d6000602084013e6126ea565b606091505b5091509150811515612764576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260208152602001807f5361666545524332303a206c6f772d6c6576656c2063616c6c206661696c656481525060200191505060405180910390fd5b6000815111156127ec5780806020019051602081101561278357600080fd5b810190808051906020019092919050505015156127eb576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252602a815260200180612a93602a913960400191505060405180910390fd5b5b50505050565b600080823b90506000811191505091905056fe23566972636c6545524332304d616e616765722063616e63656c5472616e736665725265717565737428293a204f6e6c79206d73672e73656e64657220697320616c6c6f77656420746f2063616e63656c446563696d616c732077657265206e6f742070726f766964656420776974682074686520636f72726563742076616c756556616c7565206d75737420626520626967676572206f7220657175616c204d494e5f4c4f434b5f56414c554523566972636c6545524332304d616e616765722063616e63656c5472616e736665725375636365737328293a20537461747573206d7573742062652043616e63656c52657175657374656420746f204661696c20746865207472616e7366657223566972636c6545524332304d616e616765722063616e63656c5472616e736665725265717565737428293a20537461747573206f6620627269646765207472616e73666572206d757374206265204f6b23566972636c6545524332304d616e616765722063616e63656c5472616e736665725265717565737428293a2043616e63656c206465706f73697420696e636f727265637443616c6c206d7573742062652066726f6d20747275737465642072656c61796572436f6e747261637420696e7374616e63652068617320616c7265616479206265656e20696e697469616c697a656423566972636c6545524332304d616e616765722063616e63656c5472616e736665725375636365737328293a203120686f75722074696d656f757420697320726571756972656423566972636c6545524332304d616e616765722063616e63656c5472616e736665725265717565737428293a205472616e73666572206d757374206265206174206c6561737420312e35207765656b206f6c645361666545524332303a204552433230206f7065726174696f6e20646964206e6f742073756363656564486569676874206d75737420696e637265617365207768656e207570646174696e6720617373657420726567697374727923566972636c6545524332304d616e616765722063616e63656c5472616e736665725375636365737328293a20537461747573206d7573742062652043616e63656c526571756573746564a165627a7a72305820c0228a3f65f0cbada6381e70ac12303494910255efd3ea640c9dab9abf26221c0029";

    public static final String FUNC_ASSETREGISTRY = "assetRegistry";

    public static final String FUNC_TRUSTEDRELAYERCONTRACT = "trustedRelayerContract";

    public static final String FUNC_ASSETBALANCES = "assetBalances";

    public static final String FUNC_INIT = "init";

    public static final String FUNC_WASVIRCLETXPROCESSED = "wasVircleTxProcessed";

    public static final String FUNC_PROCESSTRANSACTION = "processTransaction";

    public static final String FUNC_PROCESSASSET = "processAsset";

    public static final String FUNC_CANCELTRANSFERREQUEST = "cancelTransferRequest";

    public static final String FUNC_CANCELTRANSFERSUCCESS = "cancelTransferSuccess";

    public static final String FUNC_PROCESSCANCELTRANSFERFAIL = "processCancelTransferFail";

    public static final String FUNC_FREEZEBURNERC20 = "freezeBurnERC20";

    public static final String FUNC_GETBRIDGETRANSFER = "getBridgeTransfer";

    public static final Event TOKENUNFREEZE_EVENT = new Event("TokenUnfreeze", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TOKENUNFREEZEFEE_EVENT = new Event("TokenUnfreezeFee", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TOKENFREEZE_EVENT = new Event("TokenFreeze", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint32>() {}));
    ;

    public static final Event CANCELTRANSFERREQUEST_EVENT = new Event("CancelTransferRequest", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint32>() {}));
    ;

    public static final Event CANCELTRANSFERSUCCEEDED_EVENT = new Event("CancelTransferSucceeded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint32>() {}));
    ;

    public static final Event CANCELTRANSFERFAILED_EVENT = new Event("CancelTransferFailed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint32>() {}));
    ;

    public static final Event TOKENREGISTRY_EVENT = new Event("TokenRegistry", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}, new TypeReference<Address>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        // _addresses.put("1", "0x8a857bfd8118da4e6076559edd1b1ffcba321b2a");
        _addresses.put("1", "TQBjmYDQ3imopugQiJTPg8yhgZmoRjgski");
    }

    private TrxContract trxCntr;
    public VircleERC20Manager(TrxContract trxCntr) {
        super(trxCntr.getContract(), trxCntr.getOwnerAddress(), trxCntr.getClient());
        this.trxCntr = trxCntr;
    }


    public Tuple2<Address, Uint32> assetRegistry(Uint32 param0) {
        final Function function = new Function(FUNC_ASSETREGISTRY, 
                Arrays.<Type>asList(param0), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint32>() {}));
        List<Type> results = this.trxCntr.executeCallMultipleValueReturn(function);
        return new Tuple2<Address, Uint32>(
                (Address) results.get(0),
                (Uint32) results.get(1));
    }

    public Address trustedRelayerContract() {
        final Function function = new Function(FUNC_TRUSTEDRELAYERCONTRACT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return this.trxCntr.executeRemoteCallSingleValueReturn(function);
    }

    public Uint256 assetBalances(Uint32 param0) {
        final Function function = new Function(FUNC_ASSETBALANCES, 
                Arrays.<Type>asList(param0), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return this.trxCntr.executeRemoteCallSingleValueReturn(function);
    }
    /*
    public List<TokenUnfreezeEventResponse> getTokenUnfreezeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TOKENUNFREEZE_EVENT, transactionReceipt);
        ArrayList<TokenUnfreezeEventResponse> responses = new ArrayList<TokenUnfreezeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokenUnfreezeEventResponse typedResponse = new TokenUnfreezeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.receipient = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TokenUnfreezeEventResponse> tokenUnfreezeEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, TokenUnfreezeEventResponse>() {
            @Override
            public TokenUnfreezeEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TOKENUNFREEZE_EVENT, log);
                TokenUnfreezeEventResponse typedResponse = new TokenUnfreezeEventResponse();
                typedResponse.log = log;
                typedResponse.receipient = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<TokenUnfreezeEventResponse> tokenUnfreezeEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TOKENUNFREEZE_EVENT));
        return tokenUnfreezeEventFlowable(filter);
    }

    public List<TokenUnfreezeFeeEventResponse> getTokenUnfreezeFeeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TOKENUNFREEZEFEE_EVENT, transactionReceipt);
        ArrayList<TokenUnfreezeFeeEventResponse> responses = new ArrayList<TokenUnfreezeFeeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokenUnfreezeFeeEventResponse typedResponse = new TokenUnfreezeFeeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.receipient = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TokenUnfreezeFeeEventResponse> tokenUnfreezeFeeEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, TokenUnfreezeFeeEventResponse>() {
            @Override
            public TokenUnfreezeFeeEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TOKENUNFREEZEFEE_EVENT, log);
                TokenUnfreezeFeeEventResponse typedResponse = new TokenUnfreezeFeeEventResponse();
                typedResponse.log = log;
                typedResponse.receipient = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<TokenUnfreezeFeeEventResponse> tokenUnfreezeFeeEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TOKENUNFREEZEFEE_EVENT));
        return tokenUnfreezeFeeEventFlowable(filter);
    }

    public List<TokenFreezeEventResponse> getTokenFreezeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TOKENFREEZE_EVENT, transactionReceipt);
        ArrayList<TokenFreezeEventResponse> responses = new ArrayList<TokenFreezeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokenFreezeEventResponse typedResponse = new TokenFreezeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.freezer = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
            typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TokenFreezeEventResponse> tokenFreezeEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, TokenFreezeEventResponse>() {
            @Override
            public TokenFreezeEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TOKENFREEZE_EVENT, log);
                TokenFreezeEventResponse typedResponse = new TokenFreezeEventResponse();
                typedResponse.log = log;
                typedResponse.freezer = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
                typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Flowable<TokenFreezeEventResponse> tokenFreezeEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TOKENFREEZE_EVENT));
        return tokenFreezeEventFlowable(filter);
    }

    public List<CancelTransferRequestEventResponse> getCancelTransferRequestEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CANCELTRANSFERREQUEST_EVENT, transactionReceipt);
        ArrayList<CancelTransferRequestEventResponse> responses = new ArrayList<CancelTransferRequestEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CancelTransferRequestEventResponse typedResponse = new CancelTransferRequestEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<CancelTransferRequestEventResponse> cancelTransferRequestEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, CancelTransferRequestEventResponse>() {
            @Override
            public CancelTransferRequestEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CANCELTRANSFERREQUEST_EVENT, log);
                CancelTransferRequestEventResponse typedResponse = new CancelTransferRequestEventResponse();
                typedResponse.log = log;
                typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<CancelTransferRequestEventResponse> cancelTransferRequestEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CANCELTRANSFERREQUEST_EVENT));
        return cancelTransferRequestEventFlowable(filter);
    }

    public List<CancelTransferSucceededEventResponse> getCancelTransferSucceededEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CANCELTRANSFERSUCCEEDED_EVENT, transactionReceipt);
        ArrayList<CancelTransferSucceededEventResponse> responses = new ArrayList<CancelTransferSucceededEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CancelTransferSucceededEventResponse typedResponse = new CancelTransferSucceededEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<CancelTransferSucceededEventResponse> cancelTransferSucceededEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, CancelTransferSucceededEventResponse>() {
            @Override
            public CancelTransferSucceededEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CANCELTRANSFERSUCCEEDED_EVENT, log);
                CancelTransferSucceededEventResponse typedResponse = new CancelTransferSucceededEventResponse();
                typedResponse.log = log;
                typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<CancelTransferSucceededEventResponse> cancelTransferSucceededEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CANCELTRANSFERSUCCEEDED_EVENT));
        return cancelTransferSucceededEventFlowable(filter);
    }

    public List<CancelTransferFailedEventResponse> getCancelTransferFailedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CANCELTRANSFERFAILED_EVENT, transactionReceipt);
        ArrayList<CancelTransferFailedEventResponse> responses = new ArrayList<CancelTransferFailedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CancelTransferFailedEventResponse typedResponse = new CancelTransferFailedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<CancelTransferFailedEventResponse> cancelTransferFailedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, CancelTransferFailedEventResponse>() {
            @Override
            public CancelTransferFailedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CANCELTRANSFERFAILED_EVENT, log);
                CancelTransferFailedEventResponse typedResponse = new CancelTransferFailedEventResponse();
                typedResponse.log = log;
                typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<CancelTransferFailedEventResponse> cancelTransferFailedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CANCELTRANSFERFAILED_EVENT));
        return cancelTransferFailedEventFlowable(filter);
    }

    public List<TokenRegistryEventResponse> getTokenRegistryEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TOKENREGISTRY_EVENT, transactionReceipt);
        ArrayList<TokenRegistryEventResponse> responses = new ArrayList<TokenRegistryEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokenRegistryEventResponse typedResponse = new TokenRegistryEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.assetGuid = (Uint32) eventValues.getNonIndexedValues().get(0);
            typedResponse.erc20ContractAddress = (Address) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TokenRegistryEventResponse> tokenRegistryEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, TokenRegistryEventResponse>() {
            @Override
            public TokenRegistryEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TOKENREGISTRY_EVENT, log);
                TokenRegistryEventResponse typedResponse = new TokenRegistryEventResponse();
                typedResponse.log = log;
                typedResponse.assetGuid = (Uint32) eventValues.getNonIndexedValues().get(0);
                typedResponse.erc20ContractAddress = (Address) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<TokenRegistryEventResponse> tokenRegistryEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TOKENREGISTRY_EVENT));
        return tokenRegistryEventFlowable(filter);
    }
    */
    public TransactionReturn init(Uint8 _network, Address _trustedRelayerContract) {
        final Function function= new Function(
                FUNC_INIT, 
                Arrays.<Type>asList(_network, _trustedRelayerContract), 
                Collections.<TypeReference<?>>emptyList());
        return this.trxCntr.executeRemoteCallTransaction(function);
    }

    public Bool wasVircleTxProcessed(Uint256 txHash) {
        final Function function = new Function(FUNC_WASVIRCLETXPROCESSED, 
                Arrays.<Type>asList(txHash), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return this.trxCntr.executeRemoteCallSingleValueReturn(function);
    }

    public TransactionReturn processTransaction(Uint256 txHash, Uint256 value, Address destinationAddress, Address superblockSubmitterAddress, Address erc20ContractAddress, Uint32 assetGUID, Uint8 precision) {
        final Function function = new Function(
                FUNC_PROCESSTRANSACTION, 
                Arrays.<Type>asList(txHash, value, destinationAddress, superblockSubmitterAddress, erc20ContractAddress, assetGUID, precision), 
                Collections.<TypeReference<?>>emptyList());
        return this.trxCntr.executeRemoteCallTransaction(function);
    }

    public TransactionReturn processAsset(Uint256 _txHash, Uint32 _assetGUID, Uint32 _height, Address _erc20ContractAddress) {
        final Function function = new Function(
                FUNC_PROCESSASSET, 
                Arrays.<Type>asList(_txHash, _assetGUID, _height, _erc20ContractAddress), 
                Collections.<TypeReference<?>>emptyList());
        return this.trxCntr.executeRemoteCallTransaction(function);
    }

    public TransactionReturn cancelTransferRequest(Uint32 bridgeTransferId, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_CANCELTRANSFERREQUEST, 
                Arrays.<Type>asList(bridgeTransferId), 
                Collections.<TypeReference<?>>emptyList());
        return this.trxCntr.executeRemoteCallTransaction(function, weiValue);
    }

    public TransactionReturn cancelTransferSuccess(Uint32 bridgeTransferId) {
        final Function function = new Function(
                FUNC_CANCELTRANSFERSUCCESS, 
                Arrays.<Type>asList(bridgeTransferId), 
                Collections.<TypeReference<?>>emptyList());
        return this.trxCntr.executeRemoteCallTransaction(function);
    }

    public TransactionReturn processCancelTransferFail(Uint32 bridgeTransferId, Address challengerAddress) {
        final Function function = new Function(
                FUNC_PROCESSCANCELTRANSFERFAIL, 
                Arrays.<Type>asList(bridgeTransferId, challengerAddress), 
                Collections.<TypeReference<?>>emptyList());
        return this.trxCntr.executeRemoteCallTransaction(function);
    }

    public TransactionReturn freezeBurnERC20(Uint256 value, Uint32 assetGUID, Address erc20ContractAddress, Uint8 precision, DynamicBytes vircleAddress) {
        final Function function = new Function(
                FUNC_FREEZEBURNERC20, 
                Arrays.<Type>asList(value, assetGUID, erc20ContractAddress, precision, vircleAddress), 
                Collections.<TypeReference<?>>emptyList());
        return this.trxCntr.executeRemoteCallTransaction(function);
    }

    public Tuple6<Uint256, Uint256, Address, Address, Uint32, Uint8> getBridgeTransfer(Uint32 bridgeTransferId) {
        final Function function = new Function(FUNC_GETBRIDGETRANSFER, 
                Arrays.<Type>asList(bridgeTransferId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint32>() {}, new TypeReference<Uint8>() {}));
        List<Type> results = this.trxCntr.executeCallMultipleValueReturn(function);
        return new Tuple6<Uint256, Uint256, Address, Address, Uint32, Uint8>(
                (Uint256) results.get(0),
                (Uint256) results.get(1),
                (Address) results.get(2),
                (Address) results.get(3),
                (Uint32) results.get(4),
                (Uint8) results.get(5));
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class TokenUnfreezeEventResponse extends BaseEventResponse {
        public Address receipient;

        public Uint256 value;
    }

    public static class TokenUnfreezeFeeEventResponse extends BaseEventResponse {
        public Address receipient;

        public Uint256 value;
    }

    public static class TokenFreezeEventResponse extends BaseEventResponse {
        public Address freezer;

        public Uint256 value;

        public Uint32 bridgetransferid;
    }

    public static class CancelTransferRequestEventResponse extends BaseEventResponse {
        public Address canceller;

        public Uint32 bridgetransferid;
    }

    public static class CancelTransferSucceededEventResponse extends BaseEventResponse {
        public Address canceller;

        public Uint32 bridgetransferid;
    }

    public static class CancelTransferFailedEventResponse extends BaseEventResponse {
        public Address canceller;

        public Uint32 bridgetransferid;
    }

    public static class TokenRegistryEventResponse extends BaseEventResponse {
        public Uint32 assetGuid;

        public Address erc20ContractAddress;
    }
}
