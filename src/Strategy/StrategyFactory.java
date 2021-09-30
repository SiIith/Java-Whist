package Strategy;

public class StrategyFactory {
    private static StrategyFactory strategyFactory = null;

    private StrategyFactory() {
    }

    public static StrategyFactory getInstance() {
        if (strategyFactory == null)
            strategyFactory = new StrategyFactory();
        return strategyFactory;
    }

    public ISelectionStrategy getStrategy(String type) {
        switch (type) {
            case "random": {
                return new RandomISelectionStrategy();

            }
            case "max": {
                return new MaxRankISelectionStrategy();

            }
            case "smart": {
                return new SmartSelectionStrategy();
            }

            default:
                return new RandomISelectionStrategy();
        }
    }
}
