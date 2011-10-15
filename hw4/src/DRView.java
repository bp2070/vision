package vision.hw4;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

class DRView {

    public static int[][] image;
    private static int SIZE = 140;
    public static JTextField filename_field;
    public static JLabel image_label;
    public static JFrame frame;
    public static JPanel panel;

    public DRView(int[][] image) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {}
        this.image = image;
    }

    public void setup() {
        frame = new JFrame();
        filename_field = new JTextField(20);
        filename_field.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
                if(ke.getKeyChar() == KeyEvent.VK_ENTER) {
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
        erode_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                image = DigitRecognizer.erode(image);
                updateImage(image);
            }
        });

        JButton dilate_btn = new JButton("Dilate");
        dilate_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                image = DigitRecognizer.dilate(image);
                updateImage(image);
            }
        });

        JButton open_btn = new JButton("Open");
        open_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                image = DigitRecognizer.open(image);
                updateImage(image);
            }
        });

        JButton close_btn = new JButton("Close");
        close_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                image = DigitRecognizer.close(image);
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
                                                    .addComponent(open_btn)
                                                    .addComponent(close_btn)
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
                                                  .addComponent(open_btn)
                                                  .addComponent(close_btn)
                                                   )
                                         )
                               );

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }


    public void loadImage() {
        try {
            image = DigitRecognizer.read(filename_field.getText());
            updateImage(image);
            frame.pack();
        } catch(IOException ioe) {}
    }

    public void updateImage(int[][] image) {
        try {
            BufferedImage bufferedImage = getBufferedImage(image);
            ((ImageIcon)image_label.getIcon()).setImage(bufferedImage);
            image_label.updateUI();
        } catch(IOException ioe) {}
    }

    public BufferedImage getBufferedImage(int[][] image) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_BYTE_GRAY);

        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                /* 
                notes on creating buffered image:
                -setRGB is column major order
                -internally background is represented as 0 and foreground as 1
                but when writing to image background is 0xff and foreground is 0
                */
                bufferedImage.setRGB(j, i, (image[i][j] == 1 ? 0 : (byte)0xff));
            }
        }

        return bufferedImage;
    }

}
