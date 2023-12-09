package dev.langchain4j.chain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class SequentialChainTest {


    @Test
    public void should_run_sequential_chain_with_only_one_child_chain() {
        Long result = new SequentialChain.Builder<Long, Long>()
                .just(x -> x * 2)
                .execute(10L);

        Assertions.assertEquals(20L, result);
    }

    @Test
    public void should_run_sequential_chain_one_by_one() {
        PlusOneChain chain1 = Mockito.spy(PlusOneChain.class);
        PlusTwoChain chain2 = Mockito.spy(PlusTwoChain.class);
        PlusThreeChain chain3 = Mockito.spy(PlusThreeChain.class);


        Long result = new SequentialChain.Builder<Long, Long>()
                .first(chain1)
                .then(chain2)
                .end(chain3)
                .execute(0L);

        Assertions.assertEquals(6L,
                                result,
                                "Accumulated result mismatch, expected: 6 (+1 +2 +3), actual: " + result);

        // A. Single mock whose methods must be invoked in a particular order

        InOrder inorder = Mockito.inOrder(chain1, chain2, chain3);
        inorder.verify(chain1).execute(0L);
        inorder.verify(chain2).execute(1L);
        inorder.verify(chain3).execute(3L);
    }


    private static class PlusOneChain implements Chain<Long, Long> {

        @Override
        public Long execute(Long aLong) {
            return aLong + 1;
        }
    }

    private static class PlusTwoChain implements Chain<Long, Long> {

        @Override
        public Long execute(Long aLong) {
            return aLong + 2;
        }
    }

    private static class PlusThreeChain implements Chain<Long, Long> {

        @Override
        public Long execute(Long aLong) {
            return aLong + 3;
        }
    }
}
