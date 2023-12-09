package dev.langchain4j.chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sequential chains allow you to connect multiple chains and compose them into pipelines that execute some specific scenario, also providing 100% type safe.
 *
 * <pre>{@code
 *     Usage:
 *         SequentialChain<String, String> chains = new SequentialChain.Builder<String, String>() // The whole sequential chain: accepts String, produces String.
 *                 .first(new Chain<String, Date>() { // First child chain: accepts String (restricted with the input type of the target sequential chain), produces Date
 *                     @Override
 *                     public Date run(String s) {
 *                         return parseDate(s);
 *                     }
 *                 })
 *                 .then(new Chain<Date, Long>() { // Secondary child chain: accepts Date(inherited above), produces Long
 *                     @Override
 *                     public Long run(Date date) {
 *                         return date.getTime();
 *                     }
 *                 })
 *                 .end(new Chain<Long, String>() { // Tail child chain: accepts Long (inherited above), produces String (restricted with the output type of the target sequential chain).
 *                     @Override
 *                     public String run(Long value) {
 *                         return String.valueOf(value);
 *                     }
 *                 });
 *         System.out.println(chains.run("2023-12-09 13:00:00"));
 *         // Result: 1702098000000
 * }</pre>
 *
 * @param <Input>  Generic type of input for target {@link SequentialChain}.
 * @param <Output> Generic type of output for target {@link SequentialChain}.
 */
public class SequentialChain<Input, Output> implements Chain<Input, Output> {

    private final List<Chain<?, ?>> chains;

    private SequentialChain(List<Chain<?, ?>> chains) {
        this.chains = chains;
    }

    /**
     * Execute the sequential chain with specific input.
     *
     * @param input chain input
     * @return chain output
     */
    @Override
    // Type safe had been imposed during chain building, see javadoc above.
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Output execute(Input input) {
        Object currInput = input;
        for (Chain chain : this.chains) {
            currInput = chain.execute(currInput);
        }
        return (Output) currInput;
    }

    /**
     * Builder for {@link SequentialChain}.
     *
     * @param <Input>  Generic type of input for target {@link SequentialChain}.
     * @param <Output> Generic type of output for target {@link SequentialChain}.
     */
    public static class Builder<Input, Output> {

        /**
         * Specify the first child chain of target {@link SequentialChain}.
         *
         * @param chain           Instance of the first child chain of target {@link SequentialChain}.
         * @param <CurrentOutput> Generic type of output for child chain.
         * @return
         */
        public <CurrentOutput> ThenBuilder<Input, CurrentOutput, Output> first(Chain<Input, CurrentOutput> chain) {
            return new ThenBuilder<>(new ArrayList<>(Collections.singletonList(chain)));
        }

        /**
         * Create a {@link SequentialChain} which has only one child chain.
         *
         * @param chain
         * @return
         */
        public SequentialChain<Input, Output> just(Chain<Input, Output> chain) {
            return new ThenBuilder<Input, Input, Output>(new ArrayList<>()).end(chain);
        }
    }

    /**
     * Builder for the rest component of target {@link SequentialChain}.
     *
     * @param <FirstInput>   Generic type of the beginning input of the whole {@link SequentialChain}.
     * @param <CurrentInput> Generic type of input for current child chain of the whole {@link SequentialChain}.
     * @param <FinalOutput>  Generic type of the final output of the whole {@link SequentialChain}.
     */
    public static class ThenBuilder<FirstInput, CurrentInput, FinalOutput> {

        private final List<Chain<?, ?>> chains;

        private ThenBuilder(List<Chain<?, ?>> chains) {
            this.chains = chains;
        }

        /**
         * Append current child chain to the tail of sequence for the whole {@link SequentialChain}.
         *
         * @param chain           child chain
         * @param <CurrentOutput> Generic type of output for current child chain.
         * @return builder
         */
        public <CurrentOutput> ThenBuilder<FirstInput, CurrentOutput, FinalOutput> then(Chain<CurrentInput, CurrentOutput> chain) {
            this.chains.add(chain);
            return new ThenBuilder<>(this.chains);
        }

        /**
         * Append current child chain to the tail of sequence for the whole {@link SequentialChain}, then build up the target {@link SequentialChain} instance.
         *
         * @param chain Ending child chain.
         * @return builder
         */
        public SequentialChain<FirstInput, FinalOutput> end(Chain<CurrentInput, FinalOutput> chain) {
            this.chains.add(chain);
            return new SequentialChain<>(this.chains);
        }
    }
}
