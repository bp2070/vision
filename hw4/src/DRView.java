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
    public static int[][] saved_image;
    private static int SIZE = 128;
    public static JTextField filename_field;
    public static JTextField open_amount;
    public static JTextField close_amount;
    public static JLabel image_label;
    public static JFrame frame;
    public static JPanel panel;

    public DRView() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {}
        this.image = DigitRecognizer.init();
        this.saved_image = DigitRecognizer.init();
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

        JButton save_btn = new JButton("Save Image");
        save_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                saved_image = image;
            }
        });

        JButton center_btn = new JButton("Center Image");
        center_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                image = DigitRecognizer.center(image);
                updateImage(image);
            }
        });

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

        open_amount = new JTextField(20);
        JButton open_btn = new JButton("Open");
        open_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int n = 1;
                try {
                    n = Integer.valueOf(open_amount.getText());
                } catch(Exception e) {}
                image = DigitRecognizer.open(image, n);
                updateImage(image);
            }
        });

        close_amount = new JTextField(20);
        JButton close_btn = new JButton("Close");
        close_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int n = 1;
                try {
                    n = Integer.valueOf(close_amount.getText());
                } catch(Exception e) {}
                image = DigitRecognizer.close(image, n);
                updateImage(image);
            }
        });

        JButton diff_btn = new JButton("Current - Saved");
        diff_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                image = DigitRecognizer.diff(image, saved_image);
                updateImage(image);
            }
        });


        panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup()
                                  .addGroup(layout.createSequentialGroup()
                                            .addComponent(filename_field)
                                            .addComponent(save_btn)
                                           )
                                  .addGap(10)
                                  .addGroup(layout.createSequentialGroup()
                                            .addComponent(image_label)
                                            .addGroup(layout.createParallelGroup()
                                                    .addComponent(center_btn)
                                                    .addComponent(dilate_btn)
                                                    .addComponent(erode_btn)
                                                    .addGroup(layout.createSequentialGroup()
                                                            .addComponent(open_btn)
                                                            .addComponent(open_amount)
                                                             )
                                                    .addGroup(layout.createSequentialGroup()
                                                            .addComponent(close_btn)
                                                            .addComponent(close_amount)
                                                             )
                                                    .addComponent(diff_btn)
                                                     )
                                           )
                                 );

        layout.setVerticalGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup()
                                          .addComponent(filename_field)
                                          .addComponent(save_btn)
                                         )
                                .addGap(10)
                                .addGroup(layout.createParallelGroup()
                                          .addComponent(image_label)
                                          .addGroup(layout.createSequentialGroup()
                                                  .addComponent(center_btn)
                                                  .addComponent(dilate_btn)
                                                  .addComponent(erode_btn)
                                                  .addGroup(layout.createParallelGroup()
                                                          .addComponent(open_btn)
                                                          .addComponent(open_amount)
                                                           )
                                                  .addGroup(layout.createParallelGroup()
                                                          .addComponent(close_btn)
                                                          .addComponent(close_amount)
                                                           )
                                                  .addComponent(diff_btn)
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
