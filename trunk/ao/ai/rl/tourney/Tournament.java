package ao.ai.rl.tourney;

import ao.ai.rl.Agent;

/**
 *
 */
public interface Tournament
{
    public Agent bestOfBreed();
    public void runOnce();
    public void init();
}
