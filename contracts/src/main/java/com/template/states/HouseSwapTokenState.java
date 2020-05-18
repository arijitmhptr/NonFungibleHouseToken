package com.template.states;

import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType;
import com.template.contracts.HouseSwapTokenContract;
import jdk.nashorn.internal.parser.TokenType;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@BelongsToContract(com.template.contracts.HouseSwapTokenContract.class)
public class HouseSwapTokenState extends EvolvableTokenType {
        private final String address;
        private final int valuation;
        private final int fdigit;
        private final Party participant;
        private final UniqueIdentifier uid;

    public HouseSwapTokenState(String address, int valuation, Party participant, UniqueIdentifier uid) {
        this.address = address;
        this.valuation = valuation;
        this.fdigit = 0;
        this.participant = participant;
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public int getValuation() {
        return valuation;
    }

    public Party getParticipant() {
        return participant;
    }

    @Override
    public int getFractionDigits() {
        return this.fdigit;
    }

    @NotNull
    @Override
    public List<Party> getMaintainers() {
        return Arrays.asList(this.participant);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.uid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress(),getFractionDigits(),getValuation(),getParticipant());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HouseSwapTokenState;
    }
}
