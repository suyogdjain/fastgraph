package ao.ai.rl.problem.snakes;

import ao.ai.rl.Agent;
import ao.ai.sample.WallHugAi;
import ao.sw.engine.player.Player;
import ao.sw.engine.player.MoveSpecifier;
import ao.sw.engine.board.BoardArrangement;
import ao.sw.engine.board.Direction;
import ao.sw.engine.v2.Snake;
import com.google.inject.Inject;

import java.util.Collection;

/**
 *
 */
public class AgentPlayer implements Player
{
    //--------------------------------------------------------------------
    private Agent thinker;

    //--------------------------------------------------------------------
    @Inject
    public AgentPlayer(Agent agent)
    {
        thinker = agent;
    }


    //--------------------------------------------------------------------
    public void startThinking() {}
    public void stopThinking()  {}


    //--------------------------------------------------------------------
    public void makeMove(
            BoardArrangement boardArrangement,
            Snake snake,
            MoveSpecifier moveSpecifier,
            Collection<Snake> snakes)
    {
        if (snakes.isEmpty() ||
                Direction.availableFrom(
                        boardArrangement, snake.head()
                ).isEmpty())
        {
            new WallHugAi()
                    .makeMove(
                            boardArrangement,
                            snake,
                            moveSpecifier,
                            snakes);
        }
        else
        {
            thinker.sense(new InputObject(boardArrangement,
                                          snake,
                                          snakes.iterator().next()));

            for (int i = 0; i < 16; i++)
            {
                moveSpecifier.clearDirection();

                thinker.act(new OutputObject(moveSpecifier));

                Direction dir = moveSpecifier.latestDirection();
                if (dir == null ||
                        !dir.translate( snake.head() )
                                .availableIn(boardArrangement))
                {
                    thinker.didNotAct();
                }
                else
                {
                    break;
                }
            }
        }
    }

}
