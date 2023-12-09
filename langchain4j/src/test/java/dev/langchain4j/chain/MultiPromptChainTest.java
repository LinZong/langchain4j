package dev.langchain4j.chain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class MultiPromptChainTest {


    @Test
    public void should_route_to_destination_chain_properly() {
        SimpleRouterChain routerChain = new SimpleRouterChain();
        Map<String, Chain<Long, Long>> destinations = new HashMap<>();
        destinations.put("1", x -> x * 2);
        destinations.put("2", x -> x + 2);
        MultiPromptChain<Long, Long, Long> mpc = new MultiPromptChain<>(routerChain, destinations);
        Assertions.assertEquals(2L, mpc.execute(1L));
        Assertions.assertEquals(4L, mpc.execute(2L));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> mpc.execute(-1L));

        MultiPromptChain<Long, Long, Long> mpc2 = new MultiPromptChain<>(routerChain, destinations, x -> 0L);
        // Fallback to default chain and produces 0L.
        Assertions.assertEquals(0L, mpc2.execute(-1L));

    }

    private static class SimpleRouterChain implements RouterChain<Long, Long> {

        @Override
        public Route<Long> routing(Long aLong) {
            if (aLong == -1L) {
                return new DefaultRoute<>(aLong);
            }
            return new Route<>(String.valueOf(aLong), aLong);
        }
    }
}
