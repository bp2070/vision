import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

class DigitRecognizer{

    private static int SIZE = 140;
    public static JTextField filename_field;
    public static JLabel image_label;
    public static JFrame frame;
    public static JPanel panel;   
    public static int[][] image;
    
    public static void main(String[] args) throws Exception{
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        image = init();
        
        frame = new JFrame();
        filename_field = new JTextField(20);
        filename_field.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent ke) {
                if(ke.getKeyChar() == KeyEvent.VK_ENTER){
                    loadImage();
                }
            }
            @Override
            public void keyPressed(KeyEvent ke) {}
            @Override
            public void keyReleased(KeyEvent ke) {}
            
        });
        image_label = new JLabel(new ImageIcon());
        
//        JButton load_btn = new JButton("Load");
//        load_btn.addActionListener(new ActionListener(){
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                loadImage();                
//            }
//        });             
        
        JButton erode_btn = new JButton("Erode");
        erode_btn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                image = erode(image);
                updateImage(image);
            }
        });
        
        JButton dilate_btn = new JButton("Dilate");
        dilate_btn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                image = dilate(image);
                updateImage(image);
            }
        });
        
        panel = new JPanel();        
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(filename_field)
                    //.addComponent(load_btn)
                )
                .addGap(10)
                .addGroup(layout.createSequentialGroup()                    
                    .addComponent(image_label)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(dilate_btn)
                        .addComponent(erode_btn)
                    )
                )
            );
                
        
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                    .addComponent(filename_field)
                    //.addComponent(load_btn)
                )
                .addGap(10)
                .addGroup(layout.createParallelGroup()
                    .addComponent(image_label)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(dilate_btn)
                        .addComponent(erode_btn)
                    )
                )
            );
        
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void loadImage(){
        try{
            image = read(filename_field.getText());
            updateImage(image);
            frame.pack();
        }catch(IOException ioe){}
    }
    
    public static void updateImage(int[][] image){
        try{
            BufferedImage bufferedImage = getBufferedImage(image);
            ((ImageIcon)image_label.getIcon()).setImage(bufferedImage);
            image_label.updateUI();
        }catch(IOException ioe){}
    }
    
    public static BufferedImage getBufferedImage(int[][] image) throws IOException{
        BufferedImage bufferedImage = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_BYTE_GRAY);
        
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                /* internally background is represented as 0 and foreground as 1
                but when writing to image background is 0xff and foreground is 0 */
                bufferedImage.setRGB(i, j, (image[i][j] == 1 ? 0 : (byte)0xff));
            }
        }
        
        return bufferedImage;
    }
    
    public static int[][] read(String file) throws IOException {
        FileReader inputStream = new FileReader(file);
        int[][] image = init();

        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                /* internally background is represented as 0 and foreground as 1
                but when reading from raw image background is 255 and foreground is 0 */
                image[i][j] = (inputStream.read() == 0) ? 1 : 0;
            }
        }
        inputStream.close();
        return image;
    }

    
    public static int[][] init() {
        int[][] new_image = new int[SIZE][SIZE];
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                new_image[i][j] = 0;
            }
        }
        return new_image;
    }
    
    public static int[][] dilate(int[][] image) {
        int[][] dilated_image = init();

        for(int i = 1; i < SIZE-1; i++) {
            for(int j = 1; j < SIZE-1; j++) {
                if(     image[i][j]   == 1 ||
                        image[i-1][j] == 1 ||
                        image[i+1][j] == 1 ||
                        image[i][j-1] == 1 ||
                        image[i][j+1] == 1) {
                    dilated_image[i][j] = 1;
                }
            }
        }

        return dilated_image;
    }

    public static int[][] erode(int[][] image) {
        int[][] eroded_image = init();

        for(int i = 1; i < SIZE-1; i++) {
            for(int j = 1; j < SIZE-1; j++) {
                if(     image[i][j]   == 1 &&
                        image[i-1][j] == 1 &&
                        image[i+1][j] == 1 &&
                        image[i][j-1] == 1 &&
                        image[i][j+1] == 1) {
                    eroded_image[i][j] = 1;
                }
            }
        }

        return eroded_image;
    }

}


