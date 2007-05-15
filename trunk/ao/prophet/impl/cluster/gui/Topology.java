package ao.prophet.impl.cluster.gui;

import ao.prophet.impl.cluster.Cluster;
import ao.prophet.impl.cluster.InternalCluster;
import ao.prophet.impl.cluster.LeafCluster;
import ao.prophet.impl.cluster.FinalPly;
import ao.util.Rand;

import java.awt.*;
import java.awt.image.VolatileImage;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

/**
 * Turns a dendrogram into a topology.
 */
public class Topology extends Component
{
    //--------------------------------------------------------------------
//    private static final int BASE_WIDTH  = 512;
//    private static final int BASE_HEIGHT = 512;
    private static final double MAX_PADDING = 0.25;


    //--------------------------------------------------------------------
    public static <I> Component visualize(Cluster<I> root)
    {
        final FinalPly<I> finalPly = new FinalPly<I>();
        root.preOrderTraverse( new Cluster.Visitor<I>() {
                public void visit(Cluster<I> cluster) {}
                public void visitInternal(
                                InternalCluster<I> internalCluster){}

                public void visitLeaf(LeafCluster<I> leafCluster) {
                    finalPly.add( leafCluster );
                }
        } );
        finalPly.buildLookup();

        return new Topology(root);
    }


    //--------------------------------------------------------------------
    private final Cluster<?>    ROOT;
    private       VolatileImage backBuffer = null;
    private       long          SEED       = Rand.nextLong();

    public Topology(Cluster<?> root)
    {
        ROOT = root;

//        setBackground(BACK_COLOR);
        setPreferredSize(
                new Dimension(
                        Toolkit.getDefaultToolkit()
                                .getScreenSize().width  / 2,
                        Toolkit.getDefaultToolkit()
                                .getScreenSize().height / 2) );

        setVisible( true );
    }


    //-------------------------------------------------
    public synchronized void paint(Graphics g)
    {
//        if (backBuffer == null)
//        {
            createBackBuffer();
//        }

        do
        {
            int valCode = backBuffer.validate(getGraphicsConfiguration());
            if (valCode == VolatileImage.IMAGE_RESTORED) {
                // redraw anyways
            } else if (valCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                createBackBuffer();
            }

//            final int leafCount  = ROOT.rightmostLeafLabel() + 1;
//            final int borderSize =
//                    Math.round(
//                            ((float) Math.min(getWidth(), getHeight()))
//                                / leafCount * 0.2f);

            addRecursivePainter( backBuffer.getGraphics() );

            g.drawImage(
                    backBuffer,
                    0, 0,
                    getWidth(),
                    getHeight(),
                    this);
        }
        while (backBuffer.contentsLost());
    }

    @SuppressWarnings("unchecked")
    private void addRecursivePainter(Graphics g)
    {
        ROOT.preOrderTraverse( new RecursivePainter(g) );
    }

    //-------------------------------------------------
    private void createBackBuffer()
    {
        if (backBuffer != null)
        {
            backBuffer.flush();
            backBuffer = null;
        }

        backBuffer =
                createVolatileImage(
                        getWidth(),
                        getHeight() );
//                        BASE_WIDTH,
//                        BASE_HEIGHT );
    }


    //--------------------------------------------------------------------
    private class RecursivePainter implements Cluster.Visitor
    {
        private final Graphics         gBB;
        private final Random           RAND = new Random(SEED);
        private final Deque<Rectangle> AREA_STACK;

        private Rectangle currentArea;


        public RecursivePainter(Graphics gBB)
        {
            this.gBB = gBB;
            AREA_STACK = new LinkedList<Rectangle>()
            {
                {
                    add(new Rectangle(
                            getWidth(),
                            getHeight() ));
//                            BASE_WIDTH,
//                            BASE_HEIGHT));
                }
            };
        }

        public void visit(Cluster cluster)
        {
            currentArea = AREA_STACK.removeFirst();

            gBB.setColor( nextBackColour() );
            gBB.fillRect(
                    currentArea.x,
                    currentArea.y,
                    currentArea.width,
                    currentArea.height);
        }

        public void visitLeaf(LeafCluster leafCluster)
        {
            gBB.setColor( Color.WHITE );
            gBB.drawString(
                    leafCluster.item().toString(),
                    currentArea.x,
                    currentArea.y + 10);
        }

        public void visitInternal(InternalCluster internalCluster)
        {
            double firstProportion  =
                    internalCluster.leftChildProportion();
            double secondProportion = 1.0 - firstProportion;
            double childDistance =
                    1.0 - internalCluster.childRelation().itemWeightScaleFactor();

            if (currentArea.width > currentArea.height)
            {
                Rectangle first =
                        new Rectangle(
                                currentArea.x,
                                currentArea.y,
                                (int) (firstProportion *
                                       currentArea.width),
                                currentArea.height
                        );

                AREA_STACK.addFirst(
                        pad(new Rectangle(
                                currentArea.x + first.width,
                                currentArea.y,
                                (int) (secondProportion * currentArea.width),
                                currentArea.height
                        ), childDistance));
                AREA_STACK.addFirst(pad(first, childDistance));
            }
            else
            {
                Rectangle first =
                        new Rectangle(
                                currentArea.x,
                                currentArea.y,
                                currentArea.width,
                                (int) (firstProportion *
                                       currentArea.height)
                        );
                AREA_STACK.addFirst(
                        pad(new Rectangle(
                                currentArea.x,
                                currentArea.y + first.height,
                                currentArea.width,
                                (int) (secondProportion * currentArea.height)
                        ), childDistance));
                AREA_STACK.addFirst(pad(first, childDistance));
            }
        }


        //----------------------------------------------------------------
        private Color nextBackColour()
        {
            return new Color(RAND.nextFloat(),
                             RAND.nextFloat(),
                             RAND.nextFloat());
        }

        private Rectangle pad(Rectangle padee, double childDistance)
        {
            int widthPadding  = (int)(padee.width  * MAX_PADDING / 2 * childDistance);
            int heightPadding = (int)(padee.height * MAX_PADDING / 2 * childDistance);

            return new Rectangle(
                        padee.x + widthPadding,
                        padee.y + heightPadding,
                        padee.width  - 2 * widthPadding,
                        padee.height - 2 * heightPadding);
        }
    }
}
