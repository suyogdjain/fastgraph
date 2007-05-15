package ao.ai.evo.gene;

import ao.ai.evo.product.Product;
import ao.ai.evo.heredity.Replicable;
import ao.ai.evo.promoter.Promoter;

/**
 * Genes interact with each other to influence physical
 *  development and behavior. Genes consist of a long strand
 *  of DNA (RNA in some viruses) that contains a promoter, which
 *  controls the activity of a gene, and a coding sequence,
 *  which determines what the gene produces.
 */
public interface Gene<T extends Gene> extends Replicable<T>
{
    /**
     * @return amount of genetic material
     */
    public int size();
    
    public Promoter promoter();

    public Product express();

    public void splice(int locus, T dependency);
}
