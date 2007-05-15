package ao.ai.rl;

import ao.ai.rl.tourney.EloRating;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface Environment
{
    public void introduce(Agent agent);

    public int nextSimulationSize();
    
    public void simulate(
            List<Agent>           agents,
            Map<Agent, EloRating> ratings);
}
