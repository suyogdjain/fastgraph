package ao.ai.evo.gene.synthesis;

import ao.ai.evo.gene.GpGene;
import ao.ai.evo.primordial_soup.PrimordialSoup;
import ao.ai.evo.promoter.affinity.Affinity;
import ao.ai.evo.chromosome.config.TreeGenParams;
import com.google.inject.ImplementedBy;

/**
 * 
 */
@ImplementedBy(GeneSynthesizerImpl.class)
public interface GeneSynthesizer
{
    //--------------------------------------------------------------------
    public GpGene generate(
            TreeGenParams  params,
            Affinity       returning,
            PrimordialSoup ops);
}