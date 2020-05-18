package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.contracts.utilities.TransactionUtilitiesKt;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveNonFungibleTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveNonFungibleTokensHandler;
import com.r3.corda.lib.tokens.workflows.types.PartyAndToken;
import com.template.states.HouseSwapTokenState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

import java.util.Arrays;
import java.util.stream.Stream;

@InitiatingFlow
@StartableByRPC
public class IssueTokenFlow extends FlowLogic<String> {

    private final String address;

    public IssueTokenFlow(String address) {
        this.address = address;
    }

    @Override
    @Suspendable
    public String call() throws FlowException {

        StateAndRef<HouseSwapTokenState> stateandref = getServiceHub().getVaultService().
                queryBy(HouseSwapTokenState.class).getStates().stream()
                .filter(sf -> sf.getState().getData().getAddress().equals(address)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Token type of " + this.address + " not found in the vault"));

        HouseSwapTokenState housetoken = stateandref.getState().getData();

        //get the pointer pointer to the evolvable token type
        TokenPointer tokenPointer = housetoken.toPointer(housetoken.getClass());

        //assign the issuer to the token type who will be issuing the tokens
        IssuedTokenType issuedTokenType = new IssuedTokenType(getOurIdentity(), tokenPointer);

        //mention the current holder also
        NonFungibleToken nonFungibleToken = new NonFungibleToken(issuedTokenType, getOurIdentity(), new UniqueIdentifier(), TransactionUtilitiesKt.getAttachmentIdForGenericParam(tokenPointer));


        int tokenlist = getServiceHub().getVaultService().queryBy(NonFungibleToken.class).getStates().size();

        System.out.println("State size : " + tokenlist);

        if (tokenlist > 0 ){
            return "Same Token type is already exist, hence can't be issued";
        }
        else
        {
            SignedTransaction stx = subFlow(new IssueTokens(Arrays.asList(nonFungibleToken)));
            return "Issued new token " + stx.getId();
        }

    }
}
