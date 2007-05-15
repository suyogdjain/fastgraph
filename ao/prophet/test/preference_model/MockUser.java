package ao.prophet.test.preference_model;

import ao.prophet.impl.appraisal.Appraisal;

/**
 * In some way tries to simpulate a real user.
 */
public interface MockUser<I>
{
    public Appraisal appraise(I item);
}
