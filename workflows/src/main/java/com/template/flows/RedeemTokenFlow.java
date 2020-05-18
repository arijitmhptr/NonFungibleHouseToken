package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveNonFungibleTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveNonFungibleTokensHandler;
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemNonFungibleTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemNonFungibleTokensHandler;
import com.r3.corda.lib.tokens.workflows.types.PartyAndToken;
import com.template.states.HouseSwapTokenState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

import java.util.List;

@InitiatingFlow
@StartableByRPC
public class RedeemTokenFlow extends FlowLogic<SignedTransaction> {

    private final String address;

    public RedeemTokenFlow(String address) {
        this.address = address;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        StateAndRef<HouseSwapTokenState> stateandref = getServiceHub().getVaultService().
                queryBy(HouseSwapTokenState.class).getStates().stream()
                .filter(sf -> sf.getState().getData().getAddress().equals(address)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Token type of " + this.address + " not found in the vault"));

        HouseSwapTokenState housetoken = stateandref.getState().getData();

        Party issuer = housetoken.getParticipant();

        //get the pointer pointer to the evolvable token type
        TokenPointer tokenPointer = housetoken.toPointer(housetoken.getClass());

        //call built in flow to issue non fungible tokens
        return subFlow(new RedeemNonFungibleTokens(tokenPointer,issuer));
    }

    @InitiatedBy(RedeemTokenFlow.class)
    public static class MoveTokenFlowResponder extends FlowLogic<Void> {
        private FlowSession counterSession;
        public MoveTokenFlowResponder(FlowSession counterSession) {
            this.counterSession = counterSession;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            // Simply use the MoveFungibleTokensHandler as the responding flow
            subFlow(new RedeemNonFungibleTokensHandler(counterSession));
            return null;
        }
    }
}
