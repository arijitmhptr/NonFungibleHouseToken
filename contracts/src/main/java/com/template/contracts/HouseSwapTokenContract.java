package com.template.contracts;

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract;
import com.template.states.HouseSwapTokenState;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;

import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * This doesn't do anything over and above the [EvolvableTokenContract].
 */
public class HouseSwapTokenContract extends EvolvableTokenContract {

    @Override
    public void additionalCreateChecks(LedgerTransaction tx) {
//        ContractState state = tx.getOutputStates().get(0);
//        HouseSwapTokenState housestate = (HouseSwapTokenState) state;
//        requireThat(req->{
//            req.using("There should not be any input state", tx.getInputStates().size() == 0);
//            req.using("There should be one output state", tx.getOutputStates().size() != 0);
//            req.using("State should of type HouseSwapToken", state instanceof HouseSwapTokenState);
//            req.using("Valuation should be greater than 0", housestate.getValuation() > 0);
//            req.using("Address can't be blank", housestate.getAddress().equals(" "));
//            return null;
//                });
    }

    @Override
    public void additionalUpdateChecks(LedgerTransaction tx) {
//        ContractState oldstate = tx.getInputStates().get(0);
//        ContractState newstate = tx.getOutputStates().get(0);
//
//        HouseSwapTokenState houseoldstate = (HouseSwapTokenState) oldstate;
//        HouseSwapTokenState housenewstate = (HouseSwapTokenState) newstate;
//
//        requireThat(req -> {
//            req.using("New state should be of type HouseSwapTokenState",newstate instanceof HouseSwapTokenState);
//            req.using("Address must be same",housenewstate.getAddress() == houseoldstate.getAddress());
//            req.using("Valuation should be greater than 0",housenewstate.getValuation() > 0);
//            return null;
//        });
   }
}
