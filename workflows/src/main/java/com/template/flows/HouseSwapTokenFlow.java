package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.contracts.utilities.TransactionUtilitiesKt;
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import com.template.states.HouseSwapTokenState;
import net.corda.core.contracts.*;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;

import java.util.Arrays;
import java.util.UUID;

public class HouseSwapTokenFlow {

    private HouseSwapTokenFlow() {
        //Instantiation not allowed
    }
    /**
     * Create instance of EvolvableTokenType and call subflow CreateEvolvableToken to create an evolvableTokenType.
     * You can check the created evolvableTokenType in the Nodes vault_liner_states table, evolvableTokenType being
     * a linear state
     */
    @StartableByRPC
    @InitiatingFlow
    public static class CreateEvolvableTokenFlow extends FlowLogic<SignedTransaction> {

        private final String address;
        private final int valuation;

        public CreateEvolvableTokenFlow(String address, int valuation) {
            this.address = address;
            this.valuation = valuation;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {
            //grab the notary
            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            //create token type
            HouseSwapTokenState housestate = new HouseSwapTokenState(address,valuation,getOurIdentity(),new UniqueIdentifier());

            //warp it with transaction state specifying the notary
            TransactionState transactionState = new TransactionState(housestate, notary);

            //call built in sub flow CreateEvolvableTokens. This can be called via rpc or in unit testing
            return subFlow(new CreateEvolvableTokens(transactionState));
        }
    }
    /**
     *  Issue Non Fungible Token using IssueTokens flow
     */
    @StartableByRPC
    public static class IssueEvolvableTokenFlow extends FlowLogic<SignedTransaction>{
        private final Party recipient;

        public IssueEvolvableTokenFlow(Party recipient) {
            this.recipient = recipient;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            StateAndRef<HouseSwapTokenState> stateandref = getServiceHub().getVaultService().queryBy(HouseSwapTokenState.class).getStates().get(0);
            HouseSwapTokenState housetoken = stateandref.getState().getData();

            //get the pointer pointer to the evolvable token type
            TokenPointer tokenPointer = housetoken.toPointer(housetoken.getClass());

            //assign the issuer to the token type who will be issuing the tokens
            IssuedTokenType issuedTokenType = new IssuedTokenType(getOurIdentity(), tokenPointer);

            //mention the current holder also
            NonFungibleToken nonFungibleToken = new NonFungibleToken(issuedTokenType, recipient, new UniqueIdentifier(), TransactionUtilitiesKt.getAttachmentIdForGenericParam(tokenPointer));

            //call built in flow to issue non fungible tokens
            return subFlow(new IssueTokens(Arrays.asList(nonFungibleToken)));
        }
    }


}

