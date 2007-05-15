package ao.ai.rl;

import ao.ai.rl.problem.snakes.SnakesEnvironment;
import ao.ai.rl.tourney.ConcurrentThournament;
import ao.ai.rl.tourney.Tournament;
import ao.ai.rl.gp.agent.RandomAgentProvider;
import com.google.inject.AbstractModule;
import static com.google.inject.name.Names.named;

/**
 * 
 */
public class Config extends AbstractModule
{
    //--------------------------------------------------------------------
    protected void configure()
    {
        bind(Agent.class).toProvider(RandomAgentProvider.class);

        bind(Tournament.class)
                .to(ConcurrentThournament.class);
        bindConstant().annotatedWith(
                named(ConcurrentThournament.AGENT_POOL_SIZE)
        ).to(4);

        bind(Environment.class)
                .to(SnakesEnvironment.class);
    }
}
