package dev.langchain4j.chain;

public interface RouterChain<RoutingInput, DestinationInput> {

    /**
     * Route input to the best destination chain.
     *
     * @param input
     * @return The name and input of the destination chain, otherwise {@link RouterChain.DefaultRoute} for default destination (usually in order to head some fallback behavior).
     */
    Route<DestinationInput> routing(RoutingInput input);


    class Route<DestinationInput> {

        private final String destination;

        private final DestinationInput destinationInput;

        public Route(String destination, DestinationInput destinationInput) {
            this.destination = destination;
            this.destinationInput = destinationInput;
        }

        public String getDestination() {
            return destination;
        }

        public DestinationInput getDestinationInput() {
            return destinationInput;
        }
    }

    class DefaultRoute<DestinationInput> extends Route<DestinationInput> {

        public DefaultRoute(DestinationInput destinationInput) {
            super("default", destinationInput);
        }
    }
}
