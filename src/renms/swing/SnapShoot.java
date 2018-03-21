package renms.swing;

import java.awt.AWTException;  
import java.awt.Color;  
import java.awt.Container;  
import java.awt.Graphics2D;  
import java.awt.Image;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.awt.event.MouseEvent;  
import java.awt.event.MouseMotionListener;  
import java.awt.image.BufferedImage;
import java.nio.Buffer;

import javax.swing.GroupLayout;  
import javax.swing.ImageIcon;  
import javax.swing.JButton;  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JPanel;  
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;  
  
/** 
 * 屏幕截图小程序 
 * @author pengranxiang 
 * 
 */  
public class SnapShoot extends JFrame {  
    /** 
     *  
     */  
    private static final long serialVersionUID = 1L;  
    private JButton snapButton;  
    private JLabel imageLabel;  
      
    private int x, y;   //记录鼠标坐标  
      
    public SnapShoot() {  
        initUI();  
        initLayout();  
        createAction();  
          
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        this.setTitle("截图小工具");  
        this.setLocationRelativeTo(null);   //居中  
        this.setSize(350, 220);
        this.setVisible(true);
        this.setLocation(400, 200);
    }  
      
    JLabel jlb1 = null;
    JLabel jlb2 = null;
    
    JTextField j1 = null;
    JTextField j2 = null;
    private void initUI() {
    	jlb1 = new JLabel("登陆账号:");
    	jlb2 = new JLabel("主账号:");
    	j1 = new JTextField();
    	j2 = new JTextField();
    	
        snapButton = new JButton("开始截图（点右键退出）");  
        imageLabel = new JLabel();  
    }  
      
    private void initLayout() {  
        JPanel pane = new JPanel();  
        pane.add(imageLabel);  
        JScrollPane imgScrollPane = new JScrollPane(pane);  
        imgScrollPane.setSize(200, 100);
        
        Container container = this.getContentPane();  
        GroupLayout layout = new GroupLayout(container);  
        container.setLayout(layout);  
        layout.setAutoCreateContainerGaps(true);  
        layout.setAutoCreateGaps(true);  
          
          
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();  
        hGroup.addGroup(layout.createParallelGroup().addComponent(jlb1)
                .addComponent(jlb2));
        hGroup.addGroup(layout.createParallelGroup().addComponent(j1).addComponent(j2).addComponent(snapButton)  
            .addComponent(imgScrollPane));  
        layout.setHorizontalGroup(hGroup);  
          
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();  
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup().addComponent(jlb1)
                .addComponent(j1));
        vGroup.addGap(5);
        vGroup.addGroup(layout.createParallelGroup().addComponent(jlb2)
                .addComponent(j2));
        vGroup.addGroup(layout.createParallelGroup(Alignment.TRAILING).addComponent(snapButton));
        vGroup.addGroup(layout.createParallelGroup().addComponent(imgScrollPane));  
        layout.setVerticalGroup(vGroup);  
    }  
      
    private void createAction() {  
        snapButton.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
                try {  
                    //开启模拟屏幕，将显示截图的目标组件传入  
                    new ScreenWindow(imageLabel,j1,j2);  
                } catch (AWTException e1) {  
                    e1.printStackTrace();  
                } catch (InterruptedException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        });  
          
        imageLabel.addMouseMotionListener(new MouseMotionListener() {  
            public void mouseDragged(MouseEvent e) {  
                x = e.getX();  
                y = e.getY();
                //鼠标移动时，在imageLabel展示的图像中，绘制点  
                //1. 取得imageLabel中的图像  
                Image img = ((ImageIcon)imageLabel.getIcon()).getImage();  
                  
                //2. 创建一个缓冲图形对象 bi  
                BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
                
                Graphics2D g2d = (Graphics2D) bi.getGraphics();  
                  
                //3. 将截图的原始图像画到 bi  
                g2d.drawImage(img, 0, 0, null);  
                  
                //4. 在鼠标所在的点，画一个点  
                g2d.setColor(Color.RED);    //设置画笔颜色为红色  
                g2d.drawLine(x, y, x, y);   //Java中没有提供点的绘制，使用起点和终点为同一个点的画线代替  
                  
                g2d.dispose();  
                  
                //5. 为了保留每一个点，不能直接使用imageLabel.getGraphics()来画，  
                //需要使用imageLabel.setIcon()来直接将画了点的图像，设置到imageLabel中，  
                //这样，在第一步中，取得img时，就为已经划过上一个点的图像了。  
                ImageIcon ii = new ImageIcon(bi);
                imageLabel.setIcon(ii);  
            }  
  
            
            
            public void mouseMoved(MouseEvent e) {  
                  
            }  
        });  
    }  
  
    public static void main(String[] args) {  
        new SnapShoot();  
    }  
}  