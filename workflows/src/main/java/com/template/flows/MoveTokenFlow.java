package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.contracts.types.TokenType;
import com.r3.corda.lib.tokens.contracts.utilities.TransactionUtilitiesKt;
import com.r3.corda.lib.tokens.money.FiatCurrency;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveNonFungibleTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveNonFungibleTokensHandler;
import com.r3.corda.lib.tokens.workflows.types.PartyAndToken;
import com.template.states.HouseSwapTokenState;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

@InitiatingFlow
@StartableByRPC
public class MoveTokenFlow extends FlowLogic<SignedTransaction> {

        private final String address;
        private final Party holder;

        public MoveTokenFlow(String address, Party holder) {
            this.address = address;
            this.holder = holder;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {
            StateAndRef<HouseSwapTokenState> stateandref = getServiceHub().getVaultService().
                    queryBy(HouseSwapTokenState.class).getStates().stream()
                    .filter(sf -> sf.getState().getData().getAddress().equals(address)).findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Token type of " + this.address + " not found in the vault"));

            HouseSwapTokenState housetoken = stateandref.getState().getData();

            //get the pointer pointer to the evolvable token type
            TokenPointer tokenPointer = housetoken.toPointer(housetoken.getClass());

            PartyAndToken partytoken = new PartyAndToken(holder, tokenPointer);

            //call built in flow to issue non fungible tokens
            return (SignedTransaction) subFlow(new MoveNonFungibleTokens(partytoken));
        }

        @InitiatedBy(MoveTokenFlow.class)
        public static class MoveTokenFlowResponder extends FlowLogic<Void> {

            private FlowSession counterSession;

            public MoveTokenFlowResponder(FlowSession counterSession) {
                this.counterSession = counterSession;
            }

            @Suspendable
            @Override
            public Void call() throws FlowException {
                // Simply use the MoveFungibleTokensHandler as the responding flow
                subFlow(new MoveNonFungibleTokensHandler(counterSession));
                return null;
            }
        }
    }
