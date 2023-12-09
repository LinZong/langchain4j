package dev.langchain4j.chain;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static dev.langchain4j.internal.ValidationUtils.ensureNotEmpty;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

/**
 * MultiPromptChain is made up of two components:
 * <p>
 * The RouterChain (responsible for selecting the next chain to call)
 * destination_chains: chains that the router chain can route to.
 *
 * @param <RoutingInput> input for routerChain.
 * @param <Input>        input for destination chain.
 * @param <Output>       output for destination chain.
 */
public class MultiPromptChain<RoutingInput, Input, Output> implements Chain<RoutingInput, Output> {

    private final RouterChain<RoutingInput, Input> routerChain;

    private final Map<String, Chain<Input, Output>> destinationChains;

    private final @Nullable Chain<Input, Output> defaultChain;

    public MultiPromptChain(RouterChain<RoutingInput, Input> routerChain, Map<String, Chain<Input, Output>> destinationChains) {
        this(routerChain, destinationChains, null);
    }

    public MultiPromptChain(RouterChain<RoutingInput, Input> routerChain,
                            Map<String, Chain<Input, Output>> destinationChains,
                            @Nullable Chain<Input, Output> defaultChain) {
        this.routerChain = ensureNotNull(routerChain, "routerChain");
        this.destinationChains = ensureNotEmpty(destinationChains, "destinationChains");
        this.defaultChain = defaultChain;
    }

    @Override
    public Output execute(RoutingInput input) {
        RouterChain.Route<Input> dest = this.routerChain.routing(input);
        if (dest instanceof RouterChain.DefaultRoute) {
            if (this.defaultChain == null) {
                throw new UnsupportedOperationException(
                        String.format(
                                "No available default destination chain in order to handle a default route destination: %s provided by router chain: %s",
                                dest,
                                this.routerChain));
            }
            RouterChain.DefaultRoute<Input> defaultDest = (RouterChain.DefaultRoute<Input>) dest;
            return this.defaultChain.execute(defaultDest.getDestinationInput());
        }

        return this.destinationChains.getOrDefault(dest.getDestination(),
                                                   ignored -> {
                                                       throw new UnsupportedOperationException(String.format("No available destination: %s",
                                                                                                             dest.getDestination()));
                                                   }).execute(dest.getDestinationInput());
    }

}
