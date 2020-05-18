package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.contracts.utilities.TransactionUtilitiesKt;
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveNonFungibleTokens;
import com.r3.corda.lib.tokens.workflows.types.PartyAndToken;
import com.template.states.HouseSwapTokenState;
import net.corda.core.contracts.*;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

import java.util.Arrays;

@StartableByRPC
@InitiatingFlow
public class CreateTokenFlow extends FlowLogic<SignedTransaction> {

        private final String address;
        private final int valuation;

        public CreateTokenFlow(String address, int valuation) {
            this.address = address;
            this.valuation = valuation;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {
            //grab the notary
            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            //create token type
            HouseSwapTokenState housestate = new HouseSwapTokenState(address, valuation, getOurIdentity(), new UniqueIdentifier());

            //warp it with transaction state specifying the notary
            TransactionState transactionState = new TransactionState(housestate, notary);

            //call built in sub flow CreateEvolvableTokens. This can be called via rpc or in unit testing
            return subFlow(new CreateEvolvableTokens(transactionState));
        }
    }

