import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
public class SimpleMouseExample extends JFrame implements Runnable {
    
    private FrameRate frameRate;
    private BufferStrategy bs;
    private volatile boolean running;
    private Thread gameThread;
    private SimpleMouseInput mouse;
    private ArrayList<Point> lines = new ArrayList<Point>();
    private boolean drawingLine;
    
    private Color[] COLORS = {
        Color.RED,
        Color.GREEN,
        Color.YELLOW,
        Color.BLUE
    };
    private int colorIndex;
    
    public SimpleMouseExample() {
        frameRate = new FrameRate();
    }
    
    protected void createAndShowGUI() {
        Canvas canvas = new Canvas();
        canvas.setSize( 640, 480 );
        canvas.setBackground( Color.WHITE );
        canvas.setIgnoreRepaint( true );
        getContentPane().add( canvas );
        setTitle( "Paintop só aprendendo como q funciona esse lixo" );
        setIgnoreRepaint( true );
        pack();
        // Add key listeners
        
        // Add mouse listeners
        mouse = new SimpleMouseInput();
        canvas.addMouseListener( mouse );
        canvas.addMouseMotionListener( mouse ); //Handling Mouse Input
        canvas.addMouseWheelListener( mouse );
        setVisible( true );
        canvas.createBufferStrategy( 2 );
        bs = canvas.getBufferStrategy();
        canvas.requestFocus();
        gameThread = new Thread( this );
        gameThread.start();
    }
    public void run() {
        running = true;
        frameRate.initialize();
        while( running ) {
            gameLoop();
        }
    }
    private void gameLoop() {
        processInput();
        renderFrame();
        sleep( 10L );
    }
    private void renderFrame() {
        do {
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
                    g.clearRect( 0, 0, getWidth(), getHeight() );
                    render( g );
                } finally {
                    if( g != null ) {
                    g.dispose();
                    }
                }
            } while( bs.contentsRestored() );
            bs.show();
        } while( bs.contentsLost() );
    }
    
    private void sleep( long sleep ) {
        try {
            Thread.sleep( sleep );
        } catch( InterruptedException ex ) { }
    }
    
    private void processInput() {
    mouse.poll();
    // if button is pressed for first time,
    // start drawing lines
        if( mouse.buttonDownOnce( MouseEvent.BUTTON1 ) ) {
            drawingLine = true;
        }
        // if the button is down, add line point
        if( mouse.buttonDown( MouseEvent.BUTTON1 ) ) {
            lines.add( mouse.getPosition() );
        // if the button is not down but we were drawing,
        // add a null to break up the lines
        } else if( drawingLine ) {
            lines.add( null );
            drawingLine = false;
        }
    // if ’C’ is down, clear the lines
    
    }
    private void render( Graphics g ) {
        colorIndex += mouse.getNotches();
        Color color = COLORS[ Math.abs( colorIndex % COLORS.length ) ];
        g.setColor( color );
        frameRate.calculate();
        g.drawString( frameRate.getFrameRate(), 30, 30 );
        g.drawString( mouse.getPosition().toString(), 30, 45 );
        for( int i = 0; i < lines.size() - 1; ++i ) {
            Point p1 = lines.get( i );
            Point p2 = lines.get( i + 1 );
            // Adding a null into the list is used
            // for breaking up the lines whenRelative Mouse Movement
            // there are two or more lines
            // that are not connected
            if( !( p1 == null || p2 == null ) )
                g.drawLine( p1.x, p1.y, p2.x, p2.y );
                //g.drawRect(p1.x, p1.y, p2.x, p1.y);
        }
    }
    
    protected void onWindowClosing() {
        try {
            running = false;
            gameThread.join();
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
        System.exit( 0 );
    }
    
    public static void main( String[] args ) {
        final SimpleMouseExample app = new SimpleMouseExample();
        
        app.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                app.onWindowClosing();
            }
        });
        
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                app.createAndShowGUI();
            }
        });
    }
}